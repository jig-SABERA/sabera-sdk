package jp.jig.glasses.sample.reactnative

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import app.jigglass.ble.BleCompanionDeviceService
import app.jigglass.ble.BleDeviceSelector
import app.jigglass.glass.SdkActivityHost
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

class MainActivity : ReactActivity() {

    private lateinit var deviceSelector: BleDeviceSelector
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun getMainComponentName(): String = "GlassesSdkReactNativeSample"

    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deviceSelector = BleDeviceSelector(this)
        SdkActivityHost.showBleDeviceSelectionDialog = { _: Context, callback: (String?) -> Unit ->
            deviceSelector.showDialog(scope = scope, singleTarget = false, callback = callback)
        }

        BleCompanionDeviceService.connectToLastDevice(this)
        requestBlePermissionsIfNeeded()
    }

    override fun onDestroy() {
        SdkActivityHost.showBleDeviceSelectionDialog = null
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Use Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (deviceSelector.onActivityResult(requestCode, resultCode, data, MainScope())) return
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestBlePermissionsIfNeeded() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (perms.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            @Suppress("DEPRECATION")
            requestPermissions(perms, REQUEST_BLE_PERMISSIONS)
        }
    }

    private companion object {
        const val REQUEST_BLE_PERMISSIONS = 1001
    }
}
