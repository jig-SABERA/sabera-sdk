---
title: sendCommand
parent: GlassClient
grandparent: API リファレンス
nav_order: 7
---

# GlassClient.sendCommand

```kotlin
suspend fun sendCommand(command: ByteArray)
```

## 概要

ヘッダ・長さ・ペイロードを含む完成したパケットを、通常コマンド用Characteristicへ送る。
パケットの形式と直接送信時の注意事項は[Bluetooth コマンドリスト](../../bluetooth-commands.md)を参照。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `command` | `ByteArray` | ヘッダを含むパケット全体 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: GlassClient.sendCommand -->
```kotlin
// ホーム画面を開く（画面ID 0x0032）
client.sendCommand(byteArrayOf(0x01, 0x05, 0x80.toByte(), 0x05, 0x00, 0x01, 0x02, 0x00, 0x32, 0x00))
```
<!-- /snippet -->

## 関連

- [Bluetooth コマンドリスト](../../bluetooth-commands.md)
- [sendCommandList](send-command-list.md)
