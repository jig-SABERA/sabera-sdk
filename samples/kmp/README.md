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
指定しなければAndroidは従来どおりGitHub PackagesのSDK 1.0.1を使う。
KotlinとComposeのバージョンはローカルSDKのビルド環境に揃えている。

## iOS

Xcode 26系、JDK 17以上、iOS 18.2以上が必要です。Android／common の KMP は公開 SDK 1.0.1、iOS の `SaberaAppSDK` は SDK 1.1.0 から生成します。Swift ブリッジと OggOpus も `SwiftPM/Package.swift` の公開 SDK 1.1.0 バイナリを使います。ローカル SDK ソースや `scripts/build-ios.sh` は必要ありません。

公開 SDK でビルドするときは `SABERA_SDK_PATH` を設定せず、`local.properties` に `sabera.sdk.path` を記載しないでください。設定されていると Gradle がローカル SDK に差し替えます。

GitHub Packages の Gradle 認証に加えて、SwiftPM の XCFramework 取得用に `~/.netrc` を設定します。

```text
machine maven.pkg.github.com
  login <GitHubのユーザー名>
  password <read:packages を持つ PAT>
```

シミュレータ向けの署名なしビルド:

```bash
cd samples/kmp
xcodebuild -project iosApp/GlassesSample.xcodeproj \
  -scheme GlassesSample \
  -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath build/ios \
  -packageAuthorizationProvider netrc \
  CODE_SIGNING_ALLOWED=NO build
```

実機で署名する場合は、Xcode で Team を設定してから次のように実行します。

```bash
xcodebuild -project iosApp/GlassesSample.xcodeproj \
  -scheme GlassesSample \
  -sdk iphoneos \
  -destination 'generic/platform=iOS' \
  -derivedDataPath build/ios \
  -packageAuthorizationProvider netrc \
  DEVELOPMENT_TEAM=<TEAM_ID> build
```

Xcodeから開く場合は `iosApp/GlassesSample.xcodeproj` を開き、Teamと実機またはシミュレータを指定してRunします。

iOSの初期化は `SampleApp.swift`、画面の入口は `SampleAppViewController.kt`。
フォトピッカーはiOSのPHPicker、動画のASCII変換はAVFoundationを使う。
SwiftPM の OggOpus バイナリは iOS アプリの実機・シミュレータビルドに含まれる。

## 動画デモデータ

元のリポジトリにはBad Appleのデータファイルは含まれない。
利用可能なデータを `assets/` に置くとAndroid / iOSの両方に同梱される。
`badapple.txt`（20列8行単位）、`badapple.bin`、`badapple320.bin` を各画面が読み込む。
データがない場合は画面に読み込みエラーを表示する。写真・動画ピッカーやテストパターンは別途利用できる。

## テスト

```bash
./gradlew :shared:iosSimulatorArm64Test :shared:testDebugUnitTest
```

## 公開 SDK の CI 検証

CI は Android／common の KMP では GitHub Packages の SDK 1.0.1、iOS の KMP `SaberaAppSDK` と Swift ブリッジ／OggOpus では 1.1.0 を使って
`:shared:compileDebugKotlinAndroid` と、iOS 実機・シミュレータ向けの
`:shared:linkDebugFrameworkIosArm64` / `:shared:linkDebugFrameworkIosSimulatorArm64` を実行する。
このバージョン構成は、上記の `xcodebuild` によるアプリ全体のビルドが成功した場合に検証できる。

## ライセンス

このサンプルコードは [Apache License 2.0](../../LICENSE)。
SDK 本体は対象外で、別途 SDK 利用規約が適用される（[ルート README](../../README.md) 参照）。
