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

`time` は **epoch 秒** で渡す。`System.currentTimeMillis()` の値をそのまま渡すとグラスの時刻表示が壊れる（1000 で割る）。
見出しに表示されるのは `title`。`name` はアプリ名として扱われ、見出しには出ない。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `name` | `String` | 通知を出したアプリの名前 |
| `title` | `String` | 送信者名など、通知の見出し |
| `time` | `Long` | 通知が届いた時刻。epoch 秒（ミリ秒ではない） |
| `text` | `String` | 本文 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendMessage -->
```kotlin
commandManager.sendMessage(
    name = "Slack",
    title = "山田",
    // グラスは epoch 秒を読む。ミリ秒のまま渡すと時刻表示が壊れる
    time = System.currentTimeMillis() / 1000,
    text = "会議室を移動しました",
)
commandManager.syncNotificationCount(1)
```
<!-- /snippet -->

## 関連

- [syncNotificationCount](sync-notification-count.md)
