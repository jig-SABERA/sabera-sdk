package jp.jig.glasses.sample.flutter.glasses_sdk_flutter_sample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import app.jigglass.ble.BleCompanionDeviceService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private lateinit var companionLauncher: CompanionLauncher
    private lateinit var plugin: GlassesSdkPlugin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        companionLauncher = CompanionLauncher(this).also { it.register() }

        // 前回接続デバイスへの自動再接続
        BleCompanionDeviceService.connectToLastDevice(this)

        requestBlePermissionsIfNeeded()
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        plugin = GlassesSdkPlugin(this)
        plugin.initialize()

        val messenger = flutterEngine.dartExecutor.binaryMessenger

        MethodChannel(messenger, "jp.jig.glasses.sdk/glasses")
            .setMethodCallHandler(plugin)

        EventChannel(messenger, "jp.jig.glasses.sdk/connectionState")
            .setStreamHandler(plugin)

        EventChannel(messenger, "jp.jig.glasses.sdk/gestureEvents")
            .setStreamHandler(plugin.gestureStreamHandler)
    }

    override fun onDestroy() {
        companionLauncher.unregister()
        if (::plugin.isInitialized) plugin.dispose()
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (companionLauncher.deviceSelector.onActivityResult(
                requestCode,
                resultCode,
                data,
                kotlinx.coroutines.MainScope(),
            )
        ) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestBlePermissionsIfNeeded() {
        val perms =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            } else {
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        val needRequest =
            perms.any { perm ->
                ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
            }
        if (needRequest) {
            @Suppress("DEPRECATION")
            requestPermissions(perms, REQUEST_BLE_PERMISSIONS)
        }
    }

    private companion object {
        const val REQUEST_BLE_PERMISSIONS = 1001
    }
}
