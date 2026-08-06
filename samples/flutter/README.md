# Glasses SDK Flutter Sample

Flutter から Glasses SDK を呼び出すサンプルアプリ。
MethodChannel / EventChannel で KMP SDK の主要機能を Dart から操作する。

> 現在 Android のみ対応。iOS は後日追加予定。

## 前提条件

- Flutter SDK 3.29+
- Android 実機 (BLE 必須、エミュレータ不可)
- GitHub Packages から SDK を取得するための認証設定（[ルート README](../../README.md) 参照）

## ビルド・実行

```bash
cd samples/flutter
flutter run
```
