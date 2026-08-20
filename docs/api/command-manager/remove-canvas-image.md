---
title: removeCanvasImage
parent: CommandManager
grandparent: API リファレンス
nav_order: 34
---

# CommandManager.removeCanvasImage

```kotlin
fun removeCanvasImage(id: Int)
```

## 概要

置いた画像を消す。テキスト要素と他の id の画像は残る。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `id` | `Int` | 消す画像の識別子 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.removeCanvasImage -->
```kotlin
// テキスト要素と他の id の画像は残る
commandManager.removeCanvasImage(id = 1)
```
<!-- /snippet -->

## 関連

- [sendCanvasImage](send-canvas-image.html)
- [clearCanvas](clear-canvas.html)
