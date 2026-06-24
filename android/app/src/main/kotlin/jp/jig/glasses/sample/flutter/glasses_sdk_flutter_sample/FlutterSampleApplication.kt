package jp.jig.glasses.sample.flutter.glasses_sdk_flutter_sample

import android.app.Application
import android.util.Log
import app.jigglass.glass.GlassesSDK
import app.jigglass.glass.SdkActivityHost

class FlutterSampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Logger: Logcat に流す（既定 no-op）
        GlassesSDK.setLogger { tag, msg -> Log.d(tag, msg) }

        // 2. 本番/開発判定（既定 true）
        GlassesSDK.setProd(true)

        // 3. 永続化: SharedPreferences に lastDeviceId を覚えさせる
        GlassesSDK.setDevicePersistence(SharedPrefsDevicePersistence(this))

        // 4. Activity 連携: Activity 側で後から設定する
        SdkActivityHost.showBleDeviceSelectionDialog = null
    }
}
