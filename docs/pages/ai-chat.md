---
title: AI アシスタント
parent: ページごとの使い方
nav_order: 3
---

# AI アシスタント

質問と回答を吹き出しで並べる画面。生成はアプリ側で行い、テキストと状態を送る。

## 開く

本文のフォントは言語で変わり、グラスは開いた時点のフォントを使う。そのため
`sendAiChatLanguage()` を先に送ってから `enterAiChatPage()` を呼ぶ。言語コードは
`"JPN"` / `"ENG"` / `"CHS"` / `"CHT"` などの3文字。

## 吹き出しを送る

`sendAiChatSenderText()` は主体と本文をまとめて送る。質問は `USER`、回答は `AI`。
`AI` のときは `model` を渡すと生成モデル名が出る。

回答を流し始める前に `sendAiChatSenderStatus()` で `AI` と `GENERATING` を送り、
書き終わったら `sendAiChatStatus(COMPLETE)` を送る。`COMPLETE` を送るまでグラスは
生成中の表示を続ける。

<!-- snippet: pages.ai-chat -->
```kotlin
// フォントを先に確定させるため、開く前に言語を送る
commandManager.sendAiChatLanguage("JPN")
commandManager.enterAiChatPage()

commandManager.sendAiChatSenderText(sender = CommandManager.AiChatSender.USER, text = question)
commandManager.sendAiChatSenderStatus(
    sender = CommandManager.AiChatSender.AI,
    status = CommandManager.AiChatStatus.GENERATING,
)
commandManager.sendAiChatSenderText(sender = CommandManager.AiChatSender.AI, text = answer)
commandManager.sendAiChatStatus(CommandManager.AiChatStatus.COMPLETE)
```
<!-- /snippet -->

1パケットに収まらない長文は、続きを `sendAiChatText()` で流す。こちらは主体を指定しない
ので、最初の吹き出しは `sendAiChatSenderText()` で作る。

## 消す・やめる

<!-- snippet: pages.ai-chat-clear -->
```kotlin
commandManager.clearAiChat()
```
<!-- /snippet -->

`clearAiChat()` は FEATURE_VERSION 1.1.0 以降のファームが対象。未対応のファームは
このコマンドを読み捨てて表示が残るため、`clearAiChatLegacy()` で改行を流し込んで
見かけ上クリアする。

ページを離れるときは `enterHomePage()`。

## 関連 API

- `sendAiChatLanguage(languageCode: String)`
- `enterAiChatPage()`
- `sendAiChatSenderText(sender: CommandManager.AiChatSender, text: String, model: CommandManager.AiChatModel?)`
- `sendAiChatSenderStatus(sender: CommandManager.AiChatSender, status: CommandManager.AiChatStatus, model: CommandManager.AiChatModel?)`
- `sendAiChatStatus(status: CommandManager.AiChatStatus)`
- `sendAiChatText(text: String)`
- `clearAiChat()`
- `clearAiChatLegacy()`
