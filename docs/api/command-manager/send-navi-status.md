---
title: sendNaviStatus
parent: CommandManager
grandparent: API リファレンス
nav_order: 46
---

# CommandManager.sendNaviStatus

```kotlin
fun sendNaviStatus(status: CommandManager.NaviStatus)
```

## 概要

ナビの状態を送る。

{: .note }
> 0.0.10 には含まれない。次のリリースから使える。

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

- [sendNavi](send-navi.html)
