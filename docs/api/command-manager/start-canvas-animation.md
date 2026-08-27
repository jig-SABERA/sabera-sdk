---
title: startCanvasAnimation
parent: CommandManager
grandparent: API リファレンス
nav_order: 38
---

# CommandManager.startCanvasAnimation

```kotlin
fun startCanvasAnimation(x: Int, y: Int, width: Int, height: Int, intervalMs: Int)
```

## 概要

キャンバスに動画を流す準備をして、寸法と再生間隔を宣言する。グラスは画像バッファを1コマぶんのスロットに切り直してリングバッファにするため、宣言した時点で sendCanvasImage で置いた画像は破棄される。テキスト要素は残る。コマは使い捨てなので、長さの制限なく流し続けられる。2枚たまってから再生が始まり、送信が間に合わないときは直前のコマを出したまま待つので、黒画面にもコマ飛びにもならない。ナビの全体ルート画像とバッファを共有しているため、ナビ表示中は使えない。実効fpsはBLEのスループットが天井になる。FEATURE_VERSION 2.3.0 以上のファームが対象。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `x` | `Int` | コマの左上のx座標。x + width は 576 まで |
| `y` | `Int` | コマの左上のy座標。y + height は 360 まで |
| `width` | `Int` | 1コマの幅 |
| `height` | `Int` | 1コマの高さ |
| `intervalMs` | `Int` | 1コマの表示時間[ms] |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.startCanvasAnimation -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendCanvasAnimationFrame](send-canvas-animation-frame.html)
- [stopCanvasAnimation](stop-canvas-animation.html)
- [sendCanvasImage](send-canvas-image.html)
