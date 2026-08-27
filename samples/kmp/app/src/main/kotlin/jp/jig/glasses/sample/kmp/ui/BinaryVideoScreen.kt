package jp.jig.glasses.sample.kmp.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.jigglass.glass.GlassClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

private const val TAG = "BinaryVideoScreen"

/** 4:3 の 96x72。196角までは送れるが、小さいほど1コマの転送が短い */
private const val FRAME_WIDTH = 96
private const val FRAME_HEIGHT = 72

/** 1画素1bitに詰めた1コマの大きさ */
private const val PACKED_FRAME_BYTES = FRAME_WIDTH * FRAME_HEIGHT / 8

/** 1コマの表示時間。画像は数百バイトずつ分割して送られるので文字より遅い */
private const val FRAME_INTERVAL_MS = 200L

private const val BUNDLED_ASSET = "badapple.bin"

/**
 * 2値の画像をそのまま流す画面。
 * アスキーアート版と同じ映像を、文字ではなく画像表示ページへ送る。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinaryVideoScreen(client: GlassClient, onBack: () -> Unit) {
    val context = LocalContext.current
    val commandManager = remember(client) { client.createCommandManager() }

    var frames by remember { mutableStateOf<List<ByteArray>>(emptyList()) }
    var playing by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(0) }
    var preview by remember { mutableStateOf<GrayscaleImage?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(commandManager) {
        onDispose { commandManager.enterHomePage() }
    }

    LaunchedEffect(Unit) {
        try {
            frames = withContext(Dispatchers.IO) { loadPackedFrames(context) }
        } catch (e: Throwable) {
            Log.e(TAG, "loadPackedFrames failed", e)
            error = "同梱データを読めなかった: ${e.message}"
        }
    }

    LaunchedEffect(playing) {
        if (!playing || frames.isEmpty()) return@LaunchedEffect

        commandManager.enterImageDisplayPage()
        while (isActive) {
            val pixels = unpack(frames[position % frames.size])
            preview = GrayscaleImage(FRAME_WIDTH, FRAME_HEIGHT, pixels)
            commandManager.sendImage(FRAME_WIDTH, FRAME_HEIGHT, pixels)
            position++
            delay(FRAME_INTERVAL_MS)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("2値画像を流す") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "同梱の1bit画像を ${FRAME_WIDTH}×$FRAME_HEIGHT のまま画像表示ページへ送る。" +
                    "1コマ $PACKED_FRAME_BYTES バイトを展開して ${FRAME_INTERVAL_MS}ms ごとに1枚。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

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
                if (frames.isEmpty()) {
                    "読み込み中"
                } else {
                    "${frames.size} コマ / 再生位置 ${position % frames.size}"
                },
                style = MaterialTheme.typography.bodySmall,
            )

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            preview?.let {
                Image(
                    bitmap = it.toBitmap().asImageBitmap(),
                    contentDescription = "いま送っているコマ",
                    modifier = Modifier.size(288.dp, 216.dp),
                )
            }

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

private fun loadPackedFrames(context: Context): List<ByteArray> {
    val bytes = context.assets.open(BUNDLED_ASSET).use { it.readBytes() }
    return (0 until bytes.size / PACKED_FRAME_BYTES).map { index ->
        bytes.copyOfRange(index * PACKED_FRAME_BYTES, (index + 1) * PACKED_FRAME_BYTES)
    }
}

/** 1bitずつ詰めたコマを、SDKが受け取る1画素1バイトへ戻す */
private fun unpack(frame: ByteArray): ByteArray {
    val pixels = ByteArray(FRAME_WIDTH * FRAME_HEIGHT)
    pixels.indices.forEach { index ->
        val bit = (frame[index / 8].toInt() shr (7 - index % 8)) and 1
        pixels[index] = if (bit == 1) 0xFF.toByte() else 0
    }
    return pixels
}
