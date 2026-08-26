---
title: sendAiChatSenderStatus
parent: CommandManager
grandparent: API リファレンス
nav_order: 18
---

# CommandManager.sendAiChatSenderStatus

```kotlin
fun sendAiChatSenderStatus(
    sender: CommandManager.AiChatSender,
    status: CommandManager.AiChatStatus,
    model: CommandManager.AiChatModel? = null,
)
```

## 概要

吹き出しの主体と生成状態をまとめて送る。回答を流し始める前に `AI` と `GENERATING` を送る。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `sender` | `CommandManager.AiChatSender` | 吹き出しの主体 |
| `status` | `CommandManager.AiChatStatus` | 生成中か完了か |
| `model` | `CommandManager.AiChatModel?` | `AI` のときに表示する生成モデル |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendAiChatSenderStatus -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendAiChatText](send-ai-chat-text.html)
