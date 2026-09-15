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
| [connected](connected.md) | `val connected: StateFlow<Boolean>` |
| [gestureEvents](gesture-events.md) | `val gestureEvents: SharedFlow<GestureType>` |
| [imuData](imu-data.md) | `val imuData: SharedFlow<CommandManager.ImuData>` |
| [imuDataStarted](imu-data-started.md) | `val imuDataStarted: StateFlow<Boolean>` |
| [micAudio](mic-audio.md) | `val micAudio: SharedFlow<ByteArray>` |
| [micStreaming](mic-streaming.md) | `val micStreaming: StateFlow<Boolean>` |
| [charging](charging.md) | `val charging: StateFlow<Boolean?>` |
| [enterHomePage](enter-home-page.md) | `fun enterHomePage()` |
| [enterTeleprompterPage](enter-teleprompter-page.md) | `fun enterTeleprompterPage()` |
| [sendTeleprompterContent](send-teleprompter-content.md) | `fun sendTeleprompterContent(content: String)`<br>`fun sendTeleprompterContent(content: String, percent: Int)` |
| [enterTranslatePage](enter-translate-page.md) | `fun enterTranslatePage()` |
| [sendTranslateContent](send-translate-content.md) | `fun sendTranslateContent(content: String)` |
| [sendTranslateLanguage](send-translate-language.md) | `fun sendTranslateLanguage(source: String, target: String)` |
| [enterAiChatPage](enter-ai-chat-page.md) | `fun enterAiChatPage()` |
| [sendAiChatText](send-ai-chat-text.md) | `fun sendAiChatText(text: String)` |
| [sendAiChatStatus](send-ai-chat-status.md) | `fun sendAiChatStatus(status: CommandManager.AiChatStatus)` |
| [sendAiChatSenderText](send-ai-chat-sender-text.md) | `fun sendAiChatSenderText(sender: CommandManager.AiChatSender, text: String, model: CommandManager.AiChatModel? = null)` |
| [sendAiChatSenderStatus](send-ai-chat-sender-status.md) | `fun sendAiChatSenderStatus(sender: CommandManager.AiChatSender, status: CommandManager.AiChatStatus, model: CommandManager.AiChatModel? = null)` |
| [openGlassMic](open-glass-mic.md) | `fun openGlassMic()` |
| [closeGlassMic](close-glass-mic.md) | `fun closeGlassMic()` |
| [startMicStreaming](start-mic-streaming.md) | `fun startMicStreaming()` |
| [stopMicStreaming](stop-mic-streaming.md) | `fun stopMicStreaming()` |
| [sendMessage](send-message.md) | `fun sendMessage(name: String, title: String, time: Long, text: String)` |
| [syncNotificationCount](sync-notification-count.md) | `fun syncNotificationCount(count: Int)` |
| [sendDebugPhoneName](send-debug-phone-name.md) | `fun sendDebugPhoneName(phoneName: String)` |
| [parseResponse](parse-response.md) | `fun parseResponse(value: ByteArray)` |
| [enterEmptyScreenPage](enter-empty-screen-page.md) | `fun enterEmptyScreenPage()` |
| [enterImageDisplayPage](enter-image-display-page.md) | `fun enterImageDisplayPage()` |
| [sendLayout](send-layout.md) | `fun sendLayout(mode: CommandManager.LayoutMode, texts: Map<Int, String> = emptyMap())` |
| [sendLayoutTexts](send-layout-texts.md) | `fun sendLayoutTexts(texts: Map<Int, String>)` |
| [closeLayout](close-layout.md) | `fun closeLayout()` |
| [sendCanvas](send-canvas.md) | `fun sendCanvas(elements: List<CommandManager.CanvasElement>)` |
| [sendCanvasElements](send-canvas-elements.md) | `fun sendCanvasElements(elements: List<CommandManager.CanvasElement>)` |
| [sendCanvasImage](send-canvas-image.md) | `fun sendCanvasImage(id: Int, x: Int, y: Int, width: Int, height: Int, grayscale: ByteArray)` |
| [removeCanvasImage](remove-canvas-image.md) | `fun removeCanvasImage(id: Int)` |
| [clearCanvas](clear-canvas.md) | `fun clearCanvas()` |
| [closeCanvas](close-canvas.md) | `fun closeCanvas()` |
| [startCanvasAnimation](start-canvas-animation.md) | `fun startCanvasAnimation(x: Int, y: Int, width: Int, height: Int, intervalMs: Int)` |
| [sendCanvasAnimationFrame](send-canvas-animation-frame.md) | `fun sendCanvasAnimationFrame(width: Int, height: Int, grayscale: ByteArray)` |
| [stopCanvasAnimation](stop-canvas-animation.md) | `fun stopCanvasAnimation()` |
| [enterNavigationPage](enter-navigation-page.md) | `fun enterNavigationPage()` |
| [enterGlassAngleAdjustmentPage](enter-glass-angle-adjustment-page.md) | `fun enterGlassAngleAdjustmentPage()` |
| [enterImuDebugPage](enter-imu-debug-page.md) | `fun enterImuDebugPage()` |
| [sendTeleprompterLine](send-teleprompter-line.md) | `fun sendTeleprompterLine(text: String, percent: Int, scrollUp: Boolean = false)` |
| [sendTeleprompterStatus](send-teleprompter-status.md) | `fun sendTeleprompterStatus(status: CommandManager.TeleprompterStatus, mode: CommandManager.TeleprompterMode)` |
| [sendTeleprompterTime](send-teleprompter-time.md) | `fun sendTeleprompterTime(time: String)` |
| [clearInscriptionText](clear-inscription-text.md) | `fun clearInscriptionText()` |
| [sendEmptyScreenContent](send-empty-screen-content.md) | `fun sendEmptyScreenContent(content: String)` |
| [sendImage](send-image.md) | `fun sendImage(width: Int, height: Int, grayscale: ByteArray)` |
| [sendAiChatLanguage](send-ai-chat-language.md) | `fun sendAiChatLanguage(languageCode: String)` |
| [clearAiChat](clear-ai-chat.md) | `fun clearAiChat()` |
| [clearAiChatLegacy](clear-ai-chat-legacy.md) | `fun clearAiChatLegacy()` |
| [sendNaviStatus](send-navi-status.md) | `fun sendNaviStatus(status: CommandManager.NaviStatus)` |
| [sendNaviCourse](send-navi-course.md) | `fun sendNaviCourse(courseDegrees: Double)` |
| [sendNaviLanguage](send-navi-language.md) | `fun sendNaviLanguage(languageCode: String)` |
| [sendNavi](send-navi.md) | `fun sendNavi(maneuverIcon: CommandManager.ManeuverIcon, instructionText: String, distanceText: String, estimatedArrivalText: String, timeAndDistanceText: String, bitmapWidth: Int? = null, bitmapHeight: Int? = null, grayscale: ByteArray? = null)` |
| [sendNaviLargeImage](send-navi-large-image.md) | `fun sendNaviLargeImage(width: Int, height: Int, grayscale: ByteArray)` |
| [sendAdjust](send-adjust.md) | `fun sendAdjust(status: CommandManager.AdjustStatus, imageType: CommandManager.AdjustImageType)` |
| [sendWakeupTiltThreshold](send-wakeup-tilt-threshold.md) | `fun sendWakeupTiltThreshold(degrees: Int)` |
| [sendSettingPageVisibility](send-setting-page-visibility.md) | `fun sendSettingPageVisibility(show: Boolean)` |
| [sendSetting](send-setting.md) | `fun sendSetting(name: String, value: Int)`<br>`fun sendSetting(name: String, value: Boolean)`<br>`fun sendSetting(name: String, value: String)`<br>`fun sendSetting(name: String, value: ByteArray)` |
| [requestSettingSync](request-setting-sync.md) | `fun requestSettingSync()` |
| [startImuData](start-imu-data.md) | `fun startImuData()` |
| [stopImuData](stop-imu-data.md) | `fun stopImuData()` |
| [syncTime](sync-time.md) | `fun syncTime()` |
| [syncWeather](sync-weather.md) | `fun syncWeather(type: CommandManager.WeatherType, value: Int)` |
| [requestSystemStatus](request-system-status.md) | `fun requestSystemStatus()` |
