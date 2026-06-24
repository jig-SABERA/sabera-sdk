package jp.jig.glasses.sample.kmp.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.jigglass.glass.GlassManager
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(manager: GlassManager) {
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    var scanning by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Glasses SDK KMP Sample",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(16.dp))
        Text("OS のデバイス選択ダイアログから glasses を選んでください")
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                error = null
                scanning = true
                scope.launch {
                    try {
                        manager.showAutomaticSelectionDialog(activity)
                    } catch (e: Throwable) {
                        error = "Connection error: ${e.message}"
                    } finally {
                        scanning = false
                    }
                }
            },
            enabled = !scanning,
        ) {
            Text(if (scanning) "スキャン中..." else "スキャン開始")
        }

        error?.let { msg ->
            Spacer(Modifier.height(16.dp))
            Text(msg, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { error = null }) {
                Text("エラーを消す")
            }
        }
    }
}
