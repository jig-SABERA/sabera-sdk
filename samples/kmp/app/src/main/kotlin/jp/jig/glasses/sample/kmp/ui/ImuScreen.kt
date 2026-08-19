package jp.jig.glasses.sample.kmp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.jigglass.glass.CommandManager
import app.jigglass.glass.GlassClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 6DoF を受け取る画面。
 * 値そのものより、受信できているかと間隔が分かるようにしてある。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImuScreen(client: GlassClient, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val commandManager = remember(client) { client.createCommandManager() }
    val started by commandManager.imuDataStarted.collectAsState()

    var latest by remember { mutableStateOf<CommandManager.ImuData?>(null) }
    var count by remember { mutableStateOf(0) }

    // 周期は timestampMs の差で見る。受信時刻だと送信キューの詰まりが見えない
    var intervalMs by remember { mutableStateOf<Long?>(null) }

    DisposableEffect(commandManager) {
        val job: Job = scope.launch {
            commandManager.imuData.collect { data ->
                intervalMs = latest?.let { data.timestampMs - it.timestampMs }
                latest = data
                count++
            }
        }
        onDispose {
            job.cancel()
            commandManager.stopImuData()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("6DoF を受け取る") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "FEATURE_VERSION 2.0.0 以上のファームが対象。切断するとグラス側で止まるので、" +
                    "再接続したら開始し直す。",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (started) commandManager.stopImuData() else commandManager.startImuData()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (started) "停止する" else "開始する")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (started) "受信中 / $count サンプル" else "停止中 / $count サンプル",
                style = MaterialTheme.typography.bodySmall,
            )

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            val data = latest
            if (data == null) {
                Text("まだ届いていない", style = MaterialTheme.typography.bodyMedium)
            } else {
                ImuRow("経過時間", "${data.timestampMs} ms")
                ImuRow("前サンプルとの差", intervalMs?.let { "$it ms" } ?: "-")
                Spacer(Modifier.height(8.dp))
                ImuRow(
                    "加速度 [mg]",
                    "${data.accelXMilliG} / ${data.accelYMilliG} / ${data.accelZMilliG}",
                )
                ImuRow(
                    "角速度 [dps]",
                    "%.1f / %.1f / %.1f".format(data.gyroXDps, data.gyroYDps, data.gyroZDps),
                )
                Spacer(Modifier.height(8.dp))
                ImuRow("ピッチ [度]", "%.1f".format(data.pitchDegrees))
                ImuRow("ヨー [度]", "%.1f".format(data.yawDegrees))
                Spacer(Modifier.height(8.dp))
                Text(
                    "ピッチは取付補正済みで上向きが負。ヨーは±180で折り返し、磁力計が無いので少しずつずれる。",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    commandManager.stopImuData()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("戻る")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ImuRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}
