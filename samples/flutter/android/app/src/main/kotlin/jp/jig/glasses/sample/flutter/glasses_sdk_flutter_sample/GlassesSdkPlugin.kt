package jp.jig.glasses.sample.flutter.glasses_sdk_flutter_sample

import android.app.Activity
import app.jigglass.glass.CommandManager
import app.jigglass.glass.GlassClient
import app.jigglass.glass.GlassManager
import app.jigglass.glass.getGlassManager
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlassesSdkPlugin(private val activity: Activity) :
    MethodChannel.MethodCallHandler,
    EventChannel.StreamHandler {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var manager: GlassManager
    private var currentClient: GlassClient? = null
    private var commandManager: CommandManager? = null

    // EventChannel sinks
    private var connectionSink: EventChannel.EventSink? = null
    private var gestureSink: EventChannel.EventSink? = null

    // Collection jobs
    private var connectionJob: Job? = null
    private var gestureJob: Job? = null

    fun initialize() {
        manager = getGlassManager(activity.applicationContext)
        startConnectionMonitoring()
    }

    fun dispose() {
        connectionJob?.cancel()
        gestureJob?.cancel()
        scope.cancel()
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
                    connectionSink?.success(
                        mapOf(
                            "connected" to (client != null),
                            "deviceId" to client?.deviceIdentifier,
                            "deviceName" to client?.deviceName,
                        ),
                    )
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
                    gestureSink?.success(mapOf("type" to gesture.name))
                }
            }
        }
    }

    // MethodChannel.MethodCallHandler
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "initialize" -> {
                result.success(null)
            }

            "showSelectionDialog" -> {
                scope.launch {
                    try {
                        val client = manager.showAutomaticSelectionDialog(activity)
                        if (client != null) {
                            result.success(
                                mapOf(
                                    "deviceId" to client.deviceIdentifier,
                                    "deviceName" to client.deviceName,
                                ),
                            )
                        } else {
                            result.success(null)
                        }
                    } catch (e: Throwable) {
                        result.error("SCAN_ERROR", e.message, null)
                    }
                }
            }

            "connect" -> {
                val deviceId = call.argument<String>("deviceId")
                if (deviceId == null) {
                    result.error("INVALID_ARGS", "deviceId is required", null)
                    return
                }
                scope.launch {
                    try {
                        val client = manager.createClientFromDeviceID(deviceId)
                        if (client != null) {
                            manager.connect(client)
                            result.success(null)
                        } else {
                            result.error("DEVICE_NOT_FOUND", "Device not found: $deviceId", null)
                        }
                    } catch (e: Throwable) {
                        result.error("CONNECT_ERROR", e.message, null)
                    }
                }
            }

            "disconnect" -> {
                scope.launch {
                    try {
                        currentClient?.let { manager.disconnect(it) }
                        result.success(null)
                    } catch (e: Throwable) {
                        result.error("DISCONNECT_ERROR", e.message, null)
                    }
                }
            }

            // Page navigation
            "enterHomePage" -> runCommand(result) { it.enterHomePage() }
            "enterTeleprompterPage" -> runCommand(result) { it.enterTeleprompterPage() }
            "enterTranslatePage" -> runCommand(result) { it.enterTranslatePage() }

            // Content sending
            "sendTeleprompterContent" -> {
                val content = call.argument<String>("content") ?: ""
                runCommand(result) { it.sendTeleprompterContent(content) }
            }
            "sendAIContent" -> {
                val content = call.argument<String>("content") ?: ""
                runCommand(result) { it.sendAIContent(content) }
            }
            "sendTranslateContent" -> {
                val content = call.argument<String>("content") ?: ""
                runCommand(result) { it.sendTranslateContent(content) }
            }
            "sendTranslateLanguage" -> {
                val source = call.argument<String>("source") ?: ""
                val target = call.argument<String>("target") ?: ""
                runCommand(result) { it.sendTranslateLanguage(source, target) }
            }

            else -> result.notImplemented()
        }
    }

    private fun runCommand(result: MethodChannel.Result, block: (CommandManager) -> Unit) {
        val cm = commandManager
        if (cm == null) {
            result.error("NOT_CONNECTED", "No device connected", null)
            return
        }
        try {
            block(cm)
            result.success(null)
        } catch (e: Throwable) {
            result.error("COMMAND_ERROR", e.message, null)
        }
    }

    // EventChannel.StreamHandler — connectionState
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        connectionSink = events
        val client = currentClient
        events?.success(
            mapOf(
                "connected" to (client != null),
                "deviceId" to client?.deviceIdentifier,
                "deviceName" to client?.deviceName,
            ),
        )
    }

    override fun onCancel(arguments: Any?) {
        connectionSink = null
    }

    val gestureStreamHandler =
        object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                gestureSink = events
            }

            override fun onCancel(arguments: Any?) {
                gestureSink = null
            }
        }
}
