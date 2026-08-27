package jp.jig.glasses.sample.kmp.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.jigglass.glass.GlassClient
import app.jigglass.glass.GlassManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "CommandScreen"

/**
 * 接続後の入口。
 * ページを開かないと何も起きないコマンドはそれぞれの画面に置いてあり、ここは行き先を選ぶだけ。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandScreen(
    manager: GlassManager,
    client: GlassClient,
    onOpenTeleprompterScreen: () -> Unit,
    onOpenAiChatScreen: () -> Unit,
    onOpenTranslateScreen: () -> Unit,
    onOpenImageScreen: () -> Unit,
    onOpenNaviScreen: () -> Unit,
    onOpenImuScreen: () -> Unit,
    onOpenMicScreen: () -> Unit,
    onOpenLayoutScreen: () -> Unit,
    onOpenCanvasScreen: () -> Unit,
    onOpenKawakudariScreen: () -> Unit,
    onOpenLauncherScreen: () -> Unit,
    onOpenAsciiVideoScreen: () -> Unit,
    onOpenBinaryVideoScreen: () -> Unit,
    onOpenCanvasAnimationScreen: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val commandManager = remember(client) { client.createCommandManager() }

    val charging by commandManager.charging.collectAsState()

    var error by remember { mutableStateOf<String?>(null) }
    val gestures = remember { mutableStateListOf<String>() }

    DisposableEffect(commandManager) {
        val job: Job = scope.launch {
            commandManager.gestureEvents.collect { gesture ->
                gestures.add(gesture.name)
            }
        }
        onDispose { job.cancel() }
    }

    fun safeRun(label: String, block: () -> Unit) {
        Log.d(TAG, "safeRun: $label")
        try {
            block()
            Log.d(TAG, "safeRun: $label OK")
        } catch (e: Throwable) {
            Log.e(TAG, "safeRun: $label FAILED", e)
            error = e.message
        }
    }

    val deviceName = client.deviceName ?: client.deviceIdentifier

    Scaffold(
        topBar = { TopAppBar(title = { Text("接続中: $deviceName") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionTitle("ページを開いて使う")
            CommandButton("テレプロンプトの画面へ", onClick = onOpenTeleprompterScreen)
            CommandButton("AI アシスタントの画面へ", onClick = onOpenAiChatScreen)
            CommandButton("翻訳の画面へ", onClick = onOpenTranslateScreen)
            CommandButton("画像を送る画面へ", onClick = onOpenImageScreen)
            CommandButton("ナビを送る画面へ", onClick = onOpenNaviScreen)

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            SectionTitle("送るだけで切り替わる")
            CommandButton("分割レイアウトの画面へ", onClick = onOpenLayoutScreen)
            CommandButton("自由配置キャンバスの画面へ", onClick = onOpenCanvasScreen)

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            SectionTitle("その他")
            CommandButton("6DoF を受け取る画面へ", onClick = onOpenImuScreen)
            CommandButton("マイクを受け取る画面へ", onClick = onOpenMicScreen)
            CommandButton("かわくだりで遊ぶ画面へ", onClick = onOpenKawakudariScreen)
            CommandButton("ランチャーの画面へ", onClick = onOpenLauncherScreen)
            CommandButton("アスキーアートを流す画面へ", onClick = onOpenAsciiVideoScreen)
            CommandButton("2値画像を流す画面へ", onClick = onOpenBinaryVideoScreen)
            CommandButton("キャンバスに動画を流す画面へ", onClick = onOpenCanvasAnimationScreen)
            CommandButton("Home に戻す") { safeRun("enterHomePage") { commandManager.enterHomePage() } }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            SectionTitle("デバイス状態")
            Text(
                when (charging) {
                    true -> "充電中"
                    false -> "充電していない"
                    null -> "未取得"
                },
            )
            Spacer(Modifier.height(16.dp))

            if (gestures.isNotEmpty()) {
                SectionTitle("ジェスチャーイベント")
                Text(gestures.joinToString(", "))
                Spacer(Modifier.height(16.dp))
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            manager.disconnect(client)
                        } catch (e: Throwable) {
                            error = e.message
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("切断")
            }

            error?.let { msg ->
                Spacer(Modifier.height(16.dp))
                Text(msg, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { error = null }) {
                    Text("エラーを消す")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
