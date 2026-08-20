---
title: enterTranslatePage
parent: CommandManager
grandparent: API リファレンス
nav_order: 10
---

# CommandManager.enterTranslatePage

```kotlin
fun enterTranslatePage()
```

## 概要

翻訳ページを開く。開いたあとに sendTranslateLanguage で言語ペアを送り、sendTranslateContent で本文を送る、の順で使う。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.enterTranslatePage -->
```kotlin
commandManager.enterTranslatePage()
commandManager.sendTranslateLanguage(source = "ENG", target = "JPN")
commandManager.sendTranslateContent("これは訳文です")
```
<!-- /snippet -->

## 関連

- [sendTranslateContent](send-translate-content.html)
- [sendTranslateLanguage](send-translate-language.html)
