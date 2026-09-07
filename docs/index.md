---
title: ホーム
nav_order: 1
---

# Sabera App SDK

SABERAグラスと通信するアプリを作るための SDK。Android / iOS で同じ API を使う。

## SABERA グラスのスペック

アプリ開発に関わるハードウェアの要点。

| 項目 | 値 |
|---|---|
| ディスプレイ | 640x480、右目単眼 |
| 通信 | Bluetooth Low Energy 5.3 |
| マイク | 2個。PCM16 / 16kHz モノラル |
| タッチセンサー | 1つ。タップ・ダブルタップ・長押し |
| IMU | 6DoF |
| カメラ / スピーカー | なし |

## できること

- 用意済みのページ（テレプロンプター・翻訳・AI アシスタント・ナビなど）にテキストを流す
- 自由配置キャンバスにテキストと画像を座標指定で置く
- タッチ操作・マイク音声・IMU を受け取ってアプリ側で処理する

## ドキュメント

- [Getting Started](getting-started.html) — セットアップと基本的な使い方
- [GitHub PAT の作り方](github-pat.html) — SDK 取得に必要なトークンの発行手順
- [ページごとの使い方](pages/) — グラスに用意された画面ごとの呼び出しフロー
- [API リファレンス](api/) — 公開 API の一覧
- [更新履歴](api-history.html) — リファレンスとSDKの更新履歴
- [サードパーティ表記](third-party-notices.html) — SDKで利用しているサードパーティ

## サンプル

| サンプル | プラットフォーム |
| --- | --- |
| [Flutter](https://github.com/jig-SABERA/sabera-sdk/tree/main/samples/flutter) | Android |
| [KMP](https://github.com/jig-SABERA/sabera-sdk/tree/main/samples/kmp) | Android / iOS |
