package jp.jig.glasses.sample.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.jigglass.glass.GlassManager

private enum class ConnectedScreen {
    COMMAND,
    IMAGE,
    NAVI,
    IMU,
}

@Composable
fun GlassesApp(manager: GlassManager) {
    val client by manager.connectedDevice.collectAsState(initial = null)
    var screen by remember { mutableStateOf(ConnectedScreen.COMMAND) }

    val currentClient = client
    if (currentClient == null) {
        ScanScreen(manager = manager)
        return
    }

    when (screen) {
        ConnectedScreen.COMMAND -> CommandScreen(
            manager = manager,
            client = currentClient,
            onOpenImageScreen = { screen = ConnectedScreen.IMAGE },
            onOpenNaviScreen = { screen = ConnectedScreen.NAVI },
            onOpenImuScreen = { screen = ConnectedScreen.IMU },
        )

        ConnectedScreen.IMAGE -> ImageScreen(
            client = currentClient,
            onBack = { screen = ConnectedScreen.COMMAND },
        )

        ConnectedScreen.NAVI -> NaviScreen(
            client = currentClient,
            onBack = { screen = ConnectedScreen.COMMAND },
        )

        ConnectedScreen.IMU -> ImuScreen(
            client = currentClient,
            onBack = { screen = ConnectedScreen.COMMAND },
        )
    }
}
