# Sabera App SDK Flutter Sample

Flutter から Sabera App SDK を呼び出す Android / iOS サンプルアプリ。
MethodChannel / EventChannel を使い、同じ Dart の画面から接続・切断、ページ切り替え、文字送信、ジェスチャー受信を行う。

## 前提条件

- Flutter SDK 3.29.2 以上
- Bluetooth 通信には Android / iPhone 実機とグラスが必要
- GitHub Packages の認証設定（[ルート README](../../README.md) 参照）

## Android

Gradle の GitHub Packages 認証を設定してから実行する。

```bash
cd samples/flutter
flutter pub get
flutter run -d <AndroidのデバイスID>
```

## iOS

Xcode 26 系、iOS 18.2 以上が必要。シミュレータは Apple Silicon Mac のみ対応する。
ルートの `Package.swift` を相対参照し、公開 SDK 1.1.0 の `SaberaIOS` product
（SaberaAppSDK・SaberaIOSBridge・OggOpus）を取得する。KMP サンプルのビルドやローカル SDK ソースは不要。

SwiftPM が XCFramework を取得できるよう、`~/.netrc` に認証情報を設定する。

```text
machine maven.pkg.github.com
  login <GitHubのユーザー名>
  password <read:packages を持つ PAT>
```

初回は Flutter の iOS ツールとビルド設定を準備する。

```bash
cd samples/flutter
flutter precache --ios
flutter pub get
open ios/Runner.xcworkspace
```

Xcode の Runner ターゲットで Signing & Capabilities の Team と、自分の署名で使える
Bundle Identifier を設定し、iPhone を選択して Run する。設定後は `flutter run -d <iPhoneのデバイスID>` でも起動できる。

署名なしのシミュレータ向けビルド:

```bash
xcodebuild -workspace ios/Runner.xcworkspace \
  -scheme Runner -configuration Debug \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath build/ios-simulator \
  -clonedSourcePackagesDirPath build/SourcePackages \
  -packageAuthorizationProvider netrc \
  CODE_SIGNING_ALLOWED=NO build
```

実機向けの署名なしビルドは `-configuration Release -sdk iphoneos -destination 'generic/platform=iOS'`、
出力先は `-derivedDataPath build/ios-device` に変更する。
シミュレータではビルドと画面・ブリッジのテストまで確認できるが、グラスへの接続は実機で行う。

### 実装の入口

- `ios/Runner/AppDelegate.swift`: SDK の初期化とアプリ復帰時の通知
- `ios/Runner/GlassesSdkPlugin.swift`: 既存の Dart API に対応するメソッドとイベント
- `ios/Runner/Info.plist`: Bluetooth の利用目的と AccessorySetupKit の対象サービス

選択ダイアログでデバイスを選んでも、接続完了まではスキャン画面を表示する。
接続状態の通知を受けて操作画面へ切り替える。前回のデバイスIDは UserDefaults に保存し、復帰時の再接続は SDK に任せる。
Dart の `sendAIContent` は、iOS SDK 1.1.0 の `sendAiChatText` に対応する。

## テスト

```bash
flutter analyze
flutter test
```

Swift のブリッジテストは Xcode の Runner スキームでシミュレータを選択して Test を実行する。
CLI では上記の `xcodebuild` の `build` を `test` に、`-destination` を
`'platform=iOS Simulator,id=<シミュレータのUUID>'` に変更する。

実機ではデバイス選択・キャンセル、接続後のページ切り替えと各文字送信、
シングルタップ・ダブルタップ・長押しの受信、切断、アプリ復帰時の再接続を確認する。

## ライセンス

このサンプルコードは [Apache License 2.0](../../LICENSE)。
SDK 本体は対象外で、別途 SDK 利用規約が適用される（[ルート README](../../README.md) 参照）。
