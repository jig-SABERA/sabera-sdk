---
title: メソッドの追加履歴
nav_order: 6
---

# メソッドの追加履歴

どのメソッドがどのバージョンから使えるかの一覧。表にないメソッドは 0.0.10 以前からある。

配布しているのは Android のみで、iOS は 0.0.10 で止まっている。iOS で使えるのは
0.0.10 時点の API までになる。

## 0.8.3

メソッドの追加は無い。ビルド上の変更のみ。

- 成果物が pre-release 扱いにならなくなった。0.8.2 以前で必要だった `-Xskip-prerelease-check` は不要
- `GlassConnection` インターフェースと `PickerUnavailableException` が aar に残るようになり、接続管理を `GlassManager` 具象型ではなく `GlassConnection` で受けられる

## 0.8.1

| メソッド | 補足 |
|---|---|
| [startCanvasAnimation](api/command-manager/start-canvas-animation.html) | キャンバスに動画を流す準備をして、寸法と再生間隔を宣言する |
| [sendCanvasAnimationFrame](api/command-manager/send-canvas-animation-frame.html) | 流すコマを1枚送る |
| [stopCanvasAnimation](api/command-manager/stop-canvas-animation.html) | 流すのをやめる |

FEATURE_VERSION 2.3.0 以上のファームが対象。コマは使い捨てなので、`sendCanvasImage`
のようなバッファの容量に縛られず流し続けられる。ただしバッファを共有しているため、
静的な画像とは同時に置けない。

## 0.8.0

**充電状態の取得（`charging` / `requestSystemStatus`）が入っていないので使わないこと。**
0.8.1 と同じキャンバスのアニメーションが入っているが、0.7.0 で追加した充電状態が
欠けているため、0.7.x から上げると後退する。0.8.1 で取り込み直した。

## 0.7.3

メソッドの追加はない。0.7.0 の aar に Opus のネイティブライブラリが入っておらず、
`startMicStreaming` を呼ぶと `UnsatisfiedLinkError` でアプリごと落ちていたのを直した。
0.7.0 でマイクを使う場合は 0.7.3 に上げること。

## 0.7.0

| メソッド | 補足 |
|---|---|
| [charging](api/command-manager/charging.html) | 充電中かどうかが流れる。接続すると SDK が状態を要求するので、購読するだけでよい |

## 0.6.0

| メソッド | 補足 |
|---|---|
| [removeCanvasImage](api/command-manager/remove-canvas-image.html) | キャンバスの画像を id 指定で消す |

`sendCanvasImage` に `id` が増え、画像を8枚まで置けるようになった。ファーム側の
フレームが変わっているため、0.5.0 までの SDK とは互換がない。あわせて分割送信を
直列化し、続けて送ったときにチャンクが混ざらないようにした。

## 0.5.0

アプリ本体に実装がないメソッドを公開 API から外した。撤去したのは
`sendMeeting` / `sendAIContent` / `sendAiChatSender` / `sendEmptyScreenStatus` /
`sendTeleprompterGenerating` / `requestLog` / `requestNotificationCountSync` と、
電源・リモコンのイベントリスナー4つ。

## 0.4.0

| メソッド | 補足 |
|---|---|
| [sendCanvasImage](api/command-manager/send-canvas-image.html) | キャンバスに画像を置く。FEATURE_VERSION 2.2.0 以上 |

## 0.3.1

マイクの PCM を SDK 内で3倍に増幅するようにした。API の追加はない。

## 0.3.0

| メソッド | 補足 |
|---|---|
| [micAudio](api/command-manager/mic-audio.html) | デコード済みの PCM が流れる |
| [micStreaming](api/command-manager/mic-streaming.html) | 受信中かどうか |
| [startMicStreaming](api/command-manager/start-mic-streaming.html) | Opus のデコードまで SDK 内で行う |
| [stopMicStreaming](api/command-manager/stop-mic-streaming.html) | |

## 0.2.1

API の追加はない。

## 0.2.0

| メソッド | 補足 |
|---|---|
| [sendCanvas](api/command-manager/send-canvas.html) | 自由配置キャンバス。FEATURE_VERSION 2.1.0 以上 |
| [sendCanvasElements](api/command-manager/send-canvas-elements.html) | |
| [clearCanvas](api/command-manager/clear-canvas.html) | |
| [closeCanvas](api/command-manager/close-canvas.html) | |

## 0.1.1

| メソッド | 補足 |
|---|---|
| [sendLayout](api/command-manager/send-layout.html) | 分割レイアウト。FEATURE_VERSION 2.0.0 以上 |
| [sendLayoutTexts](api/command-manager/send-layout-texts.html) | |
| [closeLayout](api/command-manager/close-layout.html) | |

## 0.1.0

| メソッド | 補足 |
|---|---|
| [imuData](api/command-manager/imu-data.html) | 6DoF のサンプルが流れる |
| [imuDataStarted](api/command-manager/imu-data-started.html) | |
| [startImuData](api/command-manager/start-imu-data.html) | |
| [stopImuData](api/command-manager/stop-imu-data.html) | |

## 0.0.14

ファームが対応していないページ遷移（`enterAiPage` / `enterMeetingPage` /
`enterNotificationPage`）を公開 API から外した。

## 0.0.13

| メソッド | 補足 |
|---|---|
| [enterNavigationPage](api/command-manager/enter-navigation-page.html) | |
| [sendNavi](api/command-manager/send-navi.html) | 地図画像も一緒に送れる |
| [sendNaviStatus](api/command-manager/send-navi-status.html) | |
| [sendNaviLanguage](api/command-manager/send-navi-language.html) | |
| [sendNaviCourse](api/command-manager/send-navi-course.html) | |
| [sendNaviLargeImage](api/command-manager/send-navi-large-image.html) | |

## 0.0.12

画像の3bit量子化と RLE 圧縮を SDK 内に取り込んだ。
[sendImage](api/command-manager/send-image.html) に渡すのが圧縮済みデータから
グレースケールに変わっている。

## 0.0.11

| メソッド | 補足 |
|---|---|
| [enterImageDisplayPage](api/command-manager/enter-image-display-page.html) | |
| [sendImage](api/command-manager/send-image.html) | |
