---
title: ナビ
parent: ページごとの使い方
nav_order: 8
---

# ナビ

次の曲がり角・距離・到着時刻と地図を出す画面。経路の計算はアプリ側で行い、表示内容だけを送る。

## 開いて送る

`enterNavigationPage()` で開き、`sendNaviLanguage()` で言語を決め、`sendNaviStatus()` で
状態を送ってから `sendNavi()` で案内を送る。

<!-- snippet: pages.navigation -->
```kotlin
commandManager.enterNavigationPage()
commandManager.sendNaviLanguage("JPN")
commandManager.sendNaviStatus(CommandManager.NaviStatus.START)
commandManager.sendNavi(
    maneuverIcon = CommandManager.ManeuverIcon.TURN_LEFT,
    instructionText = "交差点を左折",
    distanceText = "300m",
    estimatedArrivalText = "12:34",
    timeAndDistanceText = "10分 / 1.2km",
    bitmapWidth = 128,
    bitmapHeight = 128,
    grayscale = map,
)
// 方位のドリフト補正。案内中は数秒おきに送る
commandManager.sendNaviCourse(courseDegrees)
// 着いたら到着画面に切り替える
commandManager.sendNaviStatus(CommandManager.NaviStatus.ARRIVED)
```
<!-- /snippet -->

| status | 画面 |
|---|---|
| `READY` | 案内前の待機 |
| `START` | 案内中。`sendNavi()` の内容が出るのはこの状態のときだけ |
| `ARRIVED` | 到着 |

`sendNavi()` の地図は省略できる。渡すときは `bitmapWidth` と `bitmapHeight` も必須で、
どちらも255まで。全体ルートのように大きい画像は `sendNaviLargeImage()` で送る。こちらは
[キャンバス](canvas.html)の画像とグラス側のバッファを共有している。

言語コードは `"JPN"` / `"ENG"` などの3文字。到着時刻ラベルの切り替えに使われ、画面遷移や
表示状態には影響しない。

## 方位を送り続ける

グラスは磁力計を持たず、方位を単体で保てない。案内中は数秒おきに `sendNaviCourse()` で
端末の GPS 進行方向を送り、ジャイロのドリフトを補正する。北を0とした時計回りの度数で、
0以上360未満。

## やめる

`enterHomePage()` で閉じる。

## 関連 API

- `enterNavigationPage()`
- `sendNaviLanguage(languageCode: String)`
- `sendNaviStatus(status: CommandManager.NaviStatus)`
- `sendNavi(maneuverIcon: CommandManager.ManeuverIcon, instructionText: String, distanceText: String, estimatedArrivalText: String, timeAndDistanceText: String, bitmapWidth: Int?, bitmapHeight: Int?, grayscale: ByteArray?)`
- `sendNaviLargeImage(width: Int, height: Int, grayscale: ByteArray)`
- `sendNaviCourse(courseDegrees: Double)`
