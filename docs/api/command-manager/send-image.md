---
title: sendImage
parent: CommandManager
grandparent: API リファレンス
nav_order: 42
---

# CommandManager.sendImage

```kotlin
fun sendImage(width: Int, height: Int, encodedBitmap: ByteArray)
```

## 概要

汎用画像表示ページに画像を送る。2bits/pixel のグレースケールを RLE 圧縮した形式を想定している。

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

<!-- snippet: CommandManager.sendImage -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [enterImageDisplayPage](enter-image-display-page.html)
