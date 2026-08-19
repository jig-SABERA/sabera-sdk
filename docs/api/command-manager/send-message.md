---
title: sendMessage
parent: CommandManager
grandparent: API リファレンス
nav_order: 25
---

# CommandManager.sendMessage

{: .warning }
> このページは執筆中です。

```kotlin
fun sendMessage(sender: String, body: String, timestamp: Long, appName: String)
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `sender` | `String` | <!-- WIP --> |
| `body` | `String` | <!-- WIP --> |
| `timestamp` | `Long` | <!-- WIP --> |
| `appName` | `String` | <!-- WIP --> |

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
