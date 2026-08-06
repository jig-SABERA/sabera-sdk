# Sabera App SDK React Native Sample

React Native から NativeModule 経由で Sabera App SDK を利用するサンプルアプリ。

> 現在 Android のみ対応。iOS は後日追加予定。

## 前提条件

- Node.js 18+
- Android 実機 (BLE 必須、エミュレータ不可)
- GitHub Packages から SDK を取得するための認証設定（[ルート README](../../README.md) 参照）

## セットアップ

RN Gradle プラグインが SDK の Kotlin 2.3.10 と互換性がないため、
RN CLI でプロジェクトを初期化してからソースを上書きする。

```bash
# 1. 別ディレクトリで RN プロジェクト初期化
npx @react-native-community/cli init SaberaAppSdkReactNativeSample --version 0.76.6

# 2. ソースファイルをコピー
cp -r samples/react-native/src/ SaberaAppSdkReactNativeSample/src/
cp samples/react-native/App.tsx SaberaAppSdkReactNativeSample/
cp -r samples/react-native/android/app/src/main/kotlin/ \
  SaberaAppSdkReactNativeSample/android/app/src/main/kotlin/

# 3. android/app/build.gradle に SDK 依存を追加
#    implementation("jp.jig.sabera.app.sdk:sabera-app-core:0.0.4")
#    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

# 4. AndroidManifest.xml を samples/react-native/ の内容で上書き

# 5. ビルド・実行
cd SaberaAppSdkReactNativeSample
npm run android
```
