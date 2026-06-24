package jp.jig.glasses.sample.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.jigglass.glass.GlassManager

@Composable
fun GlassesApp(manager: GlassManager) {
    val client by manager.connectedDevice.collectAsState(initial = null)

    val currentClient = client
    if (currentClient != null) {
        CommandScreen(
            manager = manager,
            client = currentClient,
        )
    } else {
        ScanScreen(manager = manager)
    }
}
