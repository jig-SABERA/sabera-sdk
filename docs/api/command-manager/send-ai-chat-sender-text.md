---
title: sendAiChatSenderText
parent: CommandManager
grandparent: API リファレンス
nav_order: 19
---

# CommandManager.sendAiChatSenderText

```kotlin
fun sendAiChatSenderText(
    sender: CommandManager.AiChatSender,
    text: String,
    model: CommandManager.AiChatModel? = null,
)
```

## 概要

吹き出しの主体と本文をまとめて送る。質問は `USER`、回答は `AI` で送る。1パケットに収まらない長文は sendAiChatText で続きを流す。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `sender` | `CommandManager.AiChatSender` | 吹き出しの主体 |
| `text` | `String` | 表示する本文 |
| `model` | `CommandManager.AiChatModel?` | `AI` のときに表示する生成モデル |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendAiChatSenderText -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendAiChatText](send-ai-chat-text.html)
