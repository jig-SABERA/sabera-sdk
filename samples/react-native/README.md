# Glasses SDK React Native Sample

React Native から NativeModule 経由で Glasses SDK を利用するサンプルアプリ。

> 現在 Android のみ対応。iOS は後日追加予定。

## 前提条件

- Node.js 18+
- Android 実機 (BLE 必須、エミュレータ不可)
- jig-glass リポジトリで SDK を mavenLocal に publish 済み（[ルート README](../../README.md) 参照）

## セットアップ

```bash
cd samples/react-native
npm install
```

## ビルド・実行

```bash
npm run android
```
