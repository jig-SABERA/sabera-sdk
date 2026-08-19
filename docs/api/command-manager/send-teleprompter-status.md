---
title: sendTeleprompterStatus
parent: CommandManager
grandparent: API リファレンス
nav_order: 36
---

# CommandManager.sendTeleprompterStatus

```kotlin
fun sendTeleprompterStatus(
    status: CommandManager.TeleprompterStatus,
    mode: CommandManager.TeleprompterMode,
)
```

## 概要

テレプロンプトの再生状態と表示モードを送る。別パケットに分けると動作が安定しないため、ファーム側の都合で必ず両方まとめて送る。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `status` | `CommandManager.TeleprompterStatus` | `READY` / `STARTED` / `PAUSED` |
| `mode` | `CommandManager.TeleprompterMode` | `TELEPROMPT` / `TRANSCRIPT` / `TRANSLATION` |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendTeleprompterStatus -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendTeleprompterTime](send-teleprompter-time.html)
