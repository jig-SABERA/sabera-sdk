---
title: Getting Started
nav_order: 2
---

# Getting Started

SABERA App SDK を使ってSABERAグラスと通信するアプリを作る手順。 SABERA
SDKはネイティブAPIとして提供している。

## SDK の取得設定

SDK 1.1.3 以降は [sdk.sabera.glass/maven](https://sdk.sabera.glass/maven) から認証なしで取得できる。
`settings.gradle.kts` の `dependencyResolutionManagement.repositories` に次を追加する。

```kotlin
maven("https://sdk.sabera.glass/maven")
```

アプリの `build.gradle.kts` に依存を追加する。Android は `compileSdk` 37 以上が必要。

```kotlin
implementation("jp.jig.sabera.app.sdk:sabera-app-core:1.1.3")
```

SDK 利用規約の Gradle プラグインを使う場合は、`pluginManagement.repositories` にも同じ Maven リポジトリを追加する。
iOS は Xcode の Add Package Dependencies に `https://github.com/jig-SABERA/sabera-sdk` を入れ、
バージョン `1.1.3` と `SaberaIOS` product を選ぶ。XCFramework の取得にも認証は不要。

## 全体の流れ

このページは Android 向けに書いている。iOS も API の並びは同じだが、
マニフェストの設定と権限の要求は要らない。

```mermaid
flowchart TD
    subgraph init["初期化"]
        B["Activity.onCreate()"] --> C["getGlassManager(context)"]
    end

    subgraph connect["接続"]
        D["connectedDevice を購読"] --> E["showAutomaticSelectionDialog(activity)"]
        E --> F["client.createCommandManager()"]
    end

    subgraph use["利用・終了"]
        G["CommandManager でコマンドの送受信"] --> H["manager.disconnect(client)"]
    end

    C --> D
    F --> G
```

## 1. マニフェストと権限

`AndroidManifest.xml` に権限と、SDK 内蔵のサービスを書く。

```xml
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.REQUEST_OBSERVE_COMPANION_DEVICE_PRESENCE" />

<service
    android:name="app.jigglass.ble.BleCompanionDeviceService"
    android:exported="true"
    android:permission="android.permission.BIND_COMPANION_DEVICE_SERVICE">
    <intent-filter>
        <action android:name="android.companion.CompanionDeviceService" />
    </intent-filter>
</service>
```

## 2. 接続してホーム画面を出す

`MainActivity.kt`に、SABERAに接続してホーム画面を出すまでのコードを書く。

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bluetoothスキャンと接続の許可
        requestPermissions(
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT),
            1001,
        )

        // デバイス選択ダイアログは SDK 側が持っている。表示に Activity が必要。
        SdkActivityHost.showBleDeviceSelectionDialog = { scope, callback ->
            BleDeviceSelector(this).showDialog(scope, singleTarget = false, callback)
        }

        // 前回接続したデバイスに自動で接続する。
        BleCompanionDeviceService.connectToLastDevice(this)

        val manager = getGlassManager(this)

        // 接続・切断の通知を受け取る
        lifecycleScope.launch {
            manager.connectedDevice.collect { client ->
                if (client == null) return@collect

                // 接続したSABERAにBluetoothコマンドを送信する。
                val commandManager = client.createCommandManager()

                // ホーム画面を開く
                commandManager.enterHomePage()
            }
        }

        // 接続するSABERAを選択するダイアログを出す。前回接続したデバイスがあれば自動で接続する。
        lifecycleScope.launch {
            manager.showAutomaticSelectionDialog(this@MainActivity)
        }
    }

    override fun onDestroy() {
        // 破棄した Activity を SDK が掴んだままにしない
        SdkActivityHost.showBleDeviceSelectionDialog = null
        super.onDestroy()
    }
}
```

ノート:

- [`SdkActivityHost.showBleDeviceSelectionDialog`](api/sdk-activity-host/show-ble-device-selection-dialog.md)
  — プロセスに1つとする。`onDestroy()`で破棄すること。
- [`showAutomaticSelectionDialog()`](api/glass-manager/show-automatic-selection-dialog.md)
  — 選択ダイアログ表示したあと、接続まで処理する。別途`connect()`
  を呼ぶ必要はない。
- [`createCommandManager()`](api/glass-client/create-command-manager.md) —
  コマンドは内部キューに積まれて順番に送信される。
- [`enterHomePage()`](api/command-manager/enter-home-page.md) —
  グラス側の画面を切り替える。コンテンツ送信は、対応するページを開いていないと表示されない

## 次のステップ

| やりたいこと           | 参照先                                                            |
| ---------------------- | ----------------------------------------------------------------- |
| 既存のページを開く     | [ページごとの使い方](pages/)                                      |
| 画像を表示する         | [画像表示](pages/image.md)                                      |
| UIを自由に配置する     | [自由配置キャンバス](pages/canvas.md)                           |
| ジェスチャーを受け取る | [gestureEvents](api/command-manager/gesture-events.md)          |
| マイクを使う           | [startMicStreaming](api/command-manager/start-mic-streaming.md) |
| IMUを使う              | [startImuData](api/command-manager/start-imu-data.md)           |
