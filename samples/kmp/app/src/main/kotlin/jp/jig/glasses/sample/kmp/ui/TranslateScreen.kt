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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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

private const val TAG = "TranslateScreen"

/**
 * 翻訳ページにテキストと言語ペアを送る画面。
 * ページを開いていないと表示されないので、画面に入ったところで開く。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen(client: GlassClient, onBack: () -> Unit) {
    val commandManager = remember(client) { client.createCommandManager() }

    var text by remember { mutableStateOf("Translate this") }
    var sourceLang by remember { mutableStateOf("en") }
    var targetLang by remember { mutableStateOf("ja") }
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
        safeRun("enterTranslatePage") { commandManager.enterTranslatePage() }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("翻訳") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "画面を開いたときに翻訳ページへ遷移している。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            SendableTextField(
                label = "翻訳テキスト",
                value = text,
                onValueChange = { text = it },
                onSend = {
                    safeRun("sendTranslateContent: $text") { commandManager.sendTranslateContent(text) }
                },
            )

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
                onClick = {
                    safeRun("sendTranslateLanguage: $sourceLang->$targetLang") {
                        commandManager.sendTranslateLanguage(sourceLang, targetLang)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("翻訳言語を送信")
            }

            Spacer(Modifier.height(16.dp))
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
