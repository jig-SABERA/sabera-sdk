---
title: sendNaviCourse
parent: CommandManager
grandparent: API リファレンス
nav_order: 47
---

# CommandManager.sendNaviCourse

```kotlin
fun sendNaviCourse(courseDegrees: Double)
```

## 概要

端末の GPS 進行方向を送る。グラスは磁力計を積んでいないため、この値でナビ中のジャイロのドリフトを補正する。

{: .note }
> 0.0.10 には含まれない。次のリリースから使える。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `courseDegrees` | `Double` | 進行方向[度]。北を0として時計回り |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendNaviCourse -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendNavi](send-navi.html)
