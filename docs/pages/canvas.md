---
title: 自由配置キャンバス
parent: ページごとの使い方
nav_order: 7
---

# 自由配置キャンバス

座標を指定してテキストと画像を置く画面。キャンバスは 576x360 で、左上が原点。
FEATURE_VERSION 2.1.0 以上のファームが対象（画像は 2.2.0 以上）。

## 開いて送る

`sendCanvas()` か `sendCanvasImage()` を送るだけで画面が切り替わる。先にページを開く
必要はない。

<!-- snippet: pages.canvas -->
```kotlin
commandManager.sendCanvas(
    listOf(
        CommandManager.CanvasElement(id = 0, x = 16, y = 8, width = 240, height = 40, text = "見出し"),
    ),
)
// 画像はテキストの背面に置かれる
commandManager.sendCanvasImage(id = 0, x = 16, y = 84, width = 192, height = 192, grayscale = photo)
// 他の要素を残して id 1 だけ足す
commandManager.sendCanvasElements(
    listOf(
        CommandManager.CanvasElement(id = 1, x = 16, y = 300, width = 240, height = 40, text = "キャプション"),
    ),
)
commandManager.closeCanvas()
```
<!-- /snippet -->

`sendCanvas()` は今ある要素を全て消してから、渡した要素を並べる。今ある要素を残したまま
一部だけ置き直すなら `sendCanvasElements()`。既にある id に送ると座標とサイズごと差し替わり、
テキストを空にするとその id が消える。

はみ出した矩形は端で切られ、キャンバスの外に出た要素は描かれない。テキストは矩形内で
左揃えに折り返し、あふれた分は切られる。

## 画像を置く

`sendCanvasImage()` の `grayscale` は画像表示ページと同じ形式で、1画素1バイト・左上から
行優先。量子化と圧縮は SDK が行う。同じ id に送ると座標ごと差し替わり、`removeCanvasImage()`
で1枚だけ消せる。画像はテキスト要素の背面に描かれるので、キャプションを重ねられる。

数百バイトずつに分けて送るため、大きい画像ほど表示までに時間がかかる。続けて呼んでも
SDK が分割送信を直列化するので、送信が混ざることはない。

## 制限

| 対象 | 上限 |
|---|---|
| テキスト要素 | id 0..7 の8個 |
| 画像 | id 0..7 の8枚 |
| テキストの合計 | 190バイト程度（分割送信できない） |
| 画像のバッファ | 置いてある画像の `width * height * 2` の合計 + 受信中の圧縮データが 380,000 バイトまで |

192角なら5枚が目安。テキストが収まらないときは `sendCanvasElements()` で1要素ずつ送れば
表示は積み上がる。

画像バッファはナビの全体ルート画像と共有しているため、**ナビ表示中はキャンバスの画像は使えない**。

## 消す・やめる

`clearCanvas()` はキャンバスを開いたまま全ての要素を消す。置いた画像も一緒に消える。
`closeCanvas()` は閉じてホームなどに戻り、表示していた要素は破棄される。リモコンの
戻る操作やホームへの遷移でも閉じる。

## 関連 API

- `sendCanvas(elements: List<CommandManager.CanvasElement>)`
- `sendCanvasElements(elements: List<CommandManager.CanvasElement>)`
- `sendCanvasImage(id: Int, x: Int, y: Int, width: Int, height: Int, grayscale: ByteArray)`
- `removeCanvasImage(id: Int)`
- `clearCanvas()`
- `closeCanvas()`
