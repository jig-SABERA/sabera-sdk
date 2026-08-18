---
title: sendAiChatSender
parent: CommandManager
grandparent: API リファレンス
nav_order: 14
---

# CommandManager.sendAiChatSender

```kotlin
fun sendAiChatSender(sender: CommandManager.AiChatSender)
```

## 概要

次に送る本文の吹き出しをどちら側にするかを切り替える。本文と一度に送る sendAiChatSenderText の方が確実。

{: .note }
> 0.0.10 では引数の型が SDK 内部の型のため呼び出せない。次のリリースで `CommandManager` の入れ子 enum に変わり、アプリから呼べるようになる。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `sender` | `CommandManager.AiChatSender` | 吹き出しの主体。`USER` か `AI` |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendAiChatSender -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendAiChatSenderText](send-ai-chat-sender-text.html)
