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
| [connected](connected.md) | `val connected: StateFlow<Boolean>` |
| [deviceName](device-name.md) | `val deviceName: String?` |
| [deviceIdentifier](device-identifier.md) | `val deviceIdentifier: String?` |
| [isConnectionValid](is-connection-valid.md) | `fun isConnectionValid(): Boolean` |
| [createCommandManager](create-command-manager.md) | `fun createCommandManager(): CommandManager` |
| [micChannel](mic-channel.md) | `val micChannel: StateFlow<Int?>`<br>`fun setMicChannel(channel: Int?)` |
| [sendCommand](send-command.md) | `suspend fun sendCommand(command: ByteArray)` |
| [sendCommandList](send-command-list.md) | `suspend fun sendCommandList(command: List<ByteArray>)` |
| [sendText](send-text.md) | `suspend fun sendText(page: Byte, content: String)` |
| [cancelPendingPackets](cancel-pending-packets.md) | `suspend fun cancelPendingPackets()` |
| [reboot](reboot.md) | `suspend fun reboot()` |
