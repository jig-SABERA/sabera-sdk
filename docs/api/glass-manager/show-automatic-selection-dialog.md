---
title: showAutomaticSelectionDialog
parent: GlassManager
grandparent: API リファレンス
nav_order: 7
---

# GlassManager.showAutomaticSelectionDialog

{: .warning }
> このページは執筆中です。

```kotlin
suspend fun showAutomaticSelectionDialog(context: Context): GlassClient?
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `context` | `Context` | <!-- WIP --> |

## 戻り値

`GlassClient?`

## 使用例

<!-- snippet: GlassManager.showAutomaticSelectionDialog -->
```kotlin
// 第1引数は Activity。Application Context を渡すとダイアログが出ない
val client = manager.showAutomaticSelectionDialog(activity)
```
<!-- /snippet -->

## 関連

- [connect](connect.html)
- [createClientFromDeviceID](create-client-from-device-id.html)
