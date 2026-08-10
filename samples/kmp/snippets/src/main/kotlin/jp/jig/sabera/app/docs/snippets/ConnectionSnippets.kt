package jp.jig.sabera.app.docs.snippets

import android.content.Context
import android.content.Intent
import android.util.Log
import app.jigglass.glass.GlassClient
import app.jigglass.glass.GlassManager
import app.jigglass.glass.getGlassManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** 接続と切断に関するコード例。 */
internal object ConnectionSnippets {

    fun obtainManager(context: Context): GlassManager {
        // #snippet getGlassManager
        val manager = getGlassManager(context)
        // #endsnippet
        return manager
    }

    fun observeConnection(manager: GlassManager, scope: CoroutineScope) {
        // #snippet GlassManager.connectedDevice
        scope.launch {
            manager.connectedDevice.collect { client ->
                if (client != null) {
                    // 接続済み。ここで CommandManager を作る
                } else {
                    // 未接続
                }
            }
        }
        // #endsnippet
    }

    fun reconnectToLast(manager: GlassManager, scope: CoroutineScope) {
        // #snippet GlassManager.lastConnectedDevice
        val last = manager.lastConnectedDevice
        if (last != null) {
            scope.launch { manager.connect(last) }
        }
        // #endsnippet
    }

    fun canReconnect(manager: GlassManager): Boolean {
        // #snippet GlassManager.hasLastConnectedDevice
        val canReconnect = manager.hasLastConnectedDevice
        // #endsnippet
        return canReconnect
    }

    fun observeDialogPresented(manager: GlassManager, scope: CoroutineScope) {
        // #snippet GlassManager.selectionDialogPresented
        scope.launch {
            manager.selectionDialogPresented.collect {
                // OS のデバイス選択ダイアログが表示された
            }
        }
        // #endsnippet
    }

    fun observeDisplayName(manager: GlassManager, scope: CoroutineScope) {
        // #snippet GlassManager.externalDisplayNameChanged
        scope.launch {
            manager.externalDisplayNameChanged.collect { name ->
                Log.d("sample", "display name: $name")
            }
        }
        // #endsnippet
    }

    suspend fun selectAndConnect(manager: GlassManager, activity: Context): GlassClient? {
        // #snippet GlassManager.showAutomaticSelectionDialog
        // 第1引数は Activity。Application Context を渡すとダイアログが出ない
        val client = manager.showAutomaticSelectionDialog(activity)
        // #endsnippet
        return client
    }

    suspend fun connect(manager: GlassManager, client: GlassClient) {
        // #snippet GlassManager.connect
        manager.connect(client)
        // #endsnippet
    }

    suspend fun disconnect(manager: GlassManager, client: GlassClient) {
        // #snippet GlassManager.disconnect
        manager.disconnect(client)
        // #endsnippet
    }

    suspend fun forget(manager: GlassManager, client: GlassClient) {
        // #snippet GlassManager.disconnectAndClearBond
        // ペアリング情報も消すため、次回はデバイス選択からやり直しになる
        manager.disconnectAndClearBond(client)
        // #endsnippet
    }

    suspend fun connectById(manager: GlassManager, deviceId: String) {
        // #snippet GlassManager.createClientFromDeviceID
        val client = manager.createClientFromDeviceID(deviceId)
        if (client != null) {
            manager.connect(client)
        }
        // #endsnippet
    }

    fun onDeviceGone(manager: GlassManager, deviceId: String) {
        // #snippet GlassManager.onDeviceDisappear
        manager.onDeviceDisappear(deviceId)
        // #endsnippet
    }

    fun onAssociationResult(manager: GlassManager, intent: Intent) {
        // #snippet GlassManager.registerDeviceFromIntent
        manager.registerDeviceFromIntent(intent)
        // #endsnippet
    }
}
