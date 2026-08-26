---
title: sendCanvasImage
parent: CommandManager
grandparent: API リファレンス
nav_order: 34
---

# CommandManager.sendCanvasImage

```kotlin
fun sendCanvasImage(id: Int, x: Int, y: Int, width: Int, height: Int, grayscale: ByteArray)
```

## 概要

キャンバスに画像を置く。送るだけで画面が切り替わるので、先にページを開く必要はない。今ある要素は残したまま、同じ id の画像だけ差し替わる。渡すのはリサイズ済みのグレースケールで、左上から行優先の並び。輝度は 0-255 のまま渡してよく、3bit(0-7)への量子化とRLE圧縮はSDK内で行う。画像は id ごとに8枚まで置けるが、グラスのバッファは全画像で共有していて、置いてある画像の width * height * 2 の合計に受信中の圧縮データを足した値が380,000バイトを超えると受け取れない。192角なら5枚が目安。画像はテキスト要素の背面に描かれる。ナビの全体ルート画像とバッファを共有しているため、ナビ表示中は使えない。数百バイトずつに分けて送るので、大きい画像ほど表示まで時間がかかる。転送しきる前に別の id を送るとファームは受信中の画像を捨てるが、SDK が分割送信を直列化するので続けて呼んでも順番に送られる。FEATURE_VERSION 2.2.0 以上のファームが対象。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `id` | `Int` | 画像の識別子。0..7 の8枚まで。同じ id に送ると座標ごと差し替わる |
| `x` | `Int` | 画像の左上のx座標。x + width は 576 まで |
| `y` | `Int` | 画像の左上のy座標。y + height は 360 まで |
| `width` | `Int` | 画像の幅 |
| `height` | `Int` | 画像の高さ |
| `grayscale` | `ByteArray` | 1画素1バイトのグレースケール。長さは width * height 以上 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendCanvasImage -->
```kotlin
// grayscale は1画素1バイト・左上から行優先。3bitへの量子化とRLE圧縮はSDKが行う
commandManager.sendCanvasImage(id = 0, x = 16, y = 84, width = 192, height = 192, grayscale = left)
// 別の id なら並べて置ける。続けて呼んでもSDKが送信を直列化する
commandManager.sendCanvasImage(id = 1, x = 368, y = 84, width = 192, height = 192, grayscale = right)
// テキストは画像の手前に描かれるので、キャプションを重ねられる
commandManager.sendCanvasElements(
    listOf(
        CommandManager.CanvasElement(id = 0, x = 16, y = 300, width = 192, height = 40, text = "左"),
    ),
)
```
<!-- /snippet -->

## 関連

- [removeCanvasImage](remove-canvas-image.html)
- [sendCanvas](send-canvas.html)
- [clearCanvas](clear-canvas.html)
