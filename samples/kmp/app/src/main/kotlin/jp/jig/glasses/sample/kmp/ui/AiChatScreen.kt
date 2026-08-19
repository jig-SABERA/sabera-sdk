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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.jigglass.glass.GlassClient

private const val TAG = "AiChatScreen"

/**
 * AI アシスタントにテキストを送る画面。
 * どちらの送信もページを開いていないと表示されないので、画面に入ったところで開く。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(client: GlassClient, onBack: () -> Unit) {
    val commandManager = remember(client) { client.createCommandManager() }

    var aiText by remember { mutableStateOf("質問内容をどうぞ") }
    var chatText by remember { mutableStateOf("今日の天気は？") }
    var error by remember { mutableStateOf<String?>(null) }

    fun safeRun(label: String, block: () -> Unit) {
        Log.d(TAG, "safeRun: $label")
        try {
            block()
        } catch (e: Throwable) {
            Log.e(TAG, "safeRun: $label FAILED", e)
            error = e.message
        }
    }

    LaunchedEffect(commandManager) {
        safeRun("enterAiChatPage") { commandManager.enterAiChatPage() }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("AI アシスタント") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "画面を開いたときに AI アシスタントページへ遷移している。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            SendableTextField(
                label = "AI テキスト",
                value = aiText,
                onValueChange = { aiText = it },
                onSend = { safeRun("sendAIContent: $aiText") { commandManager.sendAIContent(aiText) } },
            )
            SendableTextField(
                label = "AI チャットテキスト",
                value = chatText,
                onValueChange = { chatText = it },
                onSend = { safeRun("sendAiChatText: $chatText") { commandManager.sendAiChatText(chatText) } },
            )

            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    safeRun("enterHomePage") { commandManager.enterHomePage() }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("戻る（Home に戻す）")
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
