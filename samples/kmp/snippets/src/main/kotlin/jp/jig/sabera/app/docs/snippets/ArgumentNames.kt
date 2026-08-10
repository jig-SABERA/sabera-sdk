package jp.jig.sabera.app.docs.snippets

import android.content.Context
import android.content.Intent
import app.jigglass.ble.BleCompanionDeviceService
import app.jigglass.ble.BleDeviceSelector
import app.jigglass.glass.CommandManager
import app.jigglass.glass.GlassClient
import app.jigglass.glass.GlassManager
import app.jigglass.glass.GlassesSDK
import app.jigglass.glass.SdkDevicePersistence
import app.jigglass.glass.getGlassManager
import kotlinx.coroutines.CoroutineScope

/**
 * ドキュメントに載せている引数名が SDK の実際の宣言と一致することを確かめる。
 *
 * すべて名前付き引数で呼んでいるので、SDK 側で引数名が変わるとコンパイルが落ちる。
 * ここはドキュメントには写らない（`#snippet` マーカーを置かない）。
 */
internal object ArgumentNames {

    fun glassesSdk(persistence: SdkDevicePersistence) {
        GlassesSDK.setLogger(sink = { _, _ -> })
        GlassesSDK.setProd(isProd = true)
        GlassesSDK.setDevicePersistence(persistence = persistence)
    }

    fun topLevel(context: Context): GlassManager = getGlassManager(context = context)

    suspend fun glassManager(manager: GlassManager, client: GlassClient, context: Context, intent: Intent) {
        manager.connect(glassClient = client)
        manager.disconnect(glassClient = client)
        manager.disconnectAndClearBond(glassClient = client)
        manager.showAutomaticSelectionDialog(context = context)
        manager.createClientFromDeviceID(deviceId = "00:11:22:33:44:55")
        manager.onDeviceDisappear(address = "00:11:22:33:44:55")
        manager.registerDeviceFromIntent(data = intent)
    }

    suspend fun glassClient(client: GlassClient) {
        // getter と setter の型が揃っていないため、Kotlin からは読みが val、書きがメソッドに見える
        client.setMicChannel(channel = null)
        client.sendCommand(command = byteArrayOf(0x00))
        client.sendCommandList(command = listOf(byteArrayOf(0x00)))
        client.sendText(page = 0x00, content = "text")
    }

    fun commandManager(commandManager: CommandManager, listener: () -> Unit) {
        commandManager.parseResponse(value = byteArrayOf(0x00))
        commandManager.addGlassPowerEventListener(listener = listener)
        commandManager.removeGlassPowerEventListener(listener = listener)
        commandManager.enterAIPage(isAiPower = false)
        commandManager.sendTeleprompterContent(content = "content")
        commandManager.sendAIContent(content = "content")
        commandManager.sendTranslateContent(content = "content")
        commandManager.sendTranslateLanguage(source = "en", target = "ja")
        commandManager.sendDebugPhoneName(phoneName = "Pixel")
        commandManager.sendMessage(name = "app", title = "title", time = 0L, text = "text")
        commandManager.syncNotificationCount(count = 1)
        commandManager.sendMeeting(meetingType = 0x00, text = "text", percent = 0)
        commandManager.sendAiChatText(text = "text")
    }

    fun remoteControlListener(commandManager: CommandManager, listener: CommandManager.RemoteControlListener) {
        commandManager.addRemoteControllerEventListener(listener = listener)
        commandManager.removeRemoteControllerEventListener(listener = listener)
    }

    fun ble(context: Context, scope: CoroutineScope, intent: Intent) {
        BleCompanionDeviceService.connectToLastDevice(context = context)

        val selector = BleDeviceSelector(context = context)
        selector.showDialog(scope = scope, singleTarget = false, callback = {})
        selector.onActivityResult(requestCode = 0, resultCode = 0, data = intent, scope = scope)
    }
}
