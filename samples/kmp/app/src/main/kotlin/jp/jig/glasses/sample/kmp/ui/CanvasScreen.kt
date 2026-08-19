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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.jigglass.glass.CommandManager
import app.jigglass.glass.GlassClient

private const val TAG = "CanvasScreen"

/** グラス側が受け取れるテキストの合計。分割して送れないので目安を出しておく */
private const val TEXT_BUDGET_BYTES = 190

/** 仮想キャンバスの大きさ。要素はこの外に出ると描かれない */
private const val CANVAS_WIDTH = 576
private const val CANVAS_HEIGHT = 360

/** 要素TLVの固定部（id + x + y + w + h）とTLVヘッダ */
private const val ELEMENT_OVERHEAD_BYTES = 12

private class CanvasElementInput(
    val id: Int,
    x: Int,
    y: Int,
    w: Int,
    h: Int,
    text: String,
) {
    var x by mutableStateOf(x.toString())
    var y by mutableStateOf(y.toString())
    var w by mutableStateOf(w.toString())
    var h by mutableStateOf(h.toString())
    var text by mutableStateOf(text)

    fun toElement() = CommandManager.CanvasElement(
        id = id,
        x = x.toIntOrNull() ?: 0,
        y = y.toIntOrNull() ?: 0,
        width = w.toIntOrNull() ?: 0,
        height = h.toIntOrNull() ?: 0,
        text = text,
    )
}

/**
 * 自由配置キャンバスに要素を送る画面。
 * 全消しして配置するのと、送った分だけ積み上がる部分更新の違いを試せる。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(client: GlassClient, onBack: () -> Unit) {
    val commandManager = remember(client) { client.createCommandManager() }

    val elements = remember {
        mutableStateListOf(
            CanvasElementInput(id = 0, x = 16, y = 8, w = 240, h = 40, text = "左上"),
            CanvasElementInput(id = 1, x = 320, y = 160, w = 240, h = 40, text = "右の真ん中"),
            CanvasElementInput(id = 2, x = 16, y = 300, w = 240, h = 40, text = "左下"),
        )
    }
    var error by remember { mutableStateOf<String?>(null) }

    val used = elements.sumOf { it.text.toByteArray().size + ELEMENT_OVERHEAD_BYTES }

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
        topBar = { TopAppBar(title = { Text("自由配置キャンバス") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "送るだけでキャンバス画面に移る。FEATURE_VERSION 2.1.0 以上のファームが対象。",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                "キャンバスは ${CANVAS_WIDTH}×${CANVAS_HEIGHT}。要素は id 0〜7 の最大8個",
                style = MaterialTheme.typography.bodySmall,
            )

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            elements.forEach { element ->
                Text("id ${element.id}", style = MaterialTheme.typography.titleSmall)
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    NumberField("x", element.x, { element.x = it }, Modifier.weight(1f))
                    Spacer(Modifier.width(4.dp))
                    NumberField("y", element.y, { element.y = it }, Modifier.weight(1f))
                    Spacer(Modifier.width(4.dp))
                    NumberField("w", element.w, { element.w = it }, Modifier.weight(1f))
                    Spacer(Modifier.width(4.dp))
                    NumberField("h", element.h, { element.h = it }, Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = element.text,
                    onValueChange = { element.text = it },
                    label = { Text("テキスト") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                )
            }
            Text(
                "テキストは約 $used / $TEXT_BUDGET_BYTES バイト。超えるぶんは要素ごとに分けて送る",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    safeRun("sendCanvas") {
                        commandManager.sendCanvas(elements.map { it.toElement() })
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("全消しして配置する")
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    safeRun("sendCanvasElements") {
                        commandManager.sendCanvasElements(elements.map { it.toElement() })
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("残したまま差し替える")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    safeRun("sendCanvasElements one by one") {
                        // 1要素ずつでも表示は積み上がる。長いテキストはこうして分けて送る
                        elements.forEach { input ->
                            commandManager.sendCanvasElements(listOf(input.toElement()))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("1要素ずつ送る")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    safeRun("removeFirstElement") {
                        commandManager.sendCanvasElements(
                            listOf(CommandManager.CanvasElement(id = 0, x = 0, y = 0, width = 0, height = 0, text = "")),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("id 0 を消す")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { safeRun("clearCanvas") { commandManager.clearCanvas() } },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("全要素を消す（画面は残す）")
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { safeRun("closeCanvas") { commandManager.closeCanvas() } },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("キャンバスを閉じる")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    commandManager.closeCanvas()
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

@Composable
private fun NumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
    )
}
