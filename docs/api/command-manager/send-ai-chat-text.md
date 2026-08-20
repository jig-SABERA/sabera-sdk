---
title: sendAiChatText
parent: CommandManager
grandparent: API リファレンス
nav_order: 14
---

# CommandManager.sendAiChatText

```kotlin
fun sendAiChatText(text: String)
```

## 概要

AI アシスタントページに本文だけを送る。どちらの吹き出しに出すかも指定できる sendAiChatSenderText の方が確実。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `text` | `String` | 表示する本文 |

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
- [sendAiChatSenderText](send-ai-chat-sender-text.html)
