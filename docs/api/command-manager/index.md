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
| [imuData](imu-data.html) | `val imuData: SharedFlow<CommandManager.ImuData>` |
| [imuDataStarted](imu-data-started.html) | `val imuDataStarted: StateFlow<Boolean>` |
| [micAudio](mic-audio.html) | `val micAudio: SharedFlow<ByteArray>` |
| [micStreaming](mic-streaming.html) | `val micStreaming: StateFlow<Boolean>` |
| [charging](charging.html) | `val charging: StateFlow<Boolean?>` |
| [enterHomePage](enter-home-page.html) | `fun enterHomePage()` |
| [enterTeleprompterPage](enter-teleprompter-page.html) | `fun enterTeleprompterPage()` |
| [sendTeleprompterContent](send-teleprompter-content.html) | `fun sendTeleprompterContent(content: String)`<br>`fun sendTeleprompterContent(content: String, percent: Int)` |
| [enterTranslatePage](enter-translate-page.html) | `fun enterTranslatePage()` |
| [sendTranslateContent](send-translate-content.html) | `fun sendTranslateContent(content: String)` |
| [sendTranslateLanguage](send-translate-language.html) | `fun sendTranslateLanguage(source: String, target: String)` |
| [enterAiChatPage](enter-ai-chat-page.html) | `fun enterAiChatPage()` |
| [sendAiChatText](send-ai-chat-text.html) | `fun sendAiChatText(text: String)` |
| [sendAiChatStatus](send-ai-chat-status.html) | `fun sendAiChatStatus(status: CommandManager.AiChatStatus)` |
| [sendAiChatSenderText](send-ai-chat-sender-text.html) | `fun sendAiChatSenderText(sender: CommandManager.AiChatSender, text: String, model: CommandManager.AiChatModel? = null)` |
| [sendAiChatSenderStatus](send-ai-chat-sender-status.html) | `fun sendAiChatSenderStatus(sender: CommandManager.AiChatSender, status: CommandManager.AiChatStatus, model: CommandManager.AiChatModel? = null)` |
| [openGlassMic](open-glass-mic.html) | `fun openGlassMic()` |
| [closeGlassMic](close-glass-mic.html) | `fun closeGlassMic()` |
| [startMicStreaming](start-mic-streaming.html) | `fun startMicStreaming()` |
| [stopMicStreaming](stop-mic-streaming.html) | `fun stopMicStreaming()` |
| [sendMessage](send-message.html) | `fun sendMessage(name: String, title: String, time: Long, text: String)` |
| [syncNotificationCount](sync-notification-count.html) | `fun syncNotificationCount(count: Int)` |
| [sendDebugPhoneName](send-debug-phone-name.html) | `fun sendDebugPhoneName(phoneName: String)` |
| [parseResponse](parse-response.html) | `fun parseResponse(value: ByteArray)` |
| [enterEmptyScreenPage](enter-empty-screen-page.html) | `fun enterEmptyScreenPage()` |
| [enterImageDisplayPage](enter-image-display-page.html) | `fun enterImageDisplayPage()` |
| [sendLayout](send-layout.html) | `fun sendLayout(mode: CommandManager.LayoutMode, texts: Map<Int, String> = emptyMap())` |
| [sendLayoutTexts](send-layout-texts.html) | `fun sendLayoutTexts(texts: Map<Int, String>)` |
| [closeLayout](close-layout.html) | `fun closeLayout()` |
| [sendCanvas](send-canvas.html) | `fun sendCanvas(elements: List<CommandManager.CanvasElement>)` |
| [sendCanvasElements](send-canvas-elements.html) | `fun sendCanvasElements(elements: List<CommandManager.CanvasElement>)` |
| [sendCanvasImage](send-canvas-image.html) | `fun sendCanvasImage(id: Int, x: Int, y: Int, width: Int, height: Int, grayscale: ByteArray)` |
| [removeCanvasImage](remove-canvas-image.html) | `fun removeCanvasImage(id: Int)` |
| [clearCanvas](clear-canvas.html) | `fun clearCanvas()` |
| [closeCanvas](close-canvas.html) | `fun closeCanvas()` |
| [startCanvasAnimation](start-canvas-animation.html) | `fun startCanvasAnimation(x: Int, y: Int, width: Int, height: Int, intervalMs: Int)` |
| [sendCanvasAnimationFrame](send-canvas-animation-frame.html) | `fun sendCanvasAnimationFrame(width: Int, height: Int, grayscale: ByteArray)` |
| [stopCanvasAnimation](stop-canvas-animation.html) | `fun stopCanvasAnimation()` |
| [enterNavigationPage](enter-navigation-page.html) | `fun enterNavigationPage()` |
| [enterGlassAngleAdjustmentPage](enter-glass-angle-adjustment-page.html) | `fun enterGlassAngleAdjustmentPage()` |
| [enterImuDebugPage](enter-imu-debug-page.html) | `fun enterImuDebugPage()` |
| [sendTeleprompterLine](send-teleprompter-line.html) | `fun sendTeleprompterLine(text: String, percent: Int, scrollUp: Boolean = false)` |
| [sendTeleprompterStatus](send-teleprompter-status.html) | `fun sendTeleprompterStatus(status: CommandManager.TeleprompterStatus, mode: CommandManager.TeleprompterMode)` |
| [sendTeleprompterTime](send-teleprompter-time.html) | `fun sendTeleprompterTime(time: String)` |
| [clearInscriptionText](clear-inscription-text.html) | `fun clearInscriptionText()` |
| [sendEmptyScreenContent](send-empty-screen-content.html) | `fun sendEmptyScreenContent(content: String)` |
| [sendImage](send-image.html) | `fun sendImage(width: Int, height: Int, grayscale: ByteArray)` |
| [sendAiChatLanguage](send-ai-chat-language.html) | `fun sendAiChatLanguage(languageCode: String)` |
| [clearAiChat](clear-ai-chat.html) | `fun clearAiChat()` |
| [clearAiChatLegacy](clear-ai-chat-legacy.html) | `fun clearAiChatLegacy()` |
| [sendNaviStatus](send-navi-status.html) | `fun sendNaviStatus(status: CommandManager.NaviStatus)` |
| [sendNaviCourse](send-navi-course.html) | `fun sendNaviCourse(courseDegrees: Double)` |
| [sendNaviLanguage](send-navi-language.html) | `fun sendNaviLanguage(languageCode: String)` |
| [sendNavi](send-navi.html) | `fun sendNavi(maneuverIcon: CommandManager.ManeuverIcon, instructionText: String, distanceText: String, estimatedArrivalText: String, timeAndDistanceText: String, bitmapWidth: Int? = null, bitmapHeight: Int? = null, grayscale: ByteArray? = null)` |
| [sendNaviLargeImage](send-navi-large-image.html) | `fun sendNaviLargeImage(width: Int, height: Int, grayscale: ByteArray)` |
| [sendAdjust](send-adjust.html) | `fun sendAdjust(status: CommandManager.AdjustStatus, imageType: CommandManager.AdjustImageType)` |
| [sendWakeupTiltThreshold](send-wakeup-tilt-threshold.html) | `fun sendWakeupTiltThreshold(degrees: Int)` |
| [sendSettingPageVisibility](send-setting-page-visibility.html) | `fun sendSettingPageVisibility(show: Boolean)` |
| [sendSetting](send-setting.html) | `fun sendSetting(name: String, value: Int)`<br>`fun sendSetting(name: String, value: Boolean)`<br>`fun sendSetting(name: String, value: String)`<br>`fun sendSetting(name: String, value: ByteArray)` |
| [requestSettingSync](request-setting-sync.html) | `fun requestSettingSync()` |
| [startImuData](start-imu-data.html) | `fun startImuData()` |
| [stopImuData](stop-imu-data.html) | `fun stopImuData()` |
| [syncTime](sync-time.html) | `fun syncTime()` |
| [syncWeather](sync-weather.html) | `fun syncWeather(type: CommandManager.WeatherType, value: Int)` |
| [requestSystemStatus](request-system-status.html) | `fun requestSystemStatus()` |
