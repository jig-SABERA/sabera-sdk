---
title: sendTeleprompterContent
parent: CommandManager
grandparent: API リファレンス
nav_order: 5
---

# CommandManager.sendTeleprompterContent

```kotlin
fun sendTeleprompterContent(content: String)
fun sendTeleprompterContent(content: String, percent: Int)
```

## 概要

テレプロンプトに原稿を送る。200バイトを超える分は分割して送られる。`percent` つきの overload はスクロールバーの位置も一緒に送る。

{: .note }
> `percent` つきの overload は 0.0.10 には含まれない。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `content` | `String` | 表示する原稿 |
| `percent` | `Int` | スクロールバーの位置（0..100） |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendTeleprompterContent -->
```kotlin
commandManager.enterTeleprompterPage()
commandManager.sendTeleprompterContent("読み上げる原稿")
```
<!-- /snippet -->

## 関連

- [enterTeleprompterPage](enter-teleprompter-page.html)
- [sendTeleprompterLine](send-teleprompter-line.html)
