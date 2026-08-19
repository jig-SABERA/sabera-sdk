package jp.jig.glasses.sample.kmp.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
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

/** 同時に置ける要素の数。id は 0..7 */
private const val MAX_ELEMENTS = 8

/** 要素TLVの固定部（id + x + y + w + h）とTLVヘッダ */
private const val ELEMENT_OVERHEAD_BYTES = 12

/** 位置が見て分かるように四隅と中央へ置くデモ */
private val demoElements = listOf(
    ElementInput(id = 0, x = 16, y = 8, text = "左上"),
    ElementInput(id = 1, x = 360, y = 8, text = "右上"),
    ElementInput(id = 2, x = 190, y = 160, text = "中央"),
    ElementInput(id = 3, x = 16, y = 310, text = "左下"),
    ElementInput(id = 4, x = 360, y = 310, text = "右下"),
)

/**
 * 自由配置キャンバスに要素を送る画面。
 * 要素を足したり消したりしながら、全消しして置き直すのと部分更新の違いを試せる。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(client: GlassClient, onBack: () -> Unit) {
    val commandManager = remember(client) { client.createCommandManager() }

    val elements = remember { mutableStateListOf(demoElements[0], demoElements[3]) }
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

    // 削除した id は空き番として再利用する。グラス側も id で要素を見分けている
    fun nextId(): Int = (0 until MAX_ELEMENTS).first { id -> elements.none { it.id == id } }

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

            elements.forEachIndexed { index, element ->
                ElementEditor(
                    input = element,
                    onChange = { elements[index] = it },
                    onRemove = {
                        elements.removeAt(index)
                        // テキストを空にした要素を送るとグラス側から消える
                        safeRun("removeElement ${element.id}") {
                            commandManager.sendCanvasElements(listOf(element.toElement().copy(text = "")))
                        }
                    },
                )
            }
            OutlinedButton(
                onClick = { elements.add(ElementInput(id = nextId(), x = 16, y = 8, text = "テキスト")) },
                enabled = elements.size < MAX_ELEMENTS,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (elements.size < MAX_ELEMENTS) "要素を追加" else "要素は8個まで")
            }

            Spacer(Modifier.height(8.dp))
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
            Button(
                onClick = {
                    elements.clear()
                    elements.addAll(demoElements)
                    safeRun("sendDemo") {
                        commandManager.sendCanvas(demoElements.map { it.toElement() })
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("四隅と中央にサンプルを送る")
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

/** 編集中の要素 */
private data class ElementInput(
    val id: Int,
    val x: Int,
    val y: Int,
    val text: String,
    val width: Int = 200,
    val height: Int = 40,
) {
    fun toElement() = CommandManager.CanvasElement(
        id = id,
        x = x,
        y = y,
        width = width,
        height = height,
        text = text,
    )
}

@Composable
private fun ElementEditor(
    input: ElementInput,
    onChange: (ElementInput) -> Unit,
    onRemove: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("id ${input.id}", style = MaterialTheme.typography.titleSmall)
            TextButton(onClick = onRemove) {
                Text("削除")
            }
        }
        Row(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            NumberField("x", input.x, { onChange(input.copy(x = it)) }, Modifier.weight(1f))
            Spacer(Modifier.width(4.dp))
            NumberField("y", input.y, { onChange(input.copy(y = it)) }, Modifier.weight(1f))
            Spacer(Modifier.width(4.dp))
            NumberField("w", input.width, { onChange(input.copy(width = it)) }, Modifier.weight(1f))
            Spacer(Modifier.width(4.dp))
            NumberField("h", input.height, { onChange(input.copy(height = it)) }, Modifier.weight(1f))
        }
        OutlinedTextField(
            value = input.text,
            onValueChange = { onChange(input.copy(text = it)) },
            label = { Text("テキスト") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun NumberField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { onValueChange(it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0) },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
    )
}
