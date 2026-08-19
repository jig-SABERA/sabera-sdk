package jp.jig.sabera.app.docs.snippets

import android.util.Log
import app.jigglass.glass.CommandManager
import app.jigglass.glass.GestureType
import app.jigglass.glass.GlassClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** グラスを操作するコード例。 */
internal object CommandSnippets {

    fun create(client: GlassClient): CommandManager {
        // #snippet GlassClient.createCommandManager
        val commandManager = client.createCommandManager()
        // #endsnippet
        return commandManager
    }

    fun observeGestures(commandManager: CommandManager, scope: CoroutineScope) {
        // #snippet CommandManager.gestureEvents
        scope.launch {
            commandManager.gestureEvents.collect { gesture ->
                when (gesture) {
                    GestureType.SINGLE_TAP -> Log.d("sample", "tap")
                    GestureType.DOUBLE_TAP -> Log.d("sample", "double tap")
                    GestureType.HOLD -> Log.d("sample", "hold")
                }
            }
        }
        // #endsnippet
    }

    fun teleprompter(commandManager: CommandManager) {
        // #snippet CommandManager.sendTeleprompterContent
        commandManager.enterTeleprompterPage()
        commandManager.sendTeleprompterContent("読み上げる原稿")
        // #endsnippet
    }

    fun translate(commandManager: CommandManager) {
        // #snippet CommandManager.sendTranslateLanguage
        commandManager.enterTranslatePage()
        commandManager.sendTranslateLanguage(source = "en", target = "ja")
        commandManager.sendTranslateContent("Hello")
        // #endsnippet
    }

    fun home(commandManager: CommandManager) {
        // #snippet CommandManager.enterHomePage
        commandManager.enterHomePage()
        // #endsnippet
    }

    fun mic(commandManager: CommandManager) {
        // #snippet CommandManager.openGlassMic
        commandManager.openGlassMic()
        // … 録音が終わったら閉じる
        commandManager.closeGlassMic()
        // #endsnippet
    }

    fun notification(commandManager: CommandManager) {
        // #snippet CommandManager.sendMessage
        commandManager.sendMessage(
            name = "Slack",
            title = "山田",
            time = System.currentTimeMillis(),
            text = "会議室を移動しました",
        )
        commandManager.syncNotificationCount(1)
        // #endsnippet
    }

    fun enterAiChat(commandManager: CommandManager) {
        // #snippet CommandManager.enterAiChatPage
        // AI アシスタント（AI チャットページ）をグラスに開かせる
        commandManager.enterAiChatPage()
        // #endsnippet
    }

    // sendAiChatSender / sendAiChatStatus / sendAiChatSenderText / sendAiChatSenderStatus は
    // 引数の PacketCommandUtils.AiChatSender・AiChatStatus が SDK 内部の型で、外から参照できない。
    // アプリから使えるのは以下の2つだけ。
    fun aiChat(commandManager: CommandManager) {
        // #snippet CommandManager.sendAiChatText
        commandManager.enterAiChatPage()
        commandManager.sendAiChatText("今日の天気は？")
        // #endsnippet
    }

    fun imageDisplay(commandManager: CommandManager, grayscale: ByteArray) {
        // #snippet CommandManager.sendImage
        commandManager.enterImageDisplayPage()
        // grayscale は1画素1バイト・左上から行優先。3bitへの量子化とRLE圧縮はSDKが行う
        commandManager.sendImage(width = 196, height = 196, grayscale = grayscale)
        // #endsnippet
    }

    fun enterImageDisplay(commandManager: CommandManager) {
        // #snippet CommandManager.enterImageDisplayPage
        // 技適マークの表示にも使っている画像表示ページ
        commandManager.enterImageDisplayPage()
        // #endsnippet
    }

    fun remoteController(commandManager: CommandManager) {
        // #snippet CommandManager.addRemoteControllerEventListener
        val listener = object : CommandManager.RemoteControlListener {
            override fun onPrev() {
                Log.d("sample", "prev")
            }

            override fun onNext() {
                Log.d("sample", "next")
            }

            override fun onEsc() {
                Log.d("sample", "esc")
            }
        }
        commandManager.addRemoteControllerEventListener(listener)
        // #endsnippet
        commandManager.removeRemoteControllerEventListener(listener)
    }

    fun powerEvent(commandManager: CommandManager) {
        // #snippet CommandManager.addGlassPowerEventListener
        val listener: () -> Unit = { Log.d("sample", "power button") }
        commandManager.addGlassPowerEventListener(listener)
        // #endsnippet
        commandManager.removeGlassPowerEventListener(listener)
    }

    fun deviceInfo(client: GlassClient) {
        // #snippet GlassClient.deviceName
        Log.d("sample", "${client.deviceName} (${client.deviceIdentifier})")
        // #endsnippet
    }

    fun observeConnected(client: GlassClient, scope: CoroutineScope) {
        // #snippet GlassClient.connected
        scope.launch {
            client.connected.collect { connected ->
                Log.d("sample", "connected: $connected")
            }
        }
        // #endsnippet
    }

    suspend fun reboot(client: GlassClient) {
        // #snippet GlassClient.reboot
        client.reboot()
        // #endsnippet
    }

    suspend fun rawCommand(client: GlassClient) {
        // #snippet GlassClient.sendCommand
        client.sendCommand(byteArrayOf(0x4E, 0x00))
        // #endsnippet
    }
}
