# Sabera App SDK KMP Sample

Kotlin Multiplatform + Compose Multiplatform から直接 Sabera App SDK を利用するサンプルアプリ。
Flutter の MethodChannel ブリッジなしで、SDK API を直接呼び出す。

Android / iOSで同じ `shared/src/commonMain` の画面を使う。
画像・動画選択と画像描画だけをプラットフォーム別に実装している。

## 前提条件

- Android Studio
- Android 実機 (BLE 必須、エミュレータ不可)
- GitHub Packages から SDK を取得するための認証設定（[ルート README](../../README.md) 参照）

## ビルド・実行

```bash
cd samples/kmp
./gradlew :app:installDebug
```

## ローカルSDK

`SABERA_SDK_PATH` にSDK本体のソースがあるローカルリポジトリのルートを指定する。
Gradleのcomposite buildで配布座標を `app/glasses-sdk/sabera-app-core` に差し替えるため、
ローカルSDKのpublishやGitHub Packagesの認証は不要。

```bash
export SABERA_SDK_PATH=/absolute/path/to/jig-glass
./gradlew :app:assembleDebug
```

Gradleには `local.properties` の `sabera.sdk.path` でも指定できる。
指定しなければAndroidは従来どおりGitHub PackagesのSDK 1.0.0を使う。
KotlinとComposeのバージョンはローカルSDKのビルド環境に揃えている。

## iOS

Xcode 26系、JDK 17以上、iOS 18.2以上の実機が必要。
現在のiOSサンプルはローカルSDK指定が必須。

```bash
export SABERA_SDK_PATH=/absolute/path/to/jig-glass
bash scripts/build-ios.sh -quiet
```

スクリプトはSDKのSwiftブリッジとOggOpusを `build/ios-sdk` に準備してから、
XcodeのビルドフェーズでCompose画面とSDKをまとめた `SampleShared` をビルドする。
別の `SaberaAppSDK.framework` を同時にリンクしない。

署名付きビルドでは自分のTeamを指定する。

```bash
bash scripts/build-ios.sh CODE_SIGNING_ALLOWED=YES DEVELOPMENT_TEAM=<TEAM_ID> -allowProvisioningUpdates
```

Xcodeから開く場合も最初に上記スクリプトを一度実行し、
`local.properties` に `sabera.sdk.path=/absolute/path/to/jig-glass` を設定する。
`iosApp/GlassesSample.xcodeproj` を開き、Teamと実機を指定してRunする。
SDKのSwift実装を変更した場合はスクリプトで取り込み直す。

iOSの初期化は `SampleApp.swift`、画面の入口は `SampleAppViewController.kt`。
フォトピッカーはiOSのPHPicker、動画のASCII変換はAVFoundationを使う。
付属のOggOpusバイナリが実機専用なので、アプリ全体のシミュレータ実行は未対応。

## 動画デモデータ

元のリポジトリにはBad Appleのデータファイルは含まれない。
利用可能なデータを `assets/` に置くとAndroid / iOSの両方に同梱される。
`badapple.txt`（20列8行単位）、`badapple.bin`、`badapple320.bin` を各画面が読み込む。
データがない場合は画面に読み込みエラーを表示する。写真・動画ピッカーやテストパターンは別途利用できる。

## テスト

```bash
./gradlew :shared:iosSimulatorArm64Test :shared:testDebugUnitTest
```

## ライセンス

このサンプルコードは [Apache License 2.0](../../LICENSE)。
SDK 本体は対象外で、別途 SDK 利用規約が適用される（[ルート README](../../README.md) 参照）。
