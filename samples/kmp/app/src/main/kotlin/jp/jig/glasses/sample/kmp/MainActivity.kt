package jp.jig.glasses.sample.kmp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import app.jigglass.ble.BleCompanionDeviceService
import app.jigglass.ble.BleDeviceSelector
import app.jigglass.glass.SdkActivityHost
import app.jigglass.glass.getGlassManager
import jp.jig.glasses.sample.kmp.ui.GlassesApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

class MainActivity : ComponentActivity() {

    private lateinit var deviceSelector: BleDeviceSelector
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deviceSelector = BleDeviceSelector(this)
        SdkActivityHost.showBleDeviceSelectionDialog = { _: Context, callback: (String?) -> Unit ->
            deviceSelector.showDialog(scope = scope, singleTarget = false, callback = callback)
        }

        BleCompanionDeviceService.connectToLastDevice(this)
        requestBlePermissionsIfNeeded()

        val manager = getGlassManager(applicationContext)

        setContent {
            MaterialTheme {
                GlassesApp(manager = manager)
            }
        }
    }

    override fun onDestroy() {
        SdkActivityHost.showBleDeviceSelectionDialog = null
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    @Deprecated("Use Activity Result API", ReplaceWith("registerForActivityResult(...)"))
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
