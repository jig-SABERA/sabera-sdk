package jp.jig.glasses.sample.reactnative

import android.content.Context
import app.jigglass.glass.SdkDevicePersistence

class SharedPrefsDevicePersistence(context: Context) : SdkDevicePersistence {
    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override var lastDeviceId: String?
        get() = prefs.getString(KEY_LAST_DEVICE_ID, null)
        set(value) {
            prefs.edit().apply {
                if (value == null) remove(KEY_LAST_DEVICE_ID) else putString(KEY_LAST_DEVICE_ID, value)
            }.apply()
        }

    private companion object {
        const val PREFS_NAME = "glasses_sdk_rn_sample"
        const val KEY_LAST_DEVICE_ID = "last_device_id"
    }
}
