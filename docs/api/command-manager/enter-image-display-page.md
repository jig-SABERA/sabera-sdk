---
title: enterImageDisplayPage
parent: CommandManager
grandparent: API リファレンス
nav_order: 28
---

# CommandManager.enterImageDisplayPage

```kotlin
fun enterImageDisplayPage()
```

## 概要

画像表示ページを開く。技適マークの表示に使っている画面で、画像は sendImage で送る。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.enterImageDisplayPage -->
```kotlin
// 技適マークの表示にも使っている画像表示ページ
commandManager.enterImageDisplayPage()
```
<!-- /snippet -->

## 関連

- [sendImage](send-image.html)
