package jp.jig.sabera.app.docs.snippets

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import app.jigglass.ble.BleCompanionDeviceService
import app.jigglass.ble.BleDeviceSelector
import app.jigglass.glass.GlassesSDK
import app.jigglass.glass.SdkActivityHost
import app.jigglass.glass.SdkDevicePersistence
import kotlinx.coroutines.CoroutineScope

/** SDK の初期設定に関するコード例。 */
internal object SetupSnippets {

    fun setLogger() {
        // #snippet GlassesSDK.setLogger
        GlassesSDK.setLogger { tag, message -> Log.d(tag, message) }
        // #endsnippet
    }

    fun setProd() {
        // #snippet GlassesSDK.setProd
        GlassesSDK.setProd(true)
        // #endsnippet
    }

    fun setDevicePersistence(application: Application) {
        // #snippet GlassesSDK.setDevicePersistence
        GlassesSDK.setDevicePersistence(SharedPrefsDevicePersistence(application))
        // #endsnippet
    }

    fun installDialogHook(context: Context, scope: CoroutineScope) {
        val selector = BleDeviceSelector(context)
        // #snippet SdkActivityHost.showBleDeviceSelectionDialog
        SdkActivityHost.showBleDeviceSelectionDialog = { _: Context, callback: (String?) -> Unit ->
            selector.showDialog(scope = scope, singleTarget = false, callback = callback)
        }
        // #endsnippet
    }

    fun showDialog(context: Context, scope: CoroutineScope) {
        // #snippet BleDeviceSelector.showDialog
        val selector = BleDeviceSelector(context)
        selector.showDialog(scope = scope, singleTarget = false) { deviceId ->
            // 選ばれたデバイスの ID。キャンセルされた場合は null
            Log.d("sample", "selected: $deviceId")
        }
        // #endsnippet
    }

    fun connectToLastDevice(context: Context) {
        // #snippet BleCompanionDeviceService.connectToLastDevice
        BleCompanionDeviceService.connectToLastDevice(context)
        // #endsnippet
    }

    fun forwardActivityResult(
        selector: BleDeviceSelector,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        scope: CoroutineScope,
    ): Boolean {
        // #snippet BleDeviceSelector.onActivityResult
        if (selector.onActivityResult(requestCode, resultCode, data, scope)) {
            // SDK が処理した。Activity 側の処理は不要
            return true
        }
        // #endsnippet
        return false
    }
}

/** [SdkDevicePersistence] を SharedPreferences で実装した例。 */
internal class SharedPrefsDevicePersistence(context: Context) : SdkDevicePersistence {
    private val prefs = context.getSharedPreferences("sabera_sdk", Context.MODE_PRIVATE)

    // #snippet SdkDevicePersistence.lastDeviceId
    override var lastDeviceId: String?
        get() = prefs.getString("last_device_id", null)
        set(value) {
            prefs.edit().putString("last_device_id", value).apply()
        }
    // #endsnippet
}
