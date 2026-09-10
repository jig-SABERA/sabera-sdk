package jp.jig.glasses.sample.kmp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import app.jigglass.glass.GlassClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val TAG = "AsciiVideoScreen"

/** かわくだりの盤面と同じ大きさ。汎用テキスト表示ページに収まる文字数 */
private const val ASCII_COLUMNS = 20
private const val ASCII_ROWS = 8

/** 1コマの表示時間。送信が追いつく範囲で選ぶ */
private const val FRAME_INTERVAL_MS = 100L

/** 同梱のアスキーアート。1コマ ASCII_ROWS 行ずつ並べたもの */
private const val BUNDLED_ASSET = "badapple.txt"

/**
 * 動画をアスキーアートにして流す画面。
 * 送り方はかわくだりと同じで、1コマずつ汎用テキスト表示ページを置き換える。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsciiVideoScreen(client: GlassClient, onBack: () -> Unit) {
    val commandManager = remember(client) { client.createCommandManager() }

    var frames by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    // 白地に黒い影の映像は、そのままだと背景が文字で埋まる
    var invert by remember { mutableStateOf(false) }

    DisposableEffect(commandManager) {
        onDispose { commandManager.enterHomePage() }
    }

    val picker = rememberVideoPicker(invert) { result ->
        loading = false
        if (result.exceptionOrNull() is CancellationException) return@rememberVideoPicker
        playing = false
        error = null
        frames = emptyList()
        result.onSuccess { frames = it }.onFailure { e ->
            SampleLog.e(TAG, "decodeAsciiFrames failed", e)
            error = e.message
        }
    }

    LaunchedEffect(playing) {
        if (!playing || frames.isEmpty()) return@LaunchedEffect

        commandManager.enterEmptyScreenPage()
        while (isActive) {
            val frame = frames[position % frames.size]
            current = frame
            commandManager.sendEmptyScreenContent(frame)
            position++
            delay(FRAME_INTERVAL_MS)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("アスキーアートを流す") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "選んだ動画を ${ASCII_COLUMNS}×${ASCII_ROWS} の文字に落として、" +
                    "${FRAME_INTERVAL_MS}ms ごとに送る。Bad Apple!! のような影絵がいちばん形になる。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    playing = false
                    position = 0
                    error = null
                    try {
                        frames = loadBundledFrames()
                    } catch (e: Throwable) {
                        SampleLog.e(TAG, "loadBundledFrames failed", e)
                        error = "同梱データを読めなかった: ${e.message}"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("同梱の Bad Apple を読む")
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    playing = false
                    loading = true
                    picker()
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (loading) "変換中..." else "動画を選ぶ")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = invert, onCheckedChange = { invert = it })
                Spacer(Modifier.height(8.dp))
                Text("明暗を反転して変換する", style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = {
                    playing = !playing
                    if (!playing) position = 0
                },
                enabled = frames.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (playing) "止める" else "流す")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (frames.isEmpty()) "まだ動画がない" else "${frames.size} コマ / 再生位置 ${position % frames.size}",
                style = MaterialTheme.typography.bodySmall,
            )

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            Text(
                current.ifEmpty { frames.firstOrNull() ?: "" },
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
            )

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    playing = false
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("戻る")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun loadBundledFrames(): List<String> {
    val text = readSampleAsset(BUNDLED_ASSET).decodeToString()
    if (text.isEmpty()) return emptyList()
    return text
        .removeSuffix("\n").removeSuffix("\r")
        .lineSequence()
        .chunked(ASCII_ROWS)
        .map { rows -> rows.joinToString("\n") { it.trimEnd() } }
        .toList()
}
