---
title: enterAiChatPage
parent: CommandManager
grandparent: API リファレンス
nav_order: 15
---

# CommandManager.enterAiChatPage

```kotlin
fun enterAiChatPage()
```

## 概要

AI アシスタントページを開く。吹き出しは sendAiChatSenderText で送る。本文のフォントが言語で変わるため、開く前に sendAiChatLanguage を送っておく。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.enterAiChatPage -->
```kotlin
// AI アシスタント（AI チャットページ）をグラスに開かせる
commandManager.enterAiChatPage()
```
<!-- /snippet -->

## 関連

- [sendAiChatSenderText](send-ai-chat-sender-text.html)
- [sendAiChatLanguage](send-ai-chat-language.html)
