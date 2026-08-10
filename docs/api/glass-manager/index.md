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
| [getGlassManager](get-glass-manager.html) | `fun getGlassManager(context: Context): GlassManager` |
| [connectedDevice](connected-device.html) | `val connectedDevice: StateFlow<GlassClient?>` |
| [lastConnectedDevice](last-connected-device.html) | `val lastConnectedDevice: GlassClient?` |
| [hasLastConnectedDevice](has-last-connected-device.html) | `val hasLastConnectedDevice: Boolean` |
| [selectionDialogPresented](selection-dialog-presented.html) | `val selectionDialogPresented: SharedFlow<Unit>` |
| [externalDisplayNameChanged](external-display-name-changed.html) | `val externalDisplayNameChanged: SharedFlow<String?>` |
| [showAutomaticSelectionDialog](show-automatic-selection-dialog.html) | `suspend fun showAutomaticSelectionDialog(context: Context): GlassClient?` |
| [connect](connect.html) | `suspend fun connect(glassClient: GlassClient)` |
| [disconnect](disconnect.html) | `suspend fun disconnect(glassClient: GlassClient)` |
| [disconnectAndClearBond](disconnect-and-clear-bond.html) | `suspend fun disconnectAndClearBond(glassClient: GlassClient)` |
| [createClientFromDeviceID](create-client-from-device-id.html) | `fun createClientFromDeviceID(deviceId: String): GlassClient?` |
| [onDeviceDisappear](on-device-disappear.html) | `fun onDeviceDisappear(address: String)` |
| [registerDeviceFromIntent](register-device-from-intent.html) | `fun registerDeviceFromIntent(data: Intent)` |
| [showDisconnectSheetIOS](show-disconnect-sheet-ios.html) | `suspend fun showDisconnectSheetIOS(): Boolean` |
| [showRenameAccessorySheetIOS](show-rename-accessory-sheet-ios.html) | `suspend fun showRenameAccessorySheetIOS(): Boolean` |
