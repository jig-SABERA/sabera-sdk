package jp.jig.glasses.sample.kmp

import android.app.Application
import android.util.Log
import app.jigglass.glass.GlassesSDK
import app.jigglass.glass.SdkActivityHost
import jp.jig.glasses.sample.kmp.ui.SampleAssets
import jp.jig.glasses.sample.kmp.ui.SampleLog

class GlassesSampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SampleAssets.initialize(this)
        SampleLog.sink = { tag, message -> Log.d(tag, message); Unit }
        GlassesSDK.setLogger { tag, msg -> Log.d(tag, msg) }
        GlassesSDK.setProd(true)
        GlassesSDK.setDevicePersistence(SharedPrefsDevicePersistence(this))
        SdkActivityHost.showBleDeviceSelectionDialog = null
    }
}
