---
title: sendAiChatLanguage
parent: CommandManager
grandparent: API リファレンス
nav_order: 50
---

# CommandManager.sendAiChatLanguage

```kotlin
fun sendAiChatLanguage(languageCode: String)
```

## 概要

AI チャットの表示言語を通知する。グラス側の本文フォントの選択に使われ、画面遷移は起こさない。フォントを先に確定させるため enterAiChatPage の前に送る。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `languageCode` | `String` | `"JPN"` / `"ENG"` / `"CHS"` / `"CHT"` 等の3文字 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendAiChatLanguage -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [enterAiChatPage](enter-ai-chat-page.html)
