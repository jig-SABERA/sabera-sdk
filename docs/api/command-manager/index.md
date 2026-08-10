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
| [sendTeleprompterContent](send-teleprompter-content.html) | `fun sendTeleprompterContent(content: String)` |
| [enterAIPage](enter-ai-page.html) | `fun enterAIPage(isAiPower: Boolean = false)` |
| [sendAIContent](send-ai-content.html) | `fun sendAIContent(content: String)` |
| [enterTranslatePage](enter-translate-page.html) | `fun enterTranslatePage()` |
| [sendTranslateContent](send-translate-content.html) | `fun sendTranslateContent(content: String)` |
| [sendTranslateLanguage](send-translate-language.html) | `fun sendTranslateLanguage(source: String, target: String)` |
| [enterMeetingPage](enter-meeting-page.html) | `fun enterMeetingPage()` |
| [sendMeeting](send-meeting.html) | `fun sendMeeting(meetingType: Byte, text: String, percent: Int)` |
| [enterAiChatPage](enter-ai-chat-page.html) | `fun enterAiChatPage()` |
| [sendAiChatSender](send-ai-chat-sender.html) | `fun sendAiChatSender(sender: PacketCommandUtils.AiChatSender)` |
| [sendAiChatText](send-ai-chat-text.html) | `fun sendAiChatText(text: String)` |
| [sendAiChatStatus](send-ai-chat-status.html) | `fun sendAiChatStatus(status: PacketCommandUtils.AiChatStatus)` |
| [sendAiChatSenderText](send-ai-chat-sender-text.html) | `fun sendAiChatSenderText(sender: PacketCommandUtils.AiChatSender, text: String)` |
| [sendAiChatSenderStatus](send-ai-chat-sender-status.html) | `fun sendAiChatSenderStatus(sender: PacketCommandUtils.AiChatSender, status: PacketCommandUtils.AiChatStatus)` |
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
