package jp.jig.glasses.sample.kmp.ui

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import app.jigglass.glass.GestureType
import app.jigglass.glass.GlassClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

private const val COLUMNS = 20
private const val ROWS = 8
private const val SHIP_ROW = 3
private const val TICK_MS = 500L

// ぶつかった盤面を見せてからリザルトへ切り替えるまで
private const val RESULT_DELAY_MS = 1000L

// 端まで首を回す量。IchigoJam の左右キーの代わりにヨーで動かす
private const val EDGE_YAW_DEGREES = 90f

private const val SHIP = 'Ｏ'
private const val ROCK = '＊'
private const val WATER = '\u3000'

private const val WAITING_TEXT = "ＫＡＷＡＫＵＤＡＲＩ\nタップ　ノーマル\n長押し　ハード"

private enum class Mode(val label: String, val rocksPerRow: Int) {
    NORMAL("ノーマル", 1),
    HARD("ハード", 2),
}

/**
 * IchigoJam のかわくだりを 6DoF で遊ぶ画面。
 * 自機はヨー、画面は汎用テキスト表示ページへ毎ティック送り直す。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KawakudariScreen(client: GlassClient, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val commandManager = remember(client) { client.createCommandManager() }
    val yaw = remember { MutableStateFlow<Float?>(null) }

    var playing by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf(Mode.NORMAL) }
    var score by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var field by remember { mutableStateOf("") }

    DisposableEffect(commandManager) {
        val jobs = listOf(
            scope.launch {
                commandManager.imuData.collect { yaw.value = it.yawDegrees }
            },
            scope.launch {
                commandManager.gestureEvents.collect { gesture ->
                    if (playing) return@collect
                    when (gesture) {
                        GestureType.SINGLE_TAP -> mode = Mode.NORMAL
                        GestureType.HOLD -> mode = Mode.HARD
                        else -> return@collect
                    }
                    playing = true
                }
            },
        )
        commandManager.startImuData()
        commandManager.enterEmptyScreenPage()
        commandManager.sendEmptyScreenContent(WAITING_TEXT)
        onDispose {
            jobs.forEach { it.cancel() }
            commandManager.stopImuData()
            commandManager.enterHomePage()
        }
    }

    LaunchedEffect(playing, mode) {
        if (!playing) return@LaunchedEffect

        score = 0
        gameOver = false
        // 遊び始めた向きを中央にする。ヨーは磁力計が無くて絶対値が使えない
        val centerYaw = yaw.filterNotNull().first()
        val rocks = ArrayDeque(List(ROWS) { emptyList<Int>() })
        // 自機より上の行に残る軌跡。岩と一緒に上へ流れる
        val trail = ArrayDeque(List(SHIP_ROW) { NONE })
        commandManager.enterEmptyScreenPage()

        while (isActive) {
            val shipX = shipColumn(yaw.value ?: centerYaw, centerYaw)
            // 1つ下の岩は次のスクロールで自機に重なる。ぶつかる直前の盤面を見せたいので先に判定する
            val hit = shipX in rocks.elementAt(SHIP_ROW + 1)

            field = render(rocks, trail, shipX, score)
            commandManager.sendEmptyScreenContent(field)

            if (hit) {
                gameOver = true
                delay(RESULT_DELAY_MS)
                commandManager.sendEmptyScreenContent("ＧＡＭＥ　ＯＶＥＲ\nＳＣＯＲＥ${toFullWidth(score)}")
                // playing を倒すとこのコルーチンが消えるので、送り終えてから
                playing = false
                return@LaunchedEffect
            }

            score++
            rocks.removeFirst()
            rocks.addLast(List(mode.rocksPerRow) { Random.nextInt(COLUMNS) })
            trail.removeFirst()
            trail.addLast(shipX)
            delay(TICK_MS)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("かわくだり") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "首を回して岩「$ROCK」をよける。まっすぐ前が中央で、" +
                    "${EDGE_YAW_DEGREES.toInt()}度回すと端に着く。" +
                    "グラスのタップでノーマル、長押しでハードが始まる。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            if (playing) {
                Button(
                    onClick = { playing = false },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("やめる")
                }
            } else {
                Mode.entries.forEach { entry ->
                    Button(
                        onClick = {
                            mode = entry
                            playing = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("${entry.label}ではじめる")
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (gameOver) "GAME OVER / ${mode.label} / SCORE $score" else "${mode.label} / SCORE $score",
                style = MaterialTheme.typography.bodyMedium,
            )

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            Text(
                field.ifEmpty { "まだ始まっていない" },
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
            )

            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("戻る")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private const val NONE = -1

private fun shipColumn(currentYaw: Float, centerYaw: Float): Int {
    val half = (COLUMNS - 1) / 2f
    // ヨーは右を向くと減る向きなので反転させる
    val offset = -normalizeDegrees(currentYaw - centerYaw) / EDGE_YAW_DEGREES * half
    return (half + offset).roundToInt().coerceIn(0, COLUMNS - 1)
}

private fun toFullWidth(value: Int): String =
    value.toString().map { (it.code + 0xFEE0).toChar() }.joinToString("")

private fun normalizeDegrees(degrees: Float): Float {
    var value = degrees
    while (value > 180f) value -= 360f
    while (value < -180f) value += 360f
    return value
}

private fun render(rocks: List<List<Int>>, trail: List<Int>, shipX: Int, score: Int): String =
    rocks.withIndex().joinToString("\n") { (row, rocksInRow) ->
        val line = CharArray(COLUMNS) { WATER }
        rocksInRow.forEach { line[it] = ROCK }
        val ship = if (row == SHIP_ROW) shipX else trail.getOrElse(row) { NONE }
        if (ship in 0 until COLUMNS) line[ship] = SHIP
        // 最上段は上へ流れて消える行なので、岩に重ねてスコアを置く
        if (row == 0) toFullWidth(score).forEachIndexed { i, digit -> line[i] = digit }
        String(line).trimEnd()
    }
