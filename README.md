# Sabera App SDK Samples

Sabera App SDK の使い方を示すサンプルアプリ集。

## サンプル一覧

| サンプル | プラットフォーム | 説明 |
|---------|--------------|------|
| [Flutter](samples/flutter/) | Android / iOS | MethodChannel / EventChannel 経由で KMP SDK を Dart から操作 |
| [KMP](samples/kmp/) | Android / iOS | Kotlin Multiplatform から直接 SDK を利用 |

## ドキュメント

- [Getting Started](docs/getting-started.md) — SDK のセットアップと基本的な使い方
- [ページごとの使い方](docs/pages/) — グラスの画面ごとに、開き方と送るもの
- [更新履歴](docs/api-history.md) — SDK の変更点
- [API リファレンス](docs/api/) — 公開 API の一覧（内容は執筆中）
- [Bluetooth コマンドリスト](docs/bluetooth-commands.md) — コマンドID・パケット形式・対応API
- [サードパーティ表記](docs/third-party-notices.md) — SDK に含まれる OSS と、アプリ側に必要な表示
- [ドキュメントの作り方](docs/authoring.md) — サイトの構成、コード例の検証、デプロイ

## 前提条件

SDK 1.1.2 以降は [sdk.sabera.glass/maven](https://sdk.sabera.glass/maven) から認証なしで取得できる。
サンプルにはリポジトリ設定済み。自分のアプリへの設定は [Getting Started](docs/getting-started.md) を参照。

SDK 1.1.2を使うAndroidアプリは `compileSdk` 37以上が必要。
詳細は [Getting Started](docs/getting-started.md) を参照。

iOS は Swift Package Manager で取得する。Xcode の Add Package Dependencies に
このリポジトリの URL を入れ、バージョン `1.1.2` を指定する。
通常の KMP SDK を使う場合は `SaberaAppSDK` product を、BLE と Opus を含む完全版を使う場合は
`SaberaIOS` product を選択する。完全版の最低対応 iOS バージョンは 18.2。

完全版を利用するアプリでは、Swift Package の依存関係に `SaberaIOS` を追加し、以下のように import する。

```swift
import SaberaAppSDK
import SaberaIOSBridge
```

XCFramework も `https://sdk.sabera.glass/maven` から認証なしで取得できる。

## ライセンス

このリポジトリのサンプルコードは [Apache License 2.0](LICENSE) で提供する。自由に改変して
自分のアプリに取り込んでよい。

ただし **SDK 本体（`jp.jig.sabera.app.sdk:*`）はこのライセンスの対象外**。SDK は `https://sdk.sabera.glass/maven`
から配布するバイナリで、利用には別途 SDK 利用規約が適用される。

| 対象 | ライセンス |
|---|---|
| このリポジトリのサンプル・ラッパーコード | Apache License 2.0 |
| Sabera App SDK 本体（AAR / XCFramework） | [SDK 利用規約](https://github.com/jig-SABERA/sabera-sdk-packages/blob/main/TERMS.md) |
