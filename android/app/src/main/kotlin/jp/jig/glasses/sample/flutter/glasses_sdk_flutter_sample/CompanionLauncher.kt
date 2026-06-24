package jp.jig.glasses.sample.flutter.glasses_sdk_flutter_sample

import android.content.Context
import app.jigglass.ble.BleDeviceSelector
import app.jigglass.glass.SdkActivityHost
import io.flutter.embedding.android.FlutterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CompanionLauncher(private val activity: FlutterActivity) {

    val deviceSelector: BleDeviceSelector = BleDeviceSelector(activity)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun register() {
        SdkActivityHost.showBleDeviceSelectionDialog = { _: Context, callback: (String?) -> Unit ->
            deviceSelector.showDialog(
                scope = scope,
                singleTarget = false,
                callback = callback,
            )
        }
    }

    fun unregister() {
        SdkActivityHost.showBleDeviceSelectionDialog = null
    }
}
