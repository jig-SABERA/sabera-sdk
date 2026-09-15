---
title: GlassManager
parent: API リファレンス
nav_order: 2
has_children: true
---

# GlassManager

デバイスの探索・接続・切断を担う。`getGlassManager(context)` で取得する。

| メソッド | シグネチャ |
|---|---|
| [getGlassManager](get-glass-manager.md) | `fun getGlassManager(context: Context): GlassManager` |
| [connectedDevice](connected-device.md) | `val connectedDevice: StateFlow<GlassClient?>` |
| [lastConnectedDevice](last-connected-device.md) | `val lastConnectedDevice: GlassClient?` |
| [hasLastConnectedDevice](has-last-connected-device.md) | `val hasLastConnectedDevice: Boolean` |
| [selectionDialogPresented](selection-dialog-presented.md) | `val selectionDialogPresented: SharedFlow<Unit>` |
| [externalDisplayNameChanged](external-display-name-changed.md) | `val externalDisplayNameChanged: SharedFlow<String?>` |
| [showAutomaticSelectionDialog](show-automatic-selection-dialog.md) | `suspend fun showAutomaticSelectionDialog(context: Context): GlassClient?` |
| [connect](connect.md) | `suspend fun connect(glassClient: GlassClient)` |
| [disconnect](disconnect.md) | `suspend fun disconnect(glassClient: GlassClient)` |
| [disconnectAndClearBond](disconnect-and-clear-bond.md) | `suspend fun disconnectAndClearBond(glassClient: GlassClient)` |
| [createClientFromDeviceID](create-client-from-device-id.md) | `fun createClientFromDeviceID(deviceId: String): GlassClient?` |
| [onDeviceDisappear](on-device-disappear.md) | `fun onDeviceDisappear(address: String)` |
| [registerDeviceFromIntent](register-device-from-intent.md) | `fun registerDeviceFromIntent(data: Intent)` |
| [showDisconnectSheetIOS](show-disconnect-sheet-ios.md) | `suspend fun showDisconnectSheetIOS(): Boolean` |
| [showRenameAccessorySheetIOS](show-rename-accessory-sheet-ios.md) | `suspend fun showRenameAccessorySheetIOS(): Boolean` |
