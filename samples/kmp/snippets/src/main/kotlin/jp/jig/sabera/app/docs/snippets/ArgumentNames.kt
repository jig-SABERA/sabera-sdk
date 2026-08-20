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
        commandManager.sendTeleprompterContent(content = "content")
        commandManager.sendTranslateContent(content = "content")
        commandManager.sendTranslateLanguage(source = "en", target = "ja")
        commandManager.sendDebugPhoneName(phoneName = "Pixel")
        commandManager.sendMessage(name = "app", title = "title", time = 0L, text = "text")
        commandManager.syncNotificationCount(count = 1)
        commandManager.sendAiChatText(text = "text")
        commandManager.sendTeleprompterContent(content = "content", percent = 0)
        commandManager.sendTeleprompterLine(text = "text", percent = 0, scrollUp = false)
        commandManager.sendTeleprompterTime(time = "01:23")
        commandManager.sendEmptyScreenContent(content = "content")
        commandManager.sendImage(width = 196, height = 196, grayscale = ByteArray(196 * 196))
        commandManager.sendAiChatLanguage(languageCode = "JPN")
        commandManager.sendWakeupTiltThreshold(degrees = 10)
        commandManager.sendSettingPageVisibility(show = true)
        commandManager.sendSetting(name = CommandManager.SettingKey.FONT_SIZE, value = 1)
        commandManager.syncWeather(type = CommandManager.WeatherType.TEMPERATURE, value = 20)
        commandManager.sendTeleprompterStatus(
            status = CommandManager.TeleprompterStatus.STARTED,
            mode = CommandManager.TeleprompterMode.TELEPROMPT,
        )
        commandManager.sendAdjust(
            status = CommandManager.AdjustStatus.SHOW,
            imageType = CommandManager.AdjustImageType.HOME,
        )
        commandManager.sendAiChatSenderText(
            sender = CommandManager.AiChatSender.AI,
            text = "text",
            model = CommandManager.AiChatModel.SABERA_AI,
        )
        commandManager.sendAiChatSenderStatus(
            sender = CommandManager.AiChatSender.AI,
            status = CommandManager.AiChatStatus.GENERATING,
            model = CommandManager.AiChatModel.SABERA_AI,
        )
        commandManager.sendAiChatStatus(status = CommandManager.AiChatStatus.COMPLETE)
    }

    fun ble(context: Context, scope: CoroutineScope, intent: Intent) {
        BleCompanionDeviceService.connectToLastDevice(context = context)

        val selector = BleDeviceSelector(context = context)
        selector.showDialog(scope = scope, singleTarget = false, callback = {})
        selector.onActivityResult(requestCode = 0, resultCode = 0, data = intent, scope = scope)
    }
}
