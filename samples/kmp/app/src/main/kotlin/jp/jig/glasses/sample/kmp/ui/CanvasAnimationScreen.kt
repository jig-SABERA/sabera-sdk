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

private const val TAG = "CanvasAnimationScreen"

/** 4:3 の 128x96。380,000バイトのアリーナを w*h*2 で割ると15枚ぶんのリングになる */
private const val FRAME_WIDTH = 128
private const val FRAME_HEIGHT = 96
private const val PIXEL_COUNT = FRAME_WIDTH * FRAME_HEIGHT
private const val PACKED_FRAME_BYTES = PIXEL_COUNT / 8

/** キャンバス 576x360 の中央に置く */
private const val FRAME_X = (576 - FRAME_WIDTH) / 2
private const val FRAME_Y = (360 - FRAME_HEIGHT) / 2

/** 宣言する再生間隔。グラスはこの間隔でリングからフレームを取り出す */
private const val INTERVAL_MS = 100

private const val BUNDLED_ASSET = "badapple128.bin"

/**
 * キャンバスのアニメーションで動画を流す画面。
 * FEATURE_VERSION 2.3.0 で入った ANIM_START / ANIM_FRAME / ANIM_STOP を通す。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasAnimationScreen(client: GlassClient, onBack: () -> Unit) {
    val context = LocalContext.current
    val commandManager = remember(client) { client.createCommandManager() }

    var frames by remember { mutableStateOf<List<ByteArray>>(emptyList()) }
    var playing by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(0) }
    var preview by remember { mutableStateOf<GrayscaleImage?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(commandManager) {
        onDispose {
            commandManager.stopCanvasAnimation()
            commandManager.closeCanvas()
        }
    }

    LaunchedEffect(Unit) {
        try {
            frames = withContext(Dispatchers.IO) { loadFrames(context) }
        } catch (e: Throwable) {
            Log.e(TAG, "loadFrames failed", e)
            error = "同梱データを読めなかった: ${e.message}"
        }
    }

    LaunchedEffect(playing) {
        if (!playing || frames.isEmpty()) return@LaunchedEffect

        commandManager.startCanvasAnimation(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT, INTERVAL_MS)
        try {
            while (isActive) {
                val pixels = unpackBits(frames[position % frames.size], PIXEL_COUNT)
                preview = GrayscaleImage(FRAME_WIDTH, FRAME_HEIGHT, pixels)
                commandManager.sendCanvasAnimationFrame(FRAME_WIDTH, FRAME_HEIGHT, pixels)
                position++
                delay(INTERVAL_MS.toLong())
            }
        } finally {
            commandManager.stopCanvasAnimation()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("キャンバスに動画を流す") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "${FRAME_WIDTH}×$FRAME_HEIGHT を interval=$INTERVAL_MS ms で宣言して、" +
                    "1コマずつ送り続ける。FEATURE_VERSION 2.3.0 以上のファームが対象。",
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
                    "${frames.size} コマ / 送った数 $position"
                },
                style = MaterialTheme.typography.bodySmall,
            )

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            Text("流している間に割り込ませて挙動を見る", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { commandManager.clearCanvas() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("clearCanvas を送る")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    // アリーナ共用なので、静的ビットマップを送るとアニメは止まる
                    val pattern = testPatternImage(96)
                    commandManager.sendCanvasImage(
                        id = 0,
                        x = 16,
                        y = 16,
                        width = pattern.width,
                        height = pattern.height,
                        grayscale = pattern.pixels,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("静的な画像を1枚置く")
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            preview?.let {
                Image(
                    bitmap = it.toBitmap().asImageBitmap(),
                    contentDescription = "いま送っているコマ",
                    modifier = Modifier.size(256.dp, 192.dp),
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

private fun loadFrames(context: Context): List<ByteArray> {
    val bytes = context.assets.open(BUNDLED_ASSET).use { it.readBytes() }
    return (0 until bytes.size / PACKED_FRAME_BYTES).map { index ->
        bytes.copyOfRange(index * PACKED_FRAME_BYTES, (index + 1) * PACKED_FRAME_BYTES)
    }
}
