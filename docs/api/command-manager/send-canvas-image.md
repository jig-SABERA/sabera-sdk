---
title: sendCanvasImage
parent: CommandManager
grandparent: API リファレンス
nav_order: 40
---

# CommandManager.sendCanvasImage

```kotlin
fun sendCanvasImage(x: Int, y: Int, width: Int, height: Int, grayscale: ByteArray)
```

## 概要

キャンバスに画像を置く。送るだけで画面が切り替わるので、先にページを開く必要はない。今ある要素は残したまま、画像だけ差し替わる。渡すのはリサイズ済みのグレースケールで、左上から行優先の並び。輝度は 0-255 のまま渡してよく、3bit(0-7)への量子化とRLE圧縮はSDK内で行う。置けるのは1枚だけで、位置をずらして送っても最後の1枚しか残らない。テキスト要素とは共存でき、テキストは画像の手前に描かれる。ナビの全体ルート画像とバッファを共有しているため、ナビ表示中は使えない。数百バイトずつに分けて送るので、大きい画像ほど表示まで時間がかかる。FEATURE_VERSION 2.2.0 以上のファームが対象。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
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
commandManager.sendCanvasImage(x = 100, y = 50, width = 192, height = 192, grayscale = grayscale)
// テキストは画像の手前に描かれるので、キャプションを重ねられる
commandManager.sendCanvasElements(
    listOf(
        CommandManager.CanvasElement(id = 0, x = 100, y = 250, width = 192, height = 40, text = "キャプション"),
    ),
)
```
<!-- /snippet -->

## 関連

- [sendCanvas](send-canvas.html)
- [sendCanvasElements](send-canvas-elements.html)
- [clearCanvas](clear-canvas.html)
