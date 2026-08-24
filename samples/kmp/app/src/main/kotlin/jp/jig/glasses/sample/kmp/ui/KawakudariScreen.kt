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
import app.jigglass.glass.GlassClient
import kotlinx.coroutines.Job
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

// 端まで首を回す量。IchigoJam の左右キーの代わりにヨーで動かす
private const val EDGE_YAW_DEGREES = 90f

private const val SHIP = 'Ｏ'
private const val ROCK = '＊'
private const val WATER = '\u3000'

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
    var score by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var field by remember { mutableStateOf("") }

    DisposableEffect(commandManager) {
        val job: Job = scope.launch {
            commandManager.imuData.collect { yaw.value = it.yawDegrees }
        }
        commandManager.startImuData()
        onDispose {
            job.cancel()
            commandManager.stopImuData()
            commandManager.enterHomePage()
        }
    }

    LaunchedEffect(playing) {
        if (!playing) return@LaunchedEffect

        // 遊び始めた向きを中央にする。ヨーは磁力計が無くて絶対値が使えない
        val centerYaw = yaw.filterNotNull().first()
        val rocks = ArrayDeque(List(ROWS) { NONE })
        // 自機より上の行に残る軌跡。岩と一緒に上へ流れる
        val trail = ArrayDeque(List(SHIP_ROW) { NONE })
        score = 0
        gameOver = false
        commandManager.enterEmptyScreenPage()

        while (isActive) {
            val shipX = shipColumn(yaw.value ?: centerYaw, centerYaw)
            rocks.removeFirst()
            rocks.addLast(Random.nextInt(COLUMNS))

            if (rocks.elementAt(SHIP_ROW) == shipX) {
                gameOver = true
                playing = false
                field = render(rocks, trail, shipX)
                commandManager.sendEmptyScreenContent("ＧＡＭＥ　ＯＶＥＲ\nＳＣＯＲＥ${toFullWidth(score)}")
                return@LaunchedEffect
            }

            score++
            field = render(rocks, trail, shipX)
            commandManager.sendEmptyScreenContent(field)
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
                    "${EDGE_YAW_DEGREES.toInt()}度回すと端に着く。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { playing = !playing },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (playing) "やめる" else if (gameOver) "もう一度" else "はじめる")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (gameOver) "GAME OVER / SCORE $score" else "SCORE $score",
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

private fun render(rocks: List<Int>, trail: List<Int>, shipX: Int): String =
    rocks.withIndex().joinToString("\n") { (row, rock) ->
        val line = CharArray(COLUMNS) { WATER }
        if (rock in 0 until COLUMNS) line[rock] = ROCK
        val ship = if (row == SHIP_ROW) shipX else trail.getOrElse(row) { NONE }
        if (ship in 0 until COLUMNS) line[ship] = SHIP
        String(line).trimEnd()
    }
