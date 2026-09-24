# iOS SwiftPM sample

Swift Package Manager で配布版 Sabera iOS SDK を利用する最小サンプルです。Xcode 26 で `SaberaSwiftPMSample.xcodeproj` を開いてください。

## 準備

SDK の XCFramework は GitHub Packages から取得するため、`~/.netrc` に `read:packages` 権限を持つ PAT を設定します。

```text
machine maven.pkg.github.com
  login <GitHubのユーザー名>
  password <read:packages を持つ PAT>
```

Xcode の Package Dependencies には `https://github.com/jig-SABERA/sabera-sdk.git` の 1.1.0 が登録され、`SaberaIOS` product がアプリにリンクされます。

## ビルド

```sh
xcodebuild \
  -project samples/ios-swiftpm/SaberaSwiftPMSample.xcodeproj \
  -scheme SaberaSwiftPMSample \
  -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO \
  build
```

ターゲットの最低対応 iOS バージョンは 18.2 です。実機で動かす場合は、Xcode で開発チームを設定して署名してください。
