---
title: sendAiChatStatus
parent: CommandManager
grandparent: API リファレンス
nav_order: 16
---

# CommandManager.sendAiChatStatus

```kotlin
fun sendAiChatStatus(status: CommandManager.AiChatStatus)
```

## 概要

AI 応答の生成状態を送る。`COMPLETE` を送るまでグラスは生成中の表示を続ける。

{: .note }
> 0.0.10 では引数の型が SDK 内部の型のため呼び出せない。次のリリースで `CommandManager` の入れ子 enum に変わり、アプリから呼べるようになる。

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
