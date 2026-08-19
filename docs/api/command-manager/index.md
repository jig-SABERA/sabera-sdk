---
title: CommandManager
parent: API リファレンス
nav_order: 4
has_children: true
---

# CommandManager

グラス側の画面遷移・コンテンツ送信・イベント購読。`GlassClient.createCommandManager()` で作る。

| メソッド | シグネチャ |
|---|---|
| [connected](connected.html) | `val connected: StateFlow<Boolean>` |
| [gestureEvents](gesture-events.html) | `val gestureEvents: SharedFlow<GestureType>` |
| [enterHomePage](enter-home-page.html) | `fun enterHomePage()` |
| [enterTeleprompterPage](enter-teleprompter-page.html) | `fun enterTeleprompterPage()` |
| [sendTeleprompterContent](send-teleprompter-content.html) | `fun sendTeleprompterContent(content: String)
fun sendTeleprompterContent(content: String, percent: Int)` |
| [enterAIPage](enter-ai-page.html) | `fun enterAIPage(isAiPower: Boolean = false)` |
| [sendAIContent](send-ai-content.html) | `fun sendAIContent(content: String)` |
| [enterTranslatePage](enter-translate-page.html) | `fun enterTranslatePage()` |
| [sendTranslateContent](send-translate-content.html) | `fun sendTranslateContent(content: String)` |
| [sendTranslateLanguage](send-translate-language.html) | `fun sendTranslateLanguage(source: String, target: String)` |
| [enterMeetingPage](enter-meeting-page.html) | `fun enterMeetingPage()` |
| [sendMeeting](send-meeting.html) | `fun sendMeeting(meetingType: Byte, text: String, percent: Int)` |
| [enterAiChatPage](enter-ai-chat-page.html) | `fun enterAiChatPage()` |
| [sendAiChatSender](send-ai-chat-sender.html) | `fun sendAiChatSender(sender: CommandManager.AiChatSender)` |
| [sendAiChatText](send-ai-chat-text.html) | `fun sendAiChatText(text: String)` |
| [sendAiChatStatus](send-ai-chat-status.html) | `fun sendAiChatStatus(status: CommandManager.AiChatStatus)` |
| [sendAiChatSenderText](send-ai-chat-sender-text.html) | `fun sendAiChatSenderText(
    sender: CommandManager.AiChatSender,
    text: String,
    model: CommandManager.AiChatModel? = null,
)` |
| [sendAiChatSenderStatus](send-ai-chat-sender-status.html) | `fun sendAiChatSenderStatus(
    sender: CommandManager.AiChatSender,
    status: CommandManager.AiChatStatus,
    model: CommandManager.AiChatModel? = null,
)` |
| [openGlassMic](open-glass-mic.html) | `fun openGlassMic()` |
| [closeGlassMic](close-glass-mic.html) | `fun closeGlassMic()` |
| [sendMessage](send-message.html) | `fun sendMessage(sender: String, body: String, timestamp: Long, appName: String)` |
| [syncNotificationCount](sync-notification-count.html) | `fun syncNotificationCount(count: Int)` |
| [sendDebugPhoneName](send-debug-phone-name.html) | `fun sendDebugPhoneName(phoneName: String)` |
| [addGlassPowerEventListener](add-glass-power-event-listener.html) | `fun addGlassPowerEventListener(listener: () -> Unit)` |
| [removeGlassPowerEventListener](remove-glass-power-event-listener.html) | `fun removeGlassPowerEventListener(listener: () -> Unit)` |
| [addRemoteControllerEventListener](add-remote-controller-event-listener.html) | `fun addRemoteControllerEventListener(listener: CommandManager.RemoteControlListener)` |
| [removeRemoteControllerEventListener](remove-remote-controller-event-listener.html) | `fun removeRemoteControllerEventListener(listener: CommandManager.RemoteControlListener)` |
| [parseResponse](parse-response.html) | `fun parseResponse(value: ByteArray)` |
| [enterNotificationPage](enter-notification-page.html) | `fun enterNotificationPage()` |
| [enterEmptyScreenPage](enter-empty-screen-page.html) | `fun enterEmptyScreenPage()` |
| [enterImageDisplayPage](enter-image-display-page.html) | `fun enterImageDisplayPage()` |
| [enterGlassAngleAdjustmentPage](enter-glass-angle-adjustment-page.html) | `fun enterGlassAngleAdjustmentPage()` |
| [enterImuDebugPage](enter-imu-debug-page.html) | `fun enterImuDebugPage()` |
| [sendTeleprompterLine](send-teleprompter-line.html) | `fun sendTeleprompterLine(text: String, percent: Int, scrollUp: Boolean = false)` |
| [sendTeleprompterStatus](send-teleprompter-status.html) | `fun sendTeleprompterStatus(
    status: CommandManager.TeleprompterStatus,
    mode: CommandManager.TeleprompterMode,
)` |
| [sendTeleprompterTime](send-teleprompter-time.html) | `fun sendTeleprompterTime(time: String)` |
| [sendTeleprompterGenerating](send-teleprompter-generating.html) | `fun sendTeleprompterGenerating()` |
| [clearInscriptionText](clear-inscription-text.html) | `fun clearInscriptionText()` |
| [sendEmptyScreenContent](send-empty-screen-content.html) | `fun sendEmptyScreenContent(content: String)` |
| [sendEmptyScreenStatus](send-empty-screen-status.html) | `fun sendEmptyScreenStatus(status: CommandManager.TeleprompterStatus)` |
| [sendImage](send-image.html) | `fun sendImage(width: Int, height: Int, grayscale: ByteArray)` |
| [sendAiChatLanguage](send-ai-chat-language.html) | `fun sendAiChatLanguage(languageCode: String)` |
| [clearAiChat](clear-ai-chat.html) | `fun clearAiChat()` |
| [clearAiChatLegacy](clear-ai-chat-legacy.html) | `fun clearAiChatLegacy()` |
| [sendAdjust](send-adjust.html) | `fun sendAdjust(
    status: CommandManager.AdjustStatus,
    imageType: CommandManager.AdjustImageType,
)` |
| [sendWakeupTiltThreshold](send-wakeup-tilt-threshold.html) | `fun sendWakeupTiltThreshold(degrees: Int)` |
| [sendSettingPageVisibility](send-setting-page-visibility.html) | `fun sendSettingPageVisibility(show: Boolean)` |
| [sendSetting](send-setting.html) | `fun sendSetting(name: String, value: Int)
fun sendSetting(name: String, value: Boolean)
fun sendSetting(name: String, value: String)
fun sendSetting(name: String, value: ByteArray)` |
| [requestSettingSync](request-setting-sync.html) | `fun requestSettingSync()` |
| [requestLog](request-log.html) | `fun requestLog(type: CommandManager.GlassLogType)` |
| [requestNotificationCountSync](request-notification-count-sync.html) | `fun requestNotificationCountSync()` |
| [syncTime](sync-time.html) | `fun syncTime()` |
| [syncWeather](sync-weather.html) | `fun syncWeather(type: CommandManager.WeatherType, value: Int)` |
| [requestSystemStatus](request-system-status.html) | `fun requestSystemStatus()` |
