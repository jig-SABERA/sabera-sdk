package jp.jig.glasses.sample.kmp.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import app.jigglass.glass.CommandManager
import app.jigglass.glass.GestureType
import app.jigglass.glass.GlassClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** アイコンの一辺。96角6枚でもグラスの画像バッファに収まる */
private const val ICON_SIZE = 96
private const val ICON_GAP = 48
private const val ICON_ROW_GAP = 16
private const val ICON_TOP_Y = 16

/** 3列2段。画像は id 0..7 なので6枚まで置ける */
private const val GRID_COLUMNS = 3
private const val GRID_ROWS = 2

/** 説明テキストの矩形。アイコンの下の余りに置く */
private const val TEXT_X = 48
private const val TEXT_WIDTH = 480
private const val TEXT_HEIGHT = 112

private const val CANVAS_WIDTH = 576

/** 隣の列へ移るまでの振り向き量 */
private const val STEP_DEGREES = 20f

/** 下の段へ移るまでのうなずき量。ピッチは上向きが負 */
private const val ROW_STEP_DEGREES = 15f

/** ヨーとピッチを見に行く間隔。変わったときだけ送るので短くても送信は増えない */
private const val CURSOR_POLL_MS = 100L

/** ランチャーに並べる機能。アイコンは絵柄の代わりに漢字1字で描く */
enum class LauncherItem(val label: String, val glyph: String, val description: String) {
    KAWAKUDARI("かわくだり", "川", "首を振って岩をよけるゲーム。タップでノーマル、長押しでハード。"),
    TELEPROMPTER("テレプロンプター", "読", "原稿を送って、行を進めながら読み上げる。"),
    TRANSLATE("翻訳", "訳", "話した言葉をその場で訳して出す。"),
    AI_CHAT("AI アシスタント", "問", "聞いたことに答えを返す。"),
    IMAGE("画像を送る", "絵", "選んだ写真をグレースケールにして出す。"),
    IMU("6DoF を見る", "角", "加速度・角速度・ピッチ・ヨーの生の値を並べる。"),
}

/**
 * 6DoF でカーソルを動かすランチャー。
 * アイコンはキャンバスの画像、説明はテキスト要素で、選択はグラスのシングルタップ。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherScreen(client: GlassClient, onSelect: (LauncherItem) -> Unit, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val commandManager = remember(client) { client.createCommandManager() }
    val imu = remember { MutableStateFlow<CommandManager.ImuData?>(null) }

    val items = remember { LauncherItem.entries }
    // フォーカスの有無で差し替えるので、2状態とも先に作っておく
    val icons = remember { items.map { iconImage(it.glyph, false) to iconImage(it.glyph, true) } }

    var focused by remember { mutableStateOf(0) }

    DisposableEffect(commandManager) {
        val jobs = listOf(
            scope.launch {
                commandManager.imuData.collect { imu.value = it }
            },
            scope.launch {
                commandManager.gestureEvents.collect { gesture ->
                    if (gesture == GestureType.SINGLE_TAP) onSelect(items[focused])
                }
            },
        )
        commandManager.startImuData()
        onDispose {
            jobs.forEach { it.cancel() }
            commandManager.stopImuData()
            commandManager.closeCanvas()
        }
    }

    LaunchedEffect(Unit) {
        // 画面に入った向きを上段の中央に合わせる
        val center = imu.filterNotNull().first()

        commandManager.sendCanvas(listOf(descriptionElement(items[focused])))
        items.indices.forEach { index ->
            commandManager.sendCanvasImage(
                id = index,
                x = iconX(index),
                y = iconY(index),
                width = ICON_SIZE,
                height = ICON_SIZE,
                grayscale = icons[index].let { if (index == focused) it.second else it.first }.pixels,
            )
        }

        while (isActive) {
            delay(CURSOR_POLL_MS)
            val next = cursorIndex(imu.value ?: center, center, items.size)
            if (next == focused) continue

            val previous = focused
            focused = next
            commandManager.sendCanvasImage(
                id = previous,
                x = iconX(previous),
                y = iconY(previous),
                width = ICON_SIZE,
                height = ICON_SIZE,
                grayscale = icons[previous].first.pixels,
            )
            commandManager.sendCanvasImage(
                id = next,
                x = iconX(next),
                y = iconY(next),
                width = ICON_SIZE,
                height = ICON_SIZE,
                grayscale = icons[next].second.pixels,
            )
            commandManager.sendCanvasElements(listOf(descriptionElement(items[next])))
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("ランチャー") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "首を振ってカーソルを動かし、グラスのシングルタップで選ぶ。" +
                    "隣の列まで${STEP_DEGREES.toInt()}度、下の段までうなずき${ROW_STEP_DEGREES.toInt()}度。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            (0 until GRID_ROWS).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    (0 until GRID_COLUMNS).forEach { column ->
                        val index = row * GRID_COLUMNS + column
                        val item = items.getOrNull(index) ?: return@forEach
                        val icon = icons[index].let { if (index == focused) it.second else it.first }
                        Image(
                            bitmap = icon.toBitmap().asImageBitmap(),
                            contentDescription = item.label,
                            modifier = Modifier.size(72.dp),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(8.dp))
            Text(items[focused].label, style = MaterialTheme.typography.titleMedium)
            Text(items[focused].description, style = MaterialTheme.typography.bodyMedium)

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            OutlinedButton(
                onClick = { onSelect(items[focused]) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("${items[focused].label}をひらく")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("戻る")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun descriptionElement(item: LauncherItem) = CommandManager.CanvasElement(
    id = 0,
    x = TEXT_X,
    y = ICON_TOP_Y + GRID_ROWS * (ICON_SIZE + ICON_ROW_GAP),
    width = TEXT_WIDTH,
    height = TEXT_HEIGHT,
    text = "${item.label}\n${item.description}",
)

private fun iconX(index: Int): Int {
    val row = GRID_COLUMNS * ICON_SIZE + (GRID_COLUMNS - 1) * ICON_GAP
    return (CANVAS_WIDTH - row) / 2 + (index % GRID_COLUMNS) * (ICON_SIZE + ICON_GAP)
}

private fun iconY(index: Int): Int =
    ICON_TOP_Y + (index / GRID_COLUMNS) * (ICON_SIZE + ICON_ROW_GAP)

private fun cursorIndex(current: CommandManager.ImuData, center: CommandManager.ImuData, count: Int): Int {
    val steps = (-normalizeDegrees(current.yawDegrees - center.yawDegrees) / STEP_DEGREES).roundToInt()
    // 端で止めずに反対側へ回り込む。mod は負でも 0..GRID_COLUMNS-1 を返す
    val column = (GRID_COLUMNS / 2 + steps).mod(GRID_COLUMNS)
    val row = if (current.pitchDegrees - center.pitchDegrees > ROW_STEP_DEGREES) 1 else 0
    return (row * GRID_COLUMNS + column).coerceAtMost(count - 1)
}

/**
 * アイコンを2値で描く。グラスは3bitに量子化するので、中間の階調は残さない。
 * フォーカスは枠の太さと白黒反転で、絵柄そのものに含める。
 */
private fun iconImage(glyph: String, focused: Boolean, size: Int = ICON_SIZE): GrayscaleImage {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val ink = if (focused) Color.BLACK else Color.WHITE
    canvas.drawColor(if (focused) Color.WHITE else Color.BLACK)

    val border = Paint().apply {
        color = ink
        style = Paint.Style.STROKE
        strokeWidth = if (focused) 8f else 3f
        isAntiAlias = false
    }
    val inset = border.strokeWidth / 2
    canvas.drawRect(inset, inset, size - inset, size - inset, border)

    val text = Paint().apply {
        color = ink
        textSize = size * 0.55f
        textAlign = Paint.Align.CENTER
        isAntiAlias = false
    }
    canvas.drawText(glyph, size / 2f, size / 2f - (text.descent() + text.ascent()) / 2f, text)

    val colors = IntArray(size * size)
    bitmap.getPixels(colors, 0, size, 0, 0, size, size)
    // 2値にしておけばRLEがよく効いて、差し替えの転送も短くなる
    val pixels = ByteArray(colors.size) { index ->
        if (Color.red(colors[index]) >= 128) 0xFF.toByte() else 0x00
    }
    return GrayscaleImage(size, size, pixels)
}
