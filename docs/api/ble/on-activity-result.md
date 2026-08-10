---
title: BleDeviceSelector.onActivityResult
parent: BLE (Android)
grandparent: API リファレンス
nav_order: 3
---

# BleDeviceSelector.onActivityResult

{: .warning }
> このページは執筆中です。

```kotlin
fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, scope: CoroutineScope): Boolean
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `requestCode` | `Int` | <!-- WIP --> |
| `resultCode` | `Int` | <!-- WIP --> |
| `data` | `Intent?` | <!-- WIP --> |
| `scope` | `CoroutineScope` | <!-- WIP --> |

## 戻り値

`Boolean`

## 使用例

<!-- snippet: BleDeviceSelector.onActivityResult -->
```kotlin
if (selector.onActivityResult(requestCode, resultCode, data, scope)) {
    // SDK が処理した。Activity 側の処理は不要
    return true
}
```
<!-- /snippet -->

## 関連

- [BleDeviceSelector.showDialog](show-dialog.html)
