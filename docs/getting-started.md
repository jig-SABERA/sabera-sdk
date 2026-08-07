# Getting Started

Sabera App SDK を使ってスマートグラスと通信するアプリを作る手順。

Flutter でも KMP でも、**ネイティブ層で呼ぶ SDK の API と順序は同一**。
このページはその共通部分を扱う。各サンプルの差は橋渡し層（MethodChannel）の形だけで、
それは[プラットフォーム別の橋渡し](#プラットフォーム別の橋渡し)にまとめた。

## SDK の取得設定

SDK は GitHub Packages (`jig-jp/jig-glass`) で配布している。private パッケージなので、取得には
`read:packages` スコープを持つ Personal Access Token が必要。

`~/.gradle/gradle.properties` に認証情報を書く。

```properties
gpr.user=<GitHubのユーザー名>
gpr.token=<read:packages を持つ PAT>
```

環境変数 `GITHUB_ACTOR` / `GITHUB_TOKEN` でも代用できる。

## 全体の流れ

```
Application.onCreate()  … SPI を差し込む
        ↓
Activity.onCreate()     … デバイス選択ダイアログのフックを差し込む
        ↓
getGlassManager(context)
        ↓
connectedDevice を購読開始   ← 接続状態はここだけを見る
        ↓
showAutomaticSelectionDialog(activity)   … 選択と接続を両方やる
        ↓
client.createCommandManager()
        ↓
コマンド送信 / gestureEvents 購読
        ↓
manager.disconnect(client)
```

## 1. SPI の差し込み

`Application.onCreate()` で行う。SDK はプロセスシングルトンで、**後から差し替えても既に発火した
呼び出しには反映されない**ため、他のどの SDK API より先に実行する必要がある。

```kotlin
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlassesSDK.setLogger { tag, msg -> Log.d(tag, msg) }
        GlassesSDK.setProd(true)
        GlassesSDK.setDevicePersistence(SharedPrefsDevicePersistence(this))
    }
}
```

`setDevicePersistence` を省くとデフォルトのインメモリ実装になり、プロセスをまたぐと接続先を忘れる。
前回接続したデバイスへの自動再接続を使うなら必須。

## 2. Activity 側のフック（Android のみ）

Companion Device Manager のダイアログは Activity を必要とするため、`SdkActivityHost` に
表示処理を差し込む。iOS では不要（CoreBluetooth は Activity を要求しない）。

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    SdkActivityHost.showBleDeviceSelectionDialog = { scope, callback ->
        BleDeviceSelector(this).showDialog(scope, singleTarget = false, callback)
    }
    BleCompanionDeviceService.connectToLastDevice(this)
}

override fun onDestroy() {
    SdkActivityHost.showBleDeviceSelectionDialog = null
    super.onDestroy()
}
```

`connectToLastDevice()` は前回接続したデバイスがあれば自動で接続を試みる。これを呼んでおくと
2回目以降の起動でユーザーにダイアログを見せずに済む。

## 3. 接続状態を購読する

`GlassManager` を取得したら、まず `connectedDevice` の購読を始める。接続の成立も切断も
この Flow ひとつで観測する。

```kotlin
val manager = getGlassManager(context)

scope.launch {
    manager.connectedDevice.collect { client ->
        if (client != null) {
            // 接続済み。ここで CommandManager を作る
        } else {
            // 未接続
        }
    }
}
```

`connectedDevice` は `StateFlow<GlassClient?>`。`GlassClient` にも `connected: StateFlow<Boolean>`
があるが、接続の有無を知るだけなら `connectedDevice` が `null` かどうかで足りる。

## 4. デバイスを選んで接続する

```kotlin
val client: GlassClient? = manager.showAutomaticSelectionDialog(activity)
```

OS のデバイス選択 UI が出て、選ばれたデバイスの `GlassClient` が返る。
**この呼び出しは接続まで済ませる。** 返り値を受け取ったあとに別途 `connect()` を呼ぶ必要はない。

第1引数は Activity。Application Context を渡すとダイアログが出ないので注意。

## 5. コマンドを送る

```kotlin
val commandManager = client.createCommandManager()

commandManager.enterTeleprompterPage()
commandManager.sendTeleprompterContent("Hello")
```

送信系は同期メソッドだが内部でキューイングされるため、呼び出しスレッドはブロックしない。

### ページ遷移

| メソッド | 遷移先 |
|---|---|
| `enterHomePage()` | ホーム |
| `enterTeleprompterPage()` | テレプロンプター |
| `enterAIPage(isAiPower: Boolean = false)` | AI |
| `enterTranslatePage()` | 翻訳 |

### コンテンツ送信

| メソッド | 内容 |
|---|---|
| `sendTeleprompterContent(content: String)` | テレプロンプターに表示する文字列 |
| `sendAIContent(content: String)` | AI ページに表示する文字列 |
| `sendTranslateContent(content: String)` | 翻訳ページに表示する文字列 |
| `sendTranslateLanguage(source: String, target: String)` | 翻訳の言語ペア（例: `"en", "ja"`） |

ページを開いてからコンテンツを送る。送信先のページが開いていないと表示されない。

## 6. ジェスチャーを受け取る

```kotlin
scope.launch {
    commandManager.gestureEvents.collect { gesture ->
        when (gesture) {
            GestureType.SINGLE_TAP -> {}
            GestureType.DOUBLE_TAP -> {}
            GestureType.HOLD -> {}
        }
    }
}
```

`gestureEvents` は `SharedFlow<GestureType>`。購読を始める前に発生したジェスチャーは受け取れない。

## 7. 切断する

```kotlin
manager.disconnect(client)
```

`GlassClient` に `disconnect()` は無い。型の上で `GlassClientInternal` に隔離してあり、
必ず `GlassManager` を経由する。UI 層が接続状態を持たないようにするための制約。

## ハマりどころ

- **SPI を Activity で差し込んでいる** — `Application.onCreate()` で差し込む。Activity 生成前に
  SDK が動くと反映されない
- **`showAutomaticSelectionDialog()` に Application Context を渡している** — Activity が必要
- **`SdkActivityHost.showBleDeviceSelectionDialog` を差し込み忘れている** — Android でダイアログが
  出ない最頻の原因
- **エミュレータ / シミュレータで試している** — BLE の物理層が必要なため動かない。スキャン中のまま
  止まる
- **Android で BLE 権限を実行時要求していない** — API 31 以降は `BLUETOOTH_SCAN` と
  `BLUETOOTH_CONNECT` が必要
- **iOS で `Info.plist` に `NSBluetoothAlwaysUsageDescription` が無い** — CoreBluetooth が
  OS に蹴られ、`GlassClient` が永遠に得られない

## プラットフォーム別の橋渡し

ネイティブ側で呼ぶ SDK API はどちらのサンプルでも同じ。違うのは、それをアプリの言語へどう渡すかだけ。

| | コマンド呼び出し | イベント通知 |
|---|---|---|
| KMP | 橋渡しなし。SDK の型を直接扱う | Flow をそのまま `collect` |
| Flutter | `MethodChannel` | `EventChannel` 2本（`connectionState` / `gestureEvents`） |

Flutter では `GestureType` が文字列（`"SINGLE_TAP"` / `"DOUBLE_TAP"` / `"HOLD"`）
として渡り、Dart 側で独自の型に変換している。接続状態も同様に
`{connected, deviceId, deviceName}` の形に落として渡している。

実装は各サンプルを参照。

- [Flutter](../samples/flutter/)
- [KMP](../samples/kmp/)
