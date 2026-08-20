---
title: sendTranslateContent
parent: CommandManager
grandparent: API リファレンス
nav_order: 11
---

# CommandManager.sendTranslateContent

```kotlin
fun sendTranslateContent(content: String)
```

## 概要

翻訳ページに訳文を送る。enterTranslatePage で開いてから呼ぶ。送るたび表示は置き換わる。消すときは clearInscriptionText を使う。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `content` | `String` | 表示する訳文。長い分は分割して送られる |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendTranslateContent -->
```kotlin
// 送るたび表示は置き換わる。消すときは clearInscriptionText
commandManager.sendTranslateContent("これは訳文です")
```
<!-- /snippet -->

## 関連

- [enterTranslatePage](enter-translate-page.html)
- [sendTranslateLanguage](send-translate-language.html)
