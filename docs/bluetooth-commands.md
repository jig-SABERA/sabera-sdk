---
title: Bluetooth コマンドリスト
nav_order: 7
has_children: true
---

# Bluetooth コマンドリスト

SABERAグラスとBluetoothで通信するための公開コマンド仕様。

## 直接送信時の注意事項

{: .warning }
> Bluetoothコマンドの直接送信は、利用者自身の責任で行ってください。
> 直接送信によって生じたグラスの不具合・故障・データ損失等は、法令上認められる範囲で、
> 当社の保証・サポートの対象外とし、当社は責任を負いません。

## 通信先

| 用途 | Service UUID | Characteristic UUID |
|---|---|---|
| コマンド送信 | `F48A23C0-F69A-11E8-8EB2-F2801F1B9FD1` | `F48A24C1-F69A-11E8-8EB2-F2801F1B9FD1` |
| コマンド受信 | `F48A23C0-F69A-11E8-8EB2-F2801F1B9FD1` | `F48A25C2-F69A-11E8-8EB2-F2801F1B9FD1` |
| 音声受信 | `E49A3001-F69A-11E8-8EB2-F2801F1B9FD1` | `E49A3003-F69A-11E8-8EB2-F2801F1B9FD1` |

## 通常パケットの形式

バイト列は16進数、長さはバイト数で表記する。
TLV は Type（種類）、Length（長さ）、Value（値）を並べたもの。

### パケット全体

| ヘッダ | コマンドID | Type | Length | Value：ペイロード |
|---|---|---|---|---|
| `01` | コマンドID | `80` | ペイロード長（byte） | 内側TLV ① → 内側TLV ② → … |
| 1 byte | 1 byte | 1 byte | 2 byte | 可変長 |

### ペイロード内のTLV

| Type | Length | Value |
|---|---|---|
| フィールドの種類 | 値の長さ（byte） | 値 |
| 1 byte | 2 byte | 可変長 |

Lengthは外側・内側ともにリトルエンディアン（下位バイトが先）。
文字列はUTF-8。通知の日時は8バイトのビッグエンディアン。

`LE(n)` は整数nを2 byteのリトルエンディアンで表した値。`BE` はビッグエンディアン。
LengthはValueのbyte数。外側Lengthは内側TLV全体のbyte数。
各コマンドページのNは本文または圧縮データのbyte数。

### 例：ホーム画面を開く

| ヘッダ | コマンドID | 外側Type | 外側Length | 内側Type | 内側Length | 内側Value |
|---|---|---|---|---|---|---|
| `01` | `05` | `80` | `05 00` | `01` | `02 00` | `32 00` |
| 固定値 | 画面遷移 | 固定値 | ペイロード5 byte | 画面ID | 値2 byte | ホーム `0x0032` |

`sendCommand()` にヘッダを含む10バイト全体を渡す。

```text
01 05 80 05 00 01 02 00 32 00
```

### テキストの分割

翻訳・テレプロンプター・汎用テキストなどの分割テキストでは、テキストTLVのValueの先頭に
次の2バイトを付ける。分割位置はUTF-8の文字境界に合わせる。

| マーカー | 意味 |
|---|---|
| `5A 6B` | 1パケットで完結 |
| `5A 5A` | 分割した先頭 |
| `7C 7C` | 分割した途中 |
| `6B 6B` | 分割した末尾 |

### テキストを表示する例

コマンド送信用Characteristicに、次のパケットを順番に書き込む。
1つ目で汎用テキスト画面を開き、2つ目で `Hello` を表示する。

```text
01 05 80 05 00 01 02 00 47 00
01 16 80 0A 00 01 07 00 5A 6B 48 65 6C 6C 6F
```

2つ目のパケットは、コマンド `16`、外側の長さ10、テキストTLVのType `01`、
Valueの長さ7、単一パケットのマーカー `5A 6B`、UTF-8の `Hello` で構成される。
任意の文字列に置き換えるときは、UTF-8に変換したバイト数に合わせて両方の長さを更新する。
汎用テキストの本文は1パケット最大200バイト。超える場合は上記マーカーで分割し、
各パケットに通常ヘッダとテキストTLVを付けて順番に送る。
分割した一連のパケットを送信し終えてから、次のテキストを送る。

## コマンド送信

| ID | 用途 | 主な内側TLV（Type） | 対応API |
|---|---|---|---|
| [`0x01`](bluetooth-commands/tx-01.md) | 時刻同期 | `01`: 年・月・日・時・分・秒・曜日 | `syncTime()` |
| [`0x03`](bluetooth-commands/tx-03.md) | 通知 | `01`: アプリ名、`02`: 件名、`03`: 日時、`04`: 本文、`05`: 件数 | `sendMessage()` / `syncNotificationCount()` |
| [`0x04`](bluetooth-commands/tx-04.md) | テレプロンプター | `01`: 本文、`02`: 行移動、`03`: 進捗、`04`: 生成中、`05`: 状態、`06`: 時間、`07`: モード | `sendTeleprompterContent()` / `sendTeleprompterLine()` / `sendTeleprompterStatus()` / `sendTeleprompterTime()` |
| [`0x05`](bluetooth-commands/tx-05.md) | 画面遷移 | `01`: 画面ID | 画面ID一覧 |
| [`0x06`](bluetooth-commands/tx-06.md) | 設定変更・同期要求 | `01`: キー、`02`: 整数、`03`: 真偽値、`04`: 文字列、`05`: バイト列、`80`: 同期要求 | `sendSetting()` / `requestSettingSync()` |
| [`0x07`](bluetooth-commands/tx-07.md) | 翻訳 | `01`: 本文、`02`: 翻訳元言語、`03`: 翻訳先言語 | `sendTranslateContent()` / `sendTranslateLanguage()` |
| [`0x09`](bluetooth-commands/tx-09.md) | マイク制御 | `01`: 開く（チャンネル）、`02`: 閉じる | `openGlassMic()` / `closeGlassMic()`。`startMicStreaming()` / `stopMicStreaming()` からも使用 |
| [`0x0A`](bluetooth-commands/tx-0a.md) | システム状態要求 | `01`: `00 00` | `requestSystemStatus()` |
| [`0x0B`](bluetooth-commands/tx-0b.md) | 天気同期 | `01`: 気温、`02`: アイコン | `syncWeather()` |
| [`0x10`](bluetooth-commands/tx-10.md) | ナビゲーション | `01`: 状態、`02`: アイコン、`03`–`06`: 案内テキスト、`07`–`09`: 小画像、`0A`–`0C`: 大画像、`0D`: 進行方位、`0E`: 言語 | `sendNavi()` / `sendNaviStatus()` / `sendNaviCourse()` / `sendNaviLargeImage()` / `sendNaviLanguage()` |
| [`0x11`](bluetooth-commands/tx-11.md) | 表示調整 | `01`: 状態、`02`: 画像種別 | `sendAdjust()` |
| [`0x12`](bluetooth-commands/tx-12.md) | AIチャット | `01`: 本文、`02`: 送信者、`03`: 状態、`04`: モデル、`05`: 言語、`06`: クリア | `sendAiChatText()` / `sendAiChatSenderText()` / `sendAiChatStatus()` / `sendAiChatSenderStatus()` / `sendAiChatLanguage()` / `clearAiChat()` / `clearAiChatLegacy()` |
| [`0x13`](bluetooth-commands/tx-13.md) | 汎用画像 | `01`: 幅、`02`: 高さ、`03`: 画像データ | `sendImage()` |
| [`0x14`](bluetooth-commands/tx-14.md) | ウェイクアップ傾き閾値 | `01`: 角度 | `sendWakeupTiltThreshold()` |
| [`0x15`](bluetooth-commands/tx-15.md) | 設定画面の表示制御 | `01`: 表示状態 | `sendSettingPageVisibility()` |
| [`0x16`](bluetooth-commands/tx-16.md) | 汎用テキスト | `01`: 本文、`02`: 状態 | `sendEmptyScreenContent()`（本文） |
| [`0x17`](bluetooth-commands/tx-17.md) | テレプロンプター・翻訳テキストのクリア | ペイロードなし | `clearInscriptionText()` |
| [`0x18`](bluetooth-commands/tx-18.md) | デバッグ用スマホ名 | `01`: 名前 | `sendDebugPhoneName()` |
| [`0x19`](bluetooth-commands/tx-19.md) | IMU送信制御 | `01`: 開始 `01 00` / 停止 `00 00` | `startImuData()` / `stopImuData()` |
| [`0x1A`](bluetooth-commands/tx-1a.md) | 分割レイアウト | `01`: モード、`02`: 領域テキスト | `sendLayout()` / `sendLayoutTexts()` / `closeLayout()` |
| [`0x1B`](bluetooth-commands/tx-1b.md) | キャンバス | `01`: 制御、`02`: 要素、`03`: 画像、`04`: アニメーション開始、`05`: フレーム、`06`: 停止 | `sendCanvas()` / `sendCanvasElements()` / `sendCanvasImage()` / `removeCanvasImage()` / `clearCanvas()` / `closeCanvas()` / `startCanvasAnimation()` / `sendCanvasAnimationFrame()` / `stopCanvasAnimation()` |

## コマンド受信

| ID | 用途 |
|---|---|
| [`0x80`](bluetooth-commands/rx-80.md) | 汎用応答 |
| [`0x82`](bluetooth-commands/rx-82.md) | キー・ジェスチャー |
| [`0x83`](bluetooth-commands/rx-83.md) | 設定同期応答 |
| [`0x87`](bluetooth-commands/rx-87.md) | システム状態応答 |
| [`0x88`](bluetooth-commands/rx-88.md) | 通知件数応答 |
| [`0x89`](bluetooth-commands/rx-89.md) | IMUデータ |

受信パケットも `01` → コマンドID → `80` → Length（2 byte、LE）→ 内側TLVの構成。

## 関連

- [メソッドの追加履歴](api-history.md) — APIの追加時期と対応ファームウェア
- [CommandManager](api/command-manager/) — コマンドの生成と送信を行うAPI
- [sendCommand](api/glass-client/send-command.md) — パケットを1つ送信
- [sendCommandList](api/glass-client/send-command-list.md) — パケットを順番に送信
- [ページごとの使い方](pages/) — 画面遷移からコンテンツ送信までの呼び出し方
