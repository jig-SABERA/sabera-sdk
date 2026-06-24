# Glasses SDK Flutter Sample

Flutter から Glasses SDK を呼び出すサンプルアプリ。
MethodChannel / EventChannel で KMP SDK の主要機能を Dart から操作する。

> 現在 Android のみ対応。iOS は後日追加予定。

## 前提条件

- Flutter SDK 3.29+
- Android 実機 (BLE 必須、エミュレータ不可)
- jig-glass リポジトリで SDK を mavenLocal に publish 済み

## セットアップ

### 1. SDK を mavenLocal に publish

```bash
cd app
./gradlew :glasses-sdk:glasses-core:publishToMavenLocal \
          :glasses-sdk:ble-core:publishToMavenLocal \
          :glasses-sdk:glasses-protocol:publishToMavenLocal
```

### 2. ビルド・実行

```bash
flutter run
```
