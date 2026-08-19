---
title: sendAIContent
parent: CommandManager
grandparent: API リファレンス
nav_order: 7
---

# CommandManager.sendAIContent

{: .warning }
> このページは執筆中です。

```kotlin
fun sendAIContent(content: String)
```

## 概要

旧ファームの AI ページに本文を送る。AI チャットページへの送信は sendAiChatText。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `content` | `String` | <!-- WIP --> |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendAIContent -->
```kotlin
commandManager.enterAIPage()
commandManager.sendAIContent("AI の回答")
```
<!-- /snippet -->

## 関連

- [enterAIPage](enter-ai-page.html)
- [sendAiChatText](send-ai-chat-text.html)
