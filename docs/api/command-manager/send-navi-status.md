---
title: sendNaviStatus
parent: CommandManager
grandparent: API リファレンス
nav_order: 45
---

# CommandManager.sendNaviStatus

```kotlin
fun sendNaviStatus(status: CommandManager.NaviStatus)
```

## 概要

ナビの状態を送る。sendNavi で送った案内は `START` のときだけ表示される。`READY` は案内前の待機画面、`ARRIVED` は到着画面になる。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `status` | `CommandManager.NaviStatus` | `READY` / `START` / `ARRIVED` |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendNaviStatus -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [enterNavigationPage](enter-navigation-page.html)
- [sendNavi](send-navi.html)
