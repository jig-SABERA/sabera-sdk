---
title: sendNavi
parent: CommandManager
grandparent: API リファレンス
nav_order: 49
---

# CommandManager.sendNavi

```kotlin
fun sendNavi(
    maneuverIcon: Byte,
    instructionText: String,
    distanceText: String,
    estimatedArrivalText: String,
    timeAndDistanceText: String,
    bitmapWidth: Int? = null,
    bitmapHeight: Int? = null,
    encodedBitmap: ByteArray? = null,
)
```

## 概要

ナビの案内情報を送る。画像は分割して送られる。

{: .note }
> 0.0.10 には含まれない。次のリリースから使える。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `maneuverIcon` | `Byte` | 進行方向アイコンの種別 |
| `instructionText` | `String` | 次のポイントでの指示 |
| `distanceText` | `String` | 次のポイントまでの距離 |
| `estimatedArrivalText` | `String` | 予想到着時刻 |
| `timeAndDistanceText` | `String` | 画面左下に出す残り時間と距離 |
| `bitmapWidth` | `Int?` | 地図画像の幅。255まで。画像を渡すときは必須 |
| `bitmapHeight` | `Int?` | 地図画像の高さ。255まで。画像を渡すときは必須 |
| `encodedBitmap` | `ByteArray?` | 地図画像 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendNavi -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendNaviStatus](send-navi-status.html)
- [sendNaviLargeImage](send-navi-large-image.html)
