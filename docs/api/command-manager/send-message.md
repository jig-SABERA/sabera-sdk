---
title: sendMessage
parent: CommandManager
grandparent: API リファレンス
nav_order: 23
---

# CommandManager.sendMessage

```kotlin
fun sendMessage(name: String, title: String, time: Long, text: String)
```

## 概要

スマホに届いた通知をグラスに転送する。件数の表示は syncNotificationCount が別にある。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `name` | `String` | 通知を出したアプリの名前 |
| `title` | `String` | 送信者名など、通知の見出し |
| `time` | `Long` | 通知が届いた時刻。エポックミリ秒 |
| `text` | `String` | 本文 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendMessage -->
```kotlin
commandManager.sendMessage(
    name = "Slack",
    title = "山田",
    time = System.currentTimeMillis(),
    text = "会議室を移動しました",
)
commandManager.syncNotificationCount(1)
```
<!-- /snippet -->

## 関連

- [syncNotificationCount](sync-notification-count.html)
