---
title: sendNaviLargeImage
parent: CommandManager
grandparent: API リファレンス
nav_order: 50
---

# CommandManager.sendNaviLargeImage

```kotlin
fun sendNaviLargeImage(width: Int, height: Int, encodedBitmap: ByteArray)
```

## 概要

ナビ画面に全画面サイズの地図画像を送る。sendNavi の地図と違い幅・高さが255を超えられる。

{: .note }
> 0.0.10 には含まれない。次のリリースから使える。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `width` | `Int` | 画像の幅 |
| `height` | `Int` | 画像の高さ |
| `encodedBitmap` | `ByteArray` | エンコード済みの画像データ |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendNaviLargeImage -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendNavi](send-navi.html)
