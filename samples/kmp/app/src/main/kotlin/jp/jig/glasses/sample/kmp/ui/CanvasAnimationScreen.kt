package jp.jig.glasses.sample.kmp.ui

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
import androidx.compose.material3.Slider
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
import kotlin.math.roundToInt

private const val TAG = "CanvasAnimationScreen"

private const val CANVAS_WIDTH = 576
private const val CANVAS_HEIGHT = 360

/**
 * 選べる寸法。4:3 のまま、380,000バイトのアリーナに w*h*2 が2枚入る範囲で並べた。
 * 320x240 が上限で、これ以上大きくするとリングが1枚になって再生が始まらない。
 */
private val FRAME_SIZES = listOf(
    80 to 60,
    128 to 96,
    160 to 120,
    200 to 150,
    240 to 180,
    280 to 210,
    BAD_APPLE_WIDTH to BAD_APPLE_HEIGHT,
)

/** 宣言する再生間隔の初期値 */
private const val INTERVAL_DEFAULT_MS = 200

/** スライダーで動かせる範囲。1コマが大きいと転送に時間がかかるので上を広く取る */
private const val INTERVAL_MIN_MS = 50f
private const val INTERVAL_MAX_MS = 1000f
private const val INTERVAL_STEPS = 0

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

    // ドラッグ中に宣言し直すと止まって見えるので、離してから反映する
    var intervalMs by remember { mutableStateOf(INTERVAL_DEFAULT_MS) }
    var intervalSlider by remember { mutableStateOf(INTERVAL_DEFAULT_MS.toFloat()) }
    var sizeIndex by remember { mutableStateOf(FRAME_SIZES.lastIndex) }
    var sizeSlider by remember { mutableStateOf(FRAME_SIZES.lastIndex.toFloat()) }

    DisposableEffect(commandManager) {
        onDispose {
            commandManager.stopCanvasAnimation()
            commandManager.closeCanvas()
        }
    }

    LaunchedEffect(Unit) {
        try {
            frames = withContext(Dispatchers.IO) { loadBadAppleFrames(context) }
        } catch (e: Throwable) {
            Log.e(TAG, "loadBadAppleFrames failed", e)
            error = "同梱データを読めなかった: ${e.message}"
        }
    }

    LaunchedEffect(playing, intervalMs, sizeIndex) {
        if (!playing || frames.isEmpty()) return@LaunchedEffect

        val (width, height) = FRAME_SIZES[sizeIndex]
        // 寸法と間隔はANIM_STARTで宣言するので、変えたら送り直す
        commandManager.startCanvasAnimation(
            x = (CANVAS_WIDTH - width) / 2,
            y = (CANVAS_HEIGHT - height) / 2,
            width = width,
            height = height,
            intervalMs = intervalMs,
        )
        try {
            while (isActive) {
                val pixels = badAppleFrame(frames[position % frames.size], width, height)
                preview = GrayscaleImage(width, height, pixels)
                commandManager.sendCanvasAnimationFrame(width, height, pixels)
                position++
                delay(intervalMs.toLong())
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
                "4:3 のまま寸法と interval を選んで送り続ける。同梱データは ${BAD_APPLE_WIDTH}×$BAD_APPLE_HEIGHT の5fpsぶんで、" +
                    "小さい寸法は間引いて作る。FEATURE_VERSION 2.3.0 以上のファームが対象。",
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

            val (sliderWidth, sliderHeight) = FRAME_SIZES[sizeSlider.roundToInt()]
            Spacer(Modifier.height(8.dp))
            Text(
                "サイズ = ${sliderWidth}×$sliderHeight" +
                    "（リング ${380_000 / (sliderWidth * sliderHeight * 2)} 枚）",
                style = MaterialTheme.typography.bodyMedium,
            )
            Slider(
                value = sizeSlider,
                onValueChange = { sizeSlider = it },
                onValueChangeFinished = { sizeIndex = sizeSlider.roundToInt() },
                valueRange = 0f..FRAME_SIZES.lastIndex.toFloat(),
                steps = FRAME_SIZES.size - 2,
            )

            Text(
                "interval = ${intervalSlider.roundToInt()} ms" +
                    "（約 ${1000 / intervalSlider.roundToInt()} fps）",
                style = MaterialTheme.typography.bodyMedium,
            )
            Slider(
                value = intervalSlider,
                onValueChange = { intervalSlider = it },
                onValueChangeFinished = { intervalMs = intervalSlider.roundToInt() },
                valueRange = INTERVAL_MIN_MS..INTERVAL_MAX_MS,
                steps = INTERVAL_STEPS,
            )
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
