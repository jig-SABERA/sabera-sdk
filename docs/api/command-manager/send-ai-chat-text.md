---
title: sendAiChatText
parent: CommandManager
grandparent: API リファレンス
nav_order: 15
---

# CommandManager.sendAiChatText

```kotlin
fun sendAiChatText(text: String)
```

## 概要

AI チャットページの本文にテキストを追記する。enterAiChatPage でページを開いてから呼ぶ。

{: .note }
> 0.0.10 では送信者（あなた／AI）と生成中・完了の状態を指定できない。
> 次のリリースからは sendAiChatSenderText / sendAiChatSenderStatus を使う。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `text` | `String` | グラスに表示する本文 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendAiChatText -->
```kotlin
commandManager.enterAiChatPage()
commandManager.sendAiChatText("今日の天気は？")
```
<!-- /snippet -->

## 関連

- [enterAiChatPage](enter-ai-chat-page.html)
