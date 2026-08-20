---
title: sendNaviCourse
parent: CommandManager
grandparent: API リファレンス
nav_order: 50
---

# CommandManager.sendNaviCourse

```kotlin
fun sendNaviCourse(courseDegrees: Double)
```

## 概要

端末の GPS 進行方向を送る。グラスは磁力計を持たず方位を単体で保てないため、この値をジャイロのドリフト補正に使う。案内中に数秒おきに送る。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `courseDegrees` | `Double` | 進行方向[度]。0以上360未満。北を0として時計回り |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendNaviCourse -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendNaviStatus](send-navi-status.html)
