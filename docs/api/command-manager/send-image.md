---
title: sendImage
parent: CommandManager
grandparent: API リファレンス
nav_order: 41
---

# CommandManager.sendImage

```kotlin
fun sendImage(width: Int, height: Int, encodedBitmap: ByteArray)
```

## 概要

画像表示ページに画像を送る。enterImageDisplayPage で開いてから呼ぶ。ビットマップは3bitグレースケールをRLE圧縮したバイト列で、エンコードは呼び出し側で行う。グラス側のバッファは静的で、196x196 を超えるサイズはファーム側で弾かれ、何も表示されない。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `width` | `Int` | 画像の幅。196まで |
| `height` | `Int` | 画像の高さ。196まで |
| `encodedBitmap` | `ByteArray` | エンコード済みの画像データ |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendImage -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [enterImageDisplayPage](enter-image-display-page.html)
