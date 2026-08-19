---
title: sendAiChatStatus
parent: CommandManager
grandparent: API リファレンス
nav_order: 18
---

# CommandManager.sendAiChatStatus

```kotlin
fun sendAiChatStatus(status: CommandManager.AiChatStatus)
```

## 概要

AI 応答の生成状態を送る。`COMPLETE` を送るまでグラスは生成中の表示を続ける。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `status` | `CommandManager.AiChatStatus` | `GENERATING` か `COMPLETE` |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendAiChatStatus -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendAiChatSenderStatus](send-ai-chat-sender-status.html)
