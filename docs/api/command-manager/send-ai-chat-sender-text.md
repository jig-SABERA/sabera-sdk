---
title: sendAiChatSenderText
parent: CommandManager
grandparent: API リファレンス
nav_order: 17
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

{: .note }
> 0.0.10 では引数の型が SDK 内部の型のため呼び出せない。次のリリースで `CommandManager` の入れ子 enum に変わり、アプリから呼べるようになる。

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
