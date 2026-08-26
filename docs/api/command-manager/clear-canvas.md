---
title: clearCanvas
parent: CommandManager
grandparent: API リファレンス
nav_order: 36
---

# CommandManager.clearCanvas

```kotlin
fun clearCanvas()
```

## 概要

キャンバスは開いたまま、全ての要素を消す。置いた画像も一緒に消える。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.clearCanvas -->
```kotlin
commandManager.clearCanvas()
```
<!-- /snippet -->

## 関連

- [sendCanvas](send-canvas.html)
- [sendCanvasImage](send-canvas-image.html)
- [closeCanvas](close-canvas.html)
