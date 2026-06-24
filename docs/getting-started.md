# Getting Started

Glasses SDK を使ってスマートグラスと通信するアプリを作る手順。

## SDK の publish

サンプルアプリをビルドする前に、jig-glass リポジトリで SDK を mavenLocal に publish する。

```bash
cd app
./gradlew :glasses-sdk:glasses-core:publishToMavenLocal \
          :glasses-sdk:ble-core:publishToMavenLocal \
          :glasses-sdk:glasses-protocol:publishToMavenLocal
```

## 基本フロー

1. **初期化** — `GlassesSDK` のロガーや永続化を設定
2. **スキャン & 接続** — `GlassManager.showAutomaticSelectionDialog()` でデバイス選択
3. **コマンド送信** — `CommandManager` 経由でページ遷移やコンテンツ送信
4. **イベント受信** — `CommandManager.gestureEvents` でジェスチャーを受け取る
5. **切断** — `GlassManager.disconnect()` で切断

## 初期化コード (Kotlin)

```kotlin
// Application.onCreate()
GlassesSDK.setLogger { tag, msg -> Log.d(tag, msg) }
GlassesSDK.setProd(true)
GlassesSDK.setDevicePersistence(yourPersistence)
```

## 接続

```kotlin
val manager = getGlassManager(context)
val client = manager.showAutomaticSelectionDialog(activity)
```

## コマンド送信

```kotlin
val commandManager = client.createCommandManager()
commandManager.enterTeleprompterPage()
commandManager.sendTeleprompterContent("Hello")
```

## ジェスチャー受信

```kotlin
commandManager.gestureEvents.collect { gesture ->
    // gesture.name: "SINGLE_TAP", "DOUBLE_TAP", "HOLD"
}
```
