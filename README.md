# Sabera App SDK Samples

Sabera App SDK の使い方を示すサンプルアプリ集。

## サンプル一覧

| サンプル | プラットフォーム | 説明 |
|---------|--------------|------|
| [Flutter](samples/flutter/) | Android | MethodChannel / EventChannel 経由で KMP SDK を Dart から操作 |
| [KMP](samples/kmp/) | Android / iOS | Kotlin Multiplatform から直接 SDK を利用 |

## ドキュメント

- [Getting Started](docs/getting-started.md) — SDK のセットアップと基本的な使い方
- [ページごとの使い方](docs/pages/) — グラスの画面ごとに、開き方と送るもの
- [API リファレンス](docs/api/) — 公開 API の一覧（内容は執筆中）
- [サードパーティ表記](docs/third-party-notices.md) — SDK に含まれる OSS と、アプリ側に必要な表示
- [ドキュメントの作り方](docs/authoring.md) — サイトの構成、コード例の検証、デプロイ

## 前提条件

SDK は GitHub Packages (`jig-SABERA/sabera-sdk-packages`) から取得する。private パッケージのため、
`read:packages` スコープを持つ PAT を `~/.gradle/gradle.properties` に設定しておく。

```properties
GitHubPackagesUsername=<GitHubのユーザー名>
GitHubPackagesPassword=<read:packages を持つ PAT>
```

詳細は [Getting Started](docs/getting-started.md) を参照。

iOS は Swift Package Manager で取得する。Xcode の Add Package Dependencies に
このリポジトリの URL を入れる。XCFramework の実体は GitHub Packages にあり、
**SPM は Authorization ヘッダを付けられない**ため `~/.netrc` に認証情報が要る。

```
machine maven.pkg.github.com
  login <GitHubのユーザー名>
  password <read:packages を持つ PAT>
```

## ライセンス

このリポジトリのサンプルコードは [Apache License 2.0](LICENSE) で提供する。自由に改変して
自分のアプリに取り込んでよい。

ただし **SDK 本体（`jp.jig.sabera.app.sdk:*`）はこのライセンスの対象外**。SDK は GitHub Packages
から配布するバイナリで、利用には別途 SDK 利用規約が適用される。

| 対象 | ライセンス |
|---|---|
| このリポジトリのサンプル・ラッパーコード | Apache License 2.0 |
| Sabera App SDK 本体（AAR / XCFramework） | SDK 利用規約 |
