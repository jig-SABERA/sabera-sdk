---
title: sendTeleprompterLine
parent: CommandManager
grandparent: API リファレンス
nav_order: 46
---

# CommandManager.sendTeleprompterLine

```kotlin
fun sendTeleprompterLine(text: String, percent: Int, scrollUp: Boolean = false)
```

## 概要

テレプロンプトに1行だけ追記する。全文を送り直す sendTeleprompterContent と違い、読み上げに合わせて差分だけを流すのに使う。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `text` | `String` | 追記する1行 |
| `percent` | `Int` | スクロールバーの位置（0..100） |
| `scrollUp` | `Boolean` | `true` で1行上へ、`false` で1行下へスクロールさせる |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendTeleprompterLine -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendTeleprompterContent](send-teleprompter-content.html)
