---
title: 調整・デバッグ
parent: ページごとの使い方
nav_order: 9
---

# 調整・デバッグ

かけ心地の調整と、動作確認のための画面。

## ヘッドアップ角度調整

`enterGlassAngleAdjustmentPage()` で開く。顔を上げたときに画面を点ける閾値は
`sendWakeupTiltThreshold()` で度数で送る。

<!-- snippet: pages.angle-adjustment -->
```kotlin
commandManager.enterGlassAngleAdjustmentPage()
commandManager.sendWakeupTiltThreshold(degrees = 20)
```
<!-- /snippet -->

## 画面位置調整

`sendAdjust()` は表示位置の目安になる画像を出す。ページを開くコマンドではなく、
今の画面の上に重ねる。

<!-- snippet: pages.adjust -->
```kotlin
commandManager.sendAdjust(
    status = CommandManager.AdjustStatus.SHOW,
    imageType = CommandManager.AdjustImageType.HOME,
)
commandManager.sendAdjust(
    status = CommandManager.AdjustStatus.CLOSE,
    imageType = CommandManager.AdjustImageType.HOME,
)
```
<!-- /snippet -->

| imageType | 用途 |
|---|---|
| `HOME` | ホーム画面向けの位置合わせ |
| `NAVIGATE` | ナビ画面向け |
| `TELEPROMPT` | テレプロンプター向け |

## IMU・照度のデバッグ

`enterImuDebugPage()` でセンサーの生の値を出す画面を開く。アプリ側で値を扱うなら
`imuData` を購読する。

## 関連 API

- `enterGlassAngleAdjustmentPage()`
- `sendWakeupTiltThreshold(degrees: Int)`
- `sendAdjust(status: CommandManager.AdjustStatus, imageType: CommandManager.AdjustImageType)`
- `enterImuDebugPage()`
