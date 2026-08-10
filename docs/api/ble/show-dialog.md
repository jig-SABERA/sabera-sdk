---
title: BleDeviceSelector.showDialog
parent: BLE (Android)
grandparent: API リファレンス
nav_order: 2
---

# BleDeviceSelector.showDialog

{: .warning }
> このページは執筆中です。

```kotlin
fun showDialog(scope: CoroutineScope, singleTarget: Boolean, callback: (String?) -> Unit)
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `scope` | `CoroutineScope` | <!-- WIP --> |
| `singleTarget` | `Boolean` | <!-- WIP --> |
| `callback` | `(String?) -> Unit` | <!-- WIP --> |

## 戻り値

`Unit`

## 使用例

<!-- snippet: BleDeviceSelector.showDialog -->
```kotlin
val selector = BleDeviceSelector(context)
selector.showDialog(scope = scope, singleTarget = false) { deviceId ->
    // 選ばれたデバイスの ID。キャンセルされた場合は null
    Log.d("sample", "selected: $deviceId")
}
```
<!-- /snippet -->

## 関連

- [BleDeviceSelector.onActivityResult](on-activity-result.html)
