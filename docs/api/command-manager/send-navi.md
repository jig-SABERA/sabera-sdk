---
title: sendNavi
parent: CommandManager
grandparent: API リファレンス
nav_order: 60
---

# CommandManager.sendNavi

```kotlin
fun sendNavi(
    maneuverIcon: CommandManager.ManeuverIcon,
    instructionText: String,
    distanceText: String,
    estimatedArrivalText: String,
    timeAndDistanceText: String,
    bitmapWidth: Int? = null,
    bitmapHeight: Int? = null,
    grayscale: ByteArray? = null,
)
```

## 概要

ナビの案内情報を送る。事前に sendNaviStatus で `START` にしておく。地図画像は sendImage と同じく1画素1バイト・左上から行優先のグレースケールで、3bitへの量子化とRLE圧縮は SDK が行う。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `maneuverIcon` | `CommandManager.ManeuverIcon` | 次のポイントの進行方向アイコン。`TURN_LEFT` / `STRAIGHT` など |
| `instructionText` | `String` | 次のポイントでの指示。「○○を右折」など |
| `distanceText` | `String` | 次のポイントまでの距離。「300m」など |
| `estimatedArrivalText` | `String` | 予想到着時刻 |
| `timeAndDistanceText` | `String` | 画面左下に出す残り時間と距離 |
| `bitmapWidth` | `Int?` | 地図画像の幅。255まで。`grayscale` があるときは必須 |
| `bitmapHeight` | `Int?` | 地図画像の高さ。255まで。`grayscale` があるときは必須 |
| `grayscale` | `ByteArray?` | 地図画像。長さは `bitmapWidth * bitmapHeight` 以上 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendNavi -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [enterNavigationPage](enter-navigation-page.html)
- [sendNaviStatus](send-navi-status.html)
- [sendNaviLargeImage](send-navi-large-image.html)
