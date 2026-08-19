---
title: sendTranslateLanguage
parent: CommandManager
grandparent: API リファレンス
nav_order: 11
---

# CommandManager.sendTranslateLanguage

{: .warning }
> このページは執筆中です。

```kotlin
fun sendTranslateLanguage(source: String, target: String)
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `source` | `String` | <!-- WIP --> |
| `target` | `String` | <!-- WIP --> |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendTranslateLanguage -->
```kotlin
commandManager.enterTranslatePage()
commandManager.sendTranslateLanguage(source = "en", target = "ja")
commandManager.sendTranslateContent("Hello")
```
<!-- /snippet -->

## 関連

- [sendTranslateContent](send-translate-content.html)
