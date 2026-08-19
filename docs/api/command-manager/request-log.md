---
title: requestLog
parent: CommandManager
grandparent: API リファレンス
nav_order: 58
---

# CommandManager.requestLog

```kotlin
fun requestLog(type: CommandManager.GlassLogType)
```

## 概要

グラスにログを要求する。クラッシュ前のログや再起動理由の調査に使う。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `type` | `CommandManager.GlassLogType` | `REALTIME` / `SYSLOG` / `RUNTIME` / `RESET_REASON` / `STOP` |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.requestLog -->
<!-- WIP -->
<!-- /snippet -->
