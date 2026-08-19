package jp.jig.glasses.sample.kmp.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.jigglass.glass.GlassClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.sqrt

private const val TAG = "MicScreen"

/** PCM16 モノラル 16kHz。1秒ぶんのバイト数 */
private const val BYTES_PER_SECOND = 16_000 * 2

/** 16bit の最大値。レベルの正規化に使う */
private const val PCM_FULL_SCALE = 32_768f

/**
 * マイクの音声を受け取る画面。
 * 音そのものより、届いているか・途切れていないかを見たいので、
 * 経過秒に対する受信バイト数と直近の音量を出す。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicScreen(client: GlassClient, onBack: () -> Unit) {
    val commandManager = remember(client) { client.createCommandManager() }
    val scope = rememberCoroutineScope()

    val streaming by commandManager.micStreaming.collectAsState()
    var totalBytes by remember { mutableStateOf(0L) }
    var chunkCount by remember { mutableStateOf(0) }
    var lastChunkBytes by remember { mutableStateOf(0) }
    var level by remember { mutableStateOf(0f) }
    var peak by remember { mutableStateOf(0f) }
    var elapsedMs by remember { mutableStateOf(0L) }
    var error by remember { mutableStateOf<String?>(null) }

    var startedAtMs by remember { mutableStateOf(0L) }

    DisposableEffect(commandManager) {
        val job: Job = scope.launch {
            commandManager.micAudio.collect { pcm ->
                totalBytes += pcm.size
                chunkCount += 1
                lastChunkBytes = pcm.size
                level = pcm.rms()
                peak = maxOf(peak, level)
                elapsedMs = System.currentTimeMillis() - startedAtMs
            }
        }
        onDispose { job.cancel() }
    }

    fun safeRun(label: String, block: () -> Unit) {
        Log.d(TAG, "safeRun: $label")
        try {
            block()
        } catch (e: Throwable) {
            Log.e(TAG, "safeRun: $label FAILED", e)
            error = e.message
        }
    }

    // 期待は毎秒 32000 バイト。下回っていれば取りこぼしている
    val bytesPerSecond = if (elapsedMs > 0) totalBytes * 1000 / elapsedMs else 0

    Scaffold(
        topBar = { TopAppBar(title = { Text("マイク") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "PCM16 リトルエンディアン、16kHz モノラル。デコードは SDK 側で済んでいる。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            Text(if (streaming) "受信中" else "停止中", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LevelBar(level)
            Spacer(Modifier.height(4.dp))
            Text("音量 ${(level * 100).toInt()}% / ピーク ${(peak * 100).toInt()}%")

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            Text("受信 $totalBytes バイト（$chunkCount 回）")
            Text("直近のかたまり $lastChunkBytes バイト")
            Text("毎秒 $bytesPerSecond / $BYTES_PER_SECOND バイト")
            Text(
                "毎秒のバイト数が足りないときは取りこぼしている",
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    totalBytes = 0
                    chunkCount = 0
                    peak = 0f
                    startedAtMs = System.currentTimeMillis()
                    elapsedMs = 0
                    safeRun("startMicStreaming") { commandManager.startMicStreaming() }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("受信を始める")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { safeRun("stopMicStreaming") { commandManager.stopMicStreaming() } },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("受信を止める")
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    commandManager.stopMicStreaming()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("戻る")
            }

            error?.let { message ->
                Spacer(Modifier.height(16.dp))
                Text(message, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { error = null }) {
                    Text("エラーを消す")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LevelBar(level: Float) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            Modifier
                .fillMaxWidth(level.coerceIn(0f, 1f))
                .height(24.dp)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

/** PCM16 リトルエンディアンの実効値。0..1 に正規化する */
private fun ByteArray.rms(): Float {
    if (size < 2) return 0f
    var sum = 0.0
    var i = 0
    while (i + 1 < size) {
        val sample = ((this[i + 1].toInt() shl 8) or (this[i].toInt() and 0xFF)).toShort()
        sum += sample.toDouble() * sample.toDouble()
        i += 2
    }
    return (sqrt(sum / (size / 2)) / PCM_FULL_SCALE).toFloat()
}
