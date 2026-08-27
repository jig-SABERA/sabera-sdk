---
title: sendCanvasAnimationFrame
parent: CommandManager
grandparent: API リファレンス
nav_order: 39
---

# CommandManager.sendCanvasAnimationFrame

```kotlin
fun sendCanvasAnimationFrame(width: Int, height: Int, grayscale: ByteArray)
```

## 概要

流すコマを1枚送る。startCanvasAnimation で宣言してから呼ぶ。渡すのは1画素1バイト・左上から行優先のグレースケールで、輝度は 0-255 のまま渡してよい。3bit(0-7)への量子化とRLE圧縮、チャンク分割はSDK内で行う。コマの順番は送った順で決まり、SDK が分割送信を直列化するので間隔を詰めて呼んでも1枚ずつ送り切られる。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `width` | `Int` | 1コマの幅。startCanvasAnimation で宣言した値と揃える |
| `height` | `Int` | 1コマの高さ。startCanvasAnimation で宣言した値と揃える |
| `grayscale` | `ByteArray` | 1画素1バイトのグレースケール。長さは width * height 以上 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendCanvasAnimationFrame -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [startCanvasAnimation](start-canvas-animation.html)
- [stopCanvasAnimation](stop-canvas-animation.html)
