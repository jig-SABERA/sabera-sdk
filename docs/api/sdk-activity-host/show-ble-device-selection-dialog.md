---
title: showBleDeviceSelectionDialog
parent: SdkActivityHost
grandparent: API リファレンス
nav_order: 1
---

# SdkActivityHost.showBleDeviceSelectionDialog

{: .warning }
> このページは執筆中です。

```kotlin
var showBleDeviceSelectionDialog: ((Context, (String?) -> Unit) -> Unit)?
```

## 概要

<!-- WIP -->

## 戻り値

`((Context, (String?) -> Unit) -> Unit)?`

## 使用例

<!-- snippet: SdkActivityHost.showBleDeviceSelectionDialog -->
```kotlin
SdkActivityHost.showBleDeviceSelectionDialog = { _: Context, callback: (String?) -> Unit ->
    selector.showDialog(scope = scope, singleTarget = false, callback = callback)
}
```
<!-- /snippet -->
