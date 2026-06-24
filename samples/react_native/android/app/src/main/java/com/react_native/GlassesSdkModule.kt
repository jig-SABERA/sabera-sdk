package com.react_native

import android.util.Log
import app.jigglass.glass.CommandManager
import app.jigglass.glass.GlassClient
import app.jigglass.glass.GlassManager
import app.jigglass.glass.getGlassManager
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlassesSdkModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val manager: GlassManager = getGlassManager(reactContext)
    private var currentClient: GlassClient? = null
    private var commandManager: CommandManager? = null
    private var connectionJob: Job? = null
    private var gestureJob: Job? = null

    override fun getName() = "GlassesSdkModule"

    override fun initialize() {
        super.initialize()
        startConnectionMonitoring()
    }

    override fun invalidate() {
        connectionJob?.cancel()
        gestureJob?.cancel()
        scope.cancel()
        super.invalidate()
    }

    private fun sendEvent(name: String, params: com.facebook.react.bridge.WritableMap) {
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(name, params)
    }

    private fun startConnectionMonitoring() {
        connectionJob?.cancel()
        connectionJob = scope.launch {
            manager.connectedDevice.collect { client ->
                currentClient = client
                if (client != null) {
                    commandManager = client.createCommandManager()
                    startGestureMonitoring()
                } else {
                    gestureJob?.cancel()
                    commandManager = null
                }
                withContext(Dispatchers.Main) {
                    val map = Arguments.createMap()
                    map.putBoolean("connected", client != null)
                    map.putString("deviceId", client?.deviceIdentifier)
                    map.putString("deviceName", client?.deviceName)
                    sendEvent("onConnectionStateChange", map)
                }
            }
        }
    }

    private fun startGestureMonitoring() {
        gestureJob?.cancel()
        val cm = commandManager ?: return
        gestureJob = scope.launch {
            cm.gestureEvents.collect { gesture ->
                withContext(Dispatchers.Main) {
                    val map = Arguments.createMap()
                    map.putString("type", gesture.name)
                    sendEvent("onGestureEvent", map)
                }
            }
        }
    }

    @ReactMethod
    fun showSelectionDialog(promise: Promise) {
        val activity = reactApplicationContext.currentActivity
        if (activity == null) {
            promise.reject("NO_ACTIVITY", "No activity available")
            return
        }
        scope.launch {
            try {
                val client = manager.showAutomaticSelectionDialog(activity)
                if (client != null) {
                    val map = Arguments.createMap()
                    map.putString("deviceId", client.deviceIdentifier)
                    map.putString("deviceName", client.deviceName)
                    promise.resolve(map)
                } else {
                    promise.resolve(null)
                }
            } catch (e: Throwable) {
                promise.reject("SCAN_ERROR", e.message, e)
            }
        }
    }

    @ReactMethod
    fun disconnect(promise: Promise) {
        scope.launch {
            try {
                currentClient?.let { manager.disconnect(it) }
                promise.resolve(null)
            } catch (e: Throwable) {
                promise.reject("DISCONNECT_ERROR", e.message, e)
            }
        }
    }

    // Page navigation
    @ReactMethod
    fun enterHomePage(promise: Promise) = runCommand(promise) { it.enterHomePage() }

    @ReactMethod
    fun enterTeleprompterPage(promise: Promise) = runCommand(promise) { it.enterTeleprompterPage() }

    @ReactMethod
    fun enterAIPage(isAiPower: Boolean, promise: Promise) = runCommand(promise) { it.enterAIPage(isAiPower) }

    @ReactMethod
    fun enterTranslatePage(promise: Promise) = runCommand(promise) { it.enterTranslatePage() }

    // Content sending
    @ReactMethod
    fun sendTeleprompterContent(content: String, promise: Promise) =
        runCommand(promise) { it.sendTeleprompterContent(content) }

    @ReactMethod
    fun sendAIContent(content: String, promise: Promise) =
        runCommand(promise) { it.sendAIContent(content) }

    @ReactMethod
    fun sendTranslateContent(content: String, promise: Promise) =
        runCommand(promise) { it.sendTranslateContent(content) }

    @ReactMethod
    fun sendTranslateLanguage(source: String, target: String, promise: Promise) =
        runCommand(promise) { it.sendTranslateLanguage(source, target) }

    @ReactMethod
    fun addListener(eventName: String) { /* Required for RN NativeEventEmitter */ }

    @ReactMethod
    fun removeListeners(count: Int) { /* Required for RN NativeEventEmitter */ }

    private fun runCommand(promise: Promise, block: (CommandManager) -> Unit) {
        val cm = commandManager
        if (cm == null) {
            promise.reject("NOT_CONNECTED", "No device connected")
            return
        }
        try {
            block(cm)
            promise.resolve(null)
        } catch (e: Throwable) {
            Log.e("GlassesSdkModule", "Command failed", e)
            promise.reject("COMMAND_ERROR", e.message, e)
        }
    }
}
