---
title: GlassClient
parent: API リファレンス
nav_order: 3
has_children: true
---

# GlassClient

接続済みの1台を表す。`GlassManager.connectedDevice` から得る。

| メソッド | シグネチャ |
|---|---|
| [connected](connected.html) | `val connected: StateFlow<Boolean>` |
| [deviceName](device-name.html) | `val deviceName: String?` |
| [deviceIdentifier](device-identifier.html) | `val deviceIdentifier: String?` |
| [isConnectionValid](is-connection-valid.html) | `fun isConnectionValid(): Boolean` |
| [createCommandManager](create-command-manager.html) | `fun createCommandManager(): CommandManager` |
| [micChannel](mic-channel.html) | `val micChannel: StateFlow<Int?>`<br>`fun setMicChannel(channel: Int?)` |
| [sendCommand](send-command.html) | `suspend fun sendCommand(command: ByteArray)` |
| [sendCommandList](send-command-list.html) | `suspend fun sendCommandList(command: List<ByteArray>)` |
| [sendText](send-text.html) | `suspend fun sendText(page: Byte, content: String)` |
| [cancelPendingPackets](cancel-pending-packets.html) | `suspend fun cancelPendingPackets()` |
| [reboot](reboot.html) | `suspend fun reboot()` |
