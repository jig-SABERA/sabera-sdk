package jp.jig.glasses.sample.kmp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import app.jigglass.glass.GlassClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

private const val TAG = "AsciiVideoScreen"

/** かわくだりの盤面と同じ大きさ。汎用テキスト表示ページに収まる文字数 */
private const val ASCII_COLUMNS = 20
private const val ASCII_ROWS = 8

/** 1コマの表示時間。送信が追いつく範囲で選ぶ */
private const val FRAME_INTERVAL_MS = 200L

/** 変換の待ち時間と端末のメモリを抑えるための上限。200コマで40秒ぶん */
private const val MAX_FRAMES = 200

/** 暗いほど左。グラスは自発光なので、明るい画素を濃い文字にする */
private const val RAMP = " .:-=+*#%@"

/**
 * 動画をアスキーアートにして流す画面。
 * 送り方はかわくだりと同じで、1コマずつ汎用テキスト表示ページを置き換える。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsciiVideoScreen(client: GlassClient, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val commandManager = remember(client) { client.createCommandManager() }

    var frames by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(commandManager) {
        onDispose { commandManager.enterHomePage() }
    }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        playing = false
        loading = true
        error = null
        frames = emptyList()
        // デコードは重いので、選んだ時点でまとめて文字に変換しておく
        scope.launch {
            try {
                frames = decodeAsciiFrames(context, uri)
            } catch (e: Throwable) {
                Log.e(TAG, "decodeAsciiFrames failed", e)
                error = e.message
            } finally {
                loading = false
            }
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
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly),
                    )
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (loading) "変換中..." else "動画を選ぶ")
            }
            Spacer(Modifier.height(8.dp))
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

private suspend fun decodeAsciiFrames(context: Context, uri: Uri): List<String> = withContext(Dispatchers.IO) {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, uri)
        val durationMs = retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull() ?: 0L
        val count = min(MAX_FRAMES, (durationMs / FRAME_INTERVAL_MS).toInt())
        // 小さく取り出せばデコードも縮小もファームに任せず済む
        (0 until count).mapNotNull { index ->
            retriever.getScaledFrameAtTime(
                index * FRAME_INTERVAL_MS * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                ASCII_COLUMNS,
                ASCII_ROWS,
            )?.toAsciiFrame()
        }
    } finally {
        retriever.release()
    }
}

private fun Bitmap.toAsciiFrame(): String =
    (0 until height).joinToString("\n") { y ->
        buildString {
            for (x in 0 until width) {
                val color = getPixel(x, y)
                val luminance = 0.299 * Color.red(color) +
                    0.587 * Color.green(color) +
                    0.114 * Color.blue(color)
                append(RAMP[luminance.toInt().coerceIn(0, 255) * (RAMP.length - 1) / 255])
            }
        }.trimEnd()
    }
