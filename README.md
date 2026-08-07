# Sabera App SDK Samples

Sabera App SDK の使い方を示すサンプルアプリ集。

## サンプル一覧

| サンプル | プラットフォーム | 説明 |
|---------|--------------|------|
| [Flutter](samples/flutter/) | Android | MethodChannel / EventChannel 経由で KMP SDK を Dart から操作 |
| [KMP](samples/kmp/) | Android / iOS | Kotlin Multiplatform から直接 SDK を利用 |

## ドキュメント

- [Getting Started](docs/getting-started.md) — SDK のセットアップと基本的な使い方

## 前提条件

SDK は GitHub Packages (`jig-jp/jig-glass`) から取得する。private パッケージのため、
`read:packages` スコープを持つ PAT を `~/.gradle/gradle.properties` に設定しておく。

```properties
gpr.user=<GitHubのユーザー名>
gpr.token=<read:packages を持つ PAT>
```

詳細は [Getting Started](docs/getting-started.md) を参照。

## ライセンス

このリポジトリのサンプルコードは [Apache License 2.0](LICENSE) で提供する。自由に改変して
自分のアプリに取り込んでよい。

ただし **SDK 本体（`jp.jig.sabera.app.sdk:*`）はこのライセンスの対象外**。SDK は GitHub Packages
から配布するバイナリで、利用には別途 SDK 利用規約が適用される。

| 対象 | ライセンス |
|---|---|
| このリポジトリのサンプル・ラッパーコード | Apache License 2.0 |
| Sabera App SDK 本体（AAR / XCFramework） | SDK 利用規約 |
