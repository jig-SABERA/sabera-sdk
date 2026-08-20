---
title: enterTeleprompterPage
parent: CommandManager
grandparent: API リファレンス
nav_order: 8
---

# CommandManager.enterTeleprompterPage

```kotlin
fun enterTeleprompterPage()
```

## 概要

テレプロンプトページを開く。原稿は sendTeleprompterContent で送る。開く前に送った原稿は表示されない。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.enterTeleprompterPage -->
```kotlin
// 開いてからでないと原稿は表示されない
commandManager.enterTeleprompterPage()
commandManager.sendTeleprompterContent("読み上げる原稿")
```
<!-- /snippet -->

## 関連

- [sendTeleprompterContent](send-teleprompter-content.html)
