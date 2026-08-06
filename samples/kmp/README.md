# Glasses SDK KMP Sample

Kotlin + Jetpack Compose から直接 Glasses SDK を利用するサンプルアプリ。
Flutter の MethodChannel ブリッジなしで、SDK API を直接呼び出す。

> 現在 Android のみ対応。iOS は後日追加予定。

## 前提条件

- Android Studio
- Android 実機 (BLE 必須、エミュレータ不可)
- GitHub Packages から SDK を取得するための認証設定（[ルート README](../../README.md) 参照）

## ビルド・実行

```bash
cd samples/kmp
./gradlew :app:installDebug
```
