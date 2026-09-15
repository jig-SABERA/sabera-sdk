---
title: BLE (Android)
parent: API リファレンス
nav_order: 7
has_children: true
---

# BLE (Android)

Companion Device Manager 周り。Android 固有。

| メソッド | シグネチャ |
|---|---|
| [BleCompanionDeviceService.connectToLastDevice](connect-to-last-device.md) | `fun BleCompanionDeviceService.Companion.connectToLastDevice(context: Context)` |
| [BleDeviceSelector.showDialog](show-dialog.md) | `fun showDialog(scope: CoroutineScope, singleTarget: Boolean, callback: (String?) -> Unit)` |
| [BleDeviceSelector.onActivityResult](on-activity-result.md) | `fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, scope: CoroutineScope): Boolean` |
