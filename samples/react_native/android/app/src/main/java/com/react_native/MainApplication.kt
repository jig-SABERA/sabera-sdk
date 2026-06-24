package com.react_native

import android.app.Application
import android.util.Log
import app.jigglass.glass.GlassesSDK
import app.jigglass.glass.SdkActivityHost
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeApplicationEntryPoint.loadReactNative
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost

class MainApplication : Application(), ReactApplication {

    override val reactHost: ReactHost by lazy {
        getDefaultReactHost(
            context = applicationContext,
            packageList =
                PackageList(this).packages.apply {
                    add(GlassesSdkPackage())
                },
        )
    }

    override fun onCreate() {
        super.onCreate()
        loadReactNative(this)

        GlassesSDK.setLogger { tag, msg -> Log.d(tag, msg) }
        GlassesSDK.setProd(true)
        GlassesSDK.setDevicePersistence(SharedPrefsDevicePersistence(this))
        SdkActivityHost.showBleDeviceSelectionDialog = null
    }
}
