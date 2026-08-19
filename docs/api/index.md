---
title: API リファレンス
nav_order: 4
has_children: true
---

# API リファレンス

Sabera App SDK (Kotlin) の公開 API。バージョン 0.4.0 時点。

メソッドごとに使えるようになったバージョンは[メソッドの追加履歴](../api-history.html)にまとめてある。

| 型 | 説明 |
|---|---|
| [GlassesSDK](glasses-sdk/) | SDK 全体の初期設定を行うシングルトン。`Application.onCreate()` で呼ぶ。 |
| [GlassManager](glass-manager/) | デバイスの探索・接続・切断を担う。`getGlassManager(context)` で取得する。 |
| [GlassClient](glass-client/) | 接続済みの1台を表す。`GlassManager.connectedDevice` から得る。 |
| [CommandManager](command-manager/) | グラス側の画面遷移・コンテンツ送信・イベント購読。`GlassClient.createCommandManager()` で作る。 |
| [SdkActivityHost](sdk-activity-host/) | Activity を必要とする処理を差し込むシングルトン（Android のみ）。 |
| [SdkDevicePersistence](sdk-device-persistence/) | 最後に接続したデバイスの保存先。アプリ側が実装する SPI。 |
| [BLE (Android)](ble/) | Companion Device Manager 周り。Android 固有。 |
