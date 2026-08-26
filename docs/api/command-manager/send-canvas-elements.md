---
title: sendCanvasElements
parent: CommandManager
grandparent: API リファレンス
nav_order: 33
---

# CommandManager.sendCanvasElements

```kotlin
fun sendCanvasElements(elements: List<CommandManager.CanvasElement>)
```

## 概要

今ある要素を残したまま、渡した要素だけ置き直す。既にある id に送ると座標とサイズごと差し替わる。キャンバスが閉じているときは新しく開く。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `elements` | `List<CommandManager.CanvasElement>` | 配置する要素。テキストが空の要素はその id を消す |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendCanvasElements -->
```kotlin
// 他の要素は残したまま id 1 だけ差し替える。テキストを空にすると消える
commandManager.sendCanvasElements(
    listOf(
        CommandManager.CanvasElement(id = 1, x = 16, y = 300, width = 240, height = 40, text = "書き換え"),
    ),
)
```
<!-- /snippet -->

## 関連

- [sendCanvas](send-canvas.html)
- [clearCanvas](clear-canvas.html)
- [closeCanvas](close-canvas.html)
