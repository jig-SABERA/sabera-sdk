---
title: sendTranslateLanguage
parent: CommandManager
grandparent: API リファレンス
nav_order: 12
---

# CommandManager.sendTranslateLanguage

```kotlin
fun sendTranslateLanguage(source: String, target: String)
```

## 概要

翻訳ページに出す言語ラベルを切り替える。本文を送る前に呼ぶ。画面遷移は起こらない。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `source` | `String` | 翻訳元の言語コード。`"ENG"` などの3文字 |
| `target` | `String` | 翻訳先の言語コード。`"JPN"` などの3文字 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendTranslateLanguage -->
```kotlin
commandManager.enterTranslatePage()
commandManager.sendTranslateLanguage(source = "ENG", target = "JPN")
commandManager.sendTranslateContent("Hello")
```
<!-- /snippet -->

## 関連

- [sendTranslateContent](send-translate-content.html)
