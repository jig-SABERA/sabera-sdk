package jp.jig.glasses.sample.kmp.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import app.jigglass.glass.CommandManager
import app.jigglass.glass.GlassClient

private const val TAG = "NaviScreen"

/** ナビ画面のミニマップのサイズ。アプリ本体もこの大きさで送っている */
private const val NAVI_MAP_SIZE = 192

/** 全体ルート用の画像。ミニマップより大きいサイズを送れる */
private const val NAVI_LARGE_MAP_SIZE = 240

/**
 * ナビページに案内情報を送る画面。
 * 経路の探索と地図の描画はアプリ側の役目なので、ここではテストパターンを地図に見立てて送る。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NaviScreen(client: GlassClient, onBack: () -> Unit) {
    val commandManager = remember(client) { client.createCommandManager() }

    var icon by remember { mutableStateOf(CommandManager.ManeuverIcon.TURN_LEFT) }
    var instruction by remember { mutableStateOf("交差点を左折") }
    var distance by remember { mutableStateOf("300m") }
    var arrival by remember { mutableStateOf("14:05") }
    var timeAndDistance by remember { mutableStateOf("12分/1.4km") }
    var course by remember { mutableStateOf("0") }
    var withMap by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val map = remember { testPatternImage(NAVI_MAP_SIZE) }
    val largeMap = remember { testPatternImage(NAVI_LARGE_MAP_SIZE) }

    fun safeRun(label: String, block: () -> Unit) {
        Log.d(TAG, "safeRun: $label")
        try {
            block()
        } catch (e: Throwable) {
            Log.e(TAG, "safeRun: $label FAILED", e)
            error = e.message
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("ナビを送る") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "ページを開いて状態を START にしてから案内情報を送る。" +
                    "READY のままだと何も表示されない。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            NaviButton("ナビページを開く") {
                safeRun("enterNavigationPage") {
                    commandManager.enterNavigationPage()
                    commandManager.sendNaviLanguage("JPN")
                }
            }

            Text("案内状態", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                CommandManager.NaviStatus.entries.forEach { status ->
                    OutlinedButton(
                        onClick = {
                            safeRun("sendNaviStatus:${status.name}") {
                                commandManager.sendNaviStatus(status)
                            }
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(status.name)
                    }
                    Spacer(Modifier.width(4.dp))
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            Text("案内情報", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            ManeuverIconPicker(selected = icon, onSelect = { icon = it })
            NaviTextField("指示", instruction) { instruction = it }
            NaviTextField("次のポイントまでの距離", distance) { distance = it }
            NaviTextField("予想到着時刻", arrival) { arrival = it }
            NaviTextField("残り時間と距離", timeAndDistance) { timeAndDistance = it }

            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Image(
                    bitmap = remember(map) { map.toBitmap().asImageBitmap() },
                    contentDescription = "地図に見立てたテストパターン",
                    modifier = Modifier.size(96.dp),
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "${map.width}x${map.height} のテストパターンを地図として送る",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    TextButton(onClick = { withMap = !withMap }) {
                        Text(if (withMap) "地図つきで送る" else "テキストだけ送る")
                    }
                }
            }

            NaviButton("案内情報を送る") {
                safeRun("sendNavi") {
                    commandManager.sendNavi(
                        maneuverIcon = icon,
                        instructionText = instruction,
                        distanceText = distance,
                        estimatedArrivalText = arrival,
                        timeAndDistanceText = timeAndDistance,
                        bitmapWidth = if (withMap) map.width else null,
                        bitmapHeight = if (withMap) map.height else null,
                        grayscale = if (withMap) map.pixels else null,
                    )
                }
            }

            NaviButton("全体ルートの画像を送る") {
                safeRun("sendNaviLargeImage") {
                    commandManager.sendNaviLargeImage(
                        width = largeMap.width,
                        height = largeMap.height,
                        grayscale = largeMap.pixels,
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            Text(
                "進行方向。グラスは磁力計を持たないので、この値で方位のずれを補正する",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(8.dp))
            NaviTextField("進行方向[度]", course) { course = it }
            NaviButton("進行方向を送る") {
                safeRun("sendNaviCourse") {
                    val degrees = course.toDoubleOrNull()
                    if (degrees == null) {
                        error = "進行方向は数値で入れる"
                        return@safeRun
                    }
                    commandManager.sendNaviCourse(degrees)
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    commandManager.sendNaviStatus(CommandManager.NaviStatus.READY)
                    commandManager.enterHomePage()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("戻る（グラスは Home へ）")
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
private fun ManeuverIconPicker(
    selected: CommandManager.ManeuverIcon,
    onSelect: (CommandManager.ManeuverIcon) -> Unit,
) {
    val icons = CommandManager.ManeuverIcon.entries
    OutlinedButton(
        onClick = { onSelect(icons[(icons.indexOf(selected) + 1) % icons.size]) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    ) {
        Text("進行方向アイコン: ${selected.name}")
    }
}

@Composable
private fun NaviTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    )
}

@Composable
private fun NaviButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    ) {
        Text(label)
    }
}
