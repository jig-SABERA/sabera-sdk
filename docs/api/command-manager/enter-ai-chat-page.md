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
[enterAIPage](enter-ai-page.html) が開く AI ページとは別物。AI アシスタントを起動したいときは
こちらを呼ぶ。

開いたあとの本文送信は sendAiChatText。

{: .note }
> 0.0.10 では、質問文と回答を「あなた」「AI」に振り分ける SENDER、生成中／完了を伝える STATUS、
> 本文フォントを決める言語通知が送れない。引数の型が SDK 内部の型だったため。
> 次のリリースからは sendAiChatSenderText / sendAiChatSenderStatus / sendAiChatLanguage で送れる。

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
