package jp.jig.glasses.sample.kmp.ui

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.util.Log
import app.jigglass.glass.GlassClient
import app.jigglass.glass.GlassManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "CommandScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandScreen(manager: GlassManager, client: GlassClient) {
    val scope = rememberCoroutineScope()
    val commandManager = remember(client) { client.createCommandManager() }

    var error by remember { mutableStateOf<String?>(null) }
    val gestures = remember { mutableStateListOf<String>() }

    var teleprompterText by remember { mutableStateOf("Hello from KMP") }
    var aiText by remember { mutableStateOf("質問内容をどうぞ") }
    var aiChatText by remember { mutableStateOf("今日の天気は？") }
    var translateText by remember { mutableStateOf("Translate this") }
    var sourceLang by remember { mutableStateOf("en") }
    var targetLang by remember { mutableStateOf("ja") }

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

    val deviceName = client.deviceName ?: client.deviceIdentifier ?: "Unknown"

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
            // Page navigation
            SectionTitle("ページ遷移")
            CommandButton("Home に戻す") { safeRun("enterHomePage") { commandManager.enterHomePage() } }
            CommandButton("Teleprompter を開く") { safeRun("enterTeleprompterPage") { commandManager.enterTeleprompterPage() } }
            CommandButton("AI アシスタントを開く") { safeRun("enterAiChatPage") { commandManager.enterAiChatPage() } }
            CommandButton("AI ページを開く（旧 UI）") { safeRun("enterAIPage") { commandManager.enterAIPage(false) } }
            CommandButton("翻訳ページを開く") { safeRun("enterTranslatePage") { commandManager.enterTranslatePage() } }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            // Text sending
            SectionTitle("テキスト送信")
            SendableTextField(
                label = "Teleprompter テキスト",
                value = teleprompterText,
                onValueChange = { teleprompterText = it },
                onSend = { safeRun("sendTeleprompterContent: $teleprompterText") { commandManager.sendTeleprompterContent(teleprompterText) } },
            )
            SendableTextField(
                label = "AI テキスト",
                value = aiText,
                onValueChange = { aiText = it },
                onSend = { safeRun("sendAIContent: $aiText") { commandManager.sendAIContent(aiText) } },
            )
            SendableTextField(
                label = "AI チャットテキスト",
                value = aiChatText,
                onValueChange = { aiChatText = it },
                onSend = { safeRun("sendAiChatText: $aiChatText") { commandManager.sendAiChatText(aiChatText) } },
            )
            SendableTextField(
                label = "翻訳テキスト",
                value = translateText,
                onValueChange = { translateText = it },
                onSend = { safeRun("sendTranslateContent: $translateText") { commandManager.sendTranslateContent(translateText) } },
            )

            // Language pair
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = sourceLang,
                    onValueChange = { sourceLang = it },
                    label = { Text("翻訳元") },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = targetLang,
                    onValueChange = { targetLang = it },
                    label = { Text("翻訳先") },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { safeRun("sendTranslateLanguage: $sourceLang->$targetLang") { commandManager.sendTranslateLanguage(sourceLang, targetLang) } },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("翻訳言語を送信")
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            // Gesture events
            if (gestures.isNotEmpty()) {
                SectionTitle("ジェスチャーイベント")
                Text(gestures.joinToString(", "))
                Spacer(Modifier.height(16.dp))
            }

            // Disconnect
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

            // Error display
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

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun CommandButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    ) {
        Text(label)
    }
}

@Composable
private fun SendableTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(4.dp))
    Button(
        onClick = onSend,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("$label を送信")
    }
    Spacer(Modifier.height(12.dp))
}
