---
title: sendCanvas
parent: CommandManager
grandparent: API リファレンス
nav_order: 38
---

# CommandManager.sendCanvas

```kotlin
fun sendCanvas(elements: List<CommandManager.CanvasElement>)
```

## 概要

自由配置キャンバスを開いて、要素を置き直す。送るだけで画面が切り替わるので、先にページを開く必要はない。今ある要素は全て消えてから elements が並ぶ。キャンバスは 576×360 で、座標は左上が原点。はみ出した矩形は端で切られ、キャンバスの外に出た要素は描かれない。テキストは矩形内で左揃えに折り返し、あふれた分は切られる。分割して送れないため、テキストの合計は190バイト程度までに収める。収まらないときは sendCanvasElements で1要素ずつ送れば表示は積み上がる。FEATURE_VERSION 2.1.0 以上のファームが対象。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `elements` | `List<CommandManager.CanvasElement>` | 配置する要素。id は 0..7 の8個まで |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendCanvas -->
```kotlin
// 今ある要素は消えて、渡した要素だけが 576×360 のキャンバスに並ぶ
commandManager.sendCanvas(
    listOf(
        CommandManager.CanvasElement(id = 0, x = 16, y = 8, width = 240, height = 40, text = "上"),
        CommandManager.CanvasElement(id = 1, x = 16, y = 300, width = 240, height = 40, text = "下"),
    ),
)
```
<!-- /snippet -->

## 関連

- [sendCanvasElements](send-canvas-elements.html)
- [clearCanvas](clear-canvas.html)
- [closeCanvas](close-canvas.html)
