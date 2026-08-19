---
title: enterAiChatPage
parent: CommandManager
grandparent: API リファレンス
nav_order: 13
---

# CommandManager.enterAiChatPage

```kotlin
fun enterAiChatPage()
```

## 概要

グラスに AI チャットページを開かせる。グラス上の AI アシスタントはこのページで、
AI アシスタントを起動したいときはこちらを呼ぶ。

開いたあとの本文送信は sendAiChatText。

{: .note }
> 質問文と回答の振り分けは sendAiChatSenderText、生成中と完了の通知は sendAiChatSenderStatus、
> 本文フォントを決める言語通知は sendAiChatLanguage で送る。

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

- [sendAiChatText](send-ai-chat-text.html)
