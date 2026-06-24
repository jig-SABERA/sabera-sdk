# Glasses SDK Samples

Glasses SDK の使い方を示すサンプルアプリ集。

## サンプル一覧

| サンプル | プラットフォーム | 説明 |
|---------|--------------|------|
| [Flutter](samples/flutter/) | Android | MethodChannel / EventChannel 経由で KMP SDK を Dart から操作 |
| [KMP](samples/kmp/) | Android / iOS | Kotlin Multiplatform から直接 SDK を利用 |

## ドキュメント

- [Getting Started](docs/getting-started.md) — SDK のセットアップと基本的な使い方

## 前提条件

- jig-glass リポジトリで SDK を mavenLocal に publish 済み

```bash
cd app
./gradlew :glasses-sdk:glasses-core:publishToMavenLocal \
          :glasses-sdk:ble-core:publishToMavenLocal \
          :glasses-sdk:glasses-protocol:publishToMavenLocal
```
