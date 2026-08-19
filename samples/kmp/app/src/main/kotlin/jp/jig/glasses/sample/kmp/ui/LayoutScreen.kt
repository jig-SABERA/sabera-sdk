package jp.jig.glasses.sample.kmp.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.jigglass.glass.CommandManager
import app.jigglass.glass.GlassClient

private const val TAG = "LayoutScreen"

/** グラス側が受け取れるテキストの合計。分割して送れないので目安を出しておく */
private const val TEXT_BUDGET_BYTES = 190

/** 分割ごとの領域の呼び名。region 番号は分割によって意味が変わる */
private val regionLabels = mapOf(
    CommandManager.LayoutMode.FULL to listOf("全画面"),
    CommandManager.LayoutMode.TOP_BOTTOM to listOf("上", "下"),
    CommandManager.LayoutMode.LEFT_RIGHT to listOf("左", "右"),
    CommandManager.LayoutMode.QUAD to listOf("左上", "右上", "左下", "右下"),
)

/**
 * 分割レイアウトにテキストを送る画面。
 * モード送信は全領域がクリアされ、部分更新は分割を保ったまま差し替わる違いを試せる。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutScreen(client: GlassClient, onBack: () -> Unit) {
    val commandManager = remember(client) { client.createCommandManager() }

    var mode by remember { mutableStateOf(CommandManager.LayoutMode.LEFT_RIGHT) }
    val texts = remember { mutableStateListOf("左", "右", "左下", "右下") }
    var error by remember { mutableStateOf<String?>(null) }

    val labels = regionLabels.getValue(mode)
    val used = labels.indices.sumOf { texts[it].toByteArray().size + 4 }

    fun safeRun(label: String, block: () -> Unit) {
        Log.d(TAG, "safeRun: $label")
        try {
            block()
        } catch (e: Throwable) {
            Log.e(TAG, "safeRun: $label FAILED", e)
            error = e.message
        }
    }

    fun currentTexts(): Map<Int, String> = labels.indices.associateWith { texts[it] }

    Scaffold(
        topBar = { TopAppBar(title = { Text("分割レイアウト") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "送るだけでレイアウト画面に移る。FEATURE_VERSION 2.0.0 以上のファームが対象。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            Text("分割", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                CommandManager.LayoutMode.entries.forEach { entry ->
                    OutlinedButton(
                        onClick = { mode = entry },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(if (entry == mode) "●${entry.value}" else "${entry.value}")
                    }
                    Spacer(Modifier.width(4.dp))
                }
            }
            Text(
                regionLabels.getValue(mode).joinToString(" / ") { it },
                style = MaterialTheme.typography.bodySmall,
            )

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            labels.forEachIndexed { region, label ->
                OutlinedTextField(
                    value = texts[region],
                    onValueChange = { texts[region] = it },
                    label = { Text("$label（region $region）") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
            }
            Text(
                "テキストは約 $used / $TEXT_BUDGET_BYTES バイト。超えると送信時に弾かれる",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    safeRun("sendLayout") {
                        commandManager.sendLayout(mode = mode, texts = currentTexts())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("分割ごと送る（全領域クリア）")
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    safeRun("sendLayoutTexts") { commandManager.sendLayoutTexts(currentTexts()) }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("テキストだけ差し替える")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    safeRun("clearFirstRegion") { commandManager.sendLayoutTexts(mapOf(0 to "")) }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("先頭の領域だけ消す")
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { safeRun("closeLayout") { commandManager.closeLayout() } },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("レイアウトを閉じる")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    commandManager.closeLayout()
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
