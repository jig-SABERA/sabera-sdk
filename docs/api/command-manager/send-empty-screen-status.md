---
title: sendEmptyScreenStatus
parent: CommandManager
grandparent: API リファレンス
nav_order: 40
---

# CommandManager.sendEmptyScreenStatus

```kotlin
fun sendEmptyScreenStatus(status: CommandManager.TeleprompterStatus)
```

## 概要

汎用テキスト表示ページの状態を送る。`READY` で空画面に戻る。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `status` | `CommandManager.TeleprompterStatus` | `READY` / `STARTED` / `PAUSED` |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendEmptyScreenStatus -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [enterEmptyScreenPage](enter-empty-screen-page.html)
