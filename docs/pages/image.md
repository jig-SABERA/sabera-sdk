---
title: 画像表示
parent: ページごとの使い方
nav_order: 5
---

# 画像表示

1枚の画像を全画面で出す画面。技適マークの表示に使っている。

## 開いて送る

`enterImageDisplayPage()` で開き、`sendImage()` で画像を送る。

<!-- snippet: pages.image-display -->
```kotlin
commandManager.enterImageDisplayPage()
// 196x196 を超えるとファームが弾いて何も出ない
commandManager.sendImage(width = 196, height = 196, grayscale = grayscale)
```
<!-- /snippet -->

渡すのはリサイズ済みのグレースケール。1画素1バイトで、左上から行優先に並べる。輝度は
0-255 のまま渡してよく、グラスが読む3bitへの量子化と RLE 圧縮は SDK が行う。

グラス側のバッファは静的なので、**196x196 を超えるサイズはファームが弾いて何も表示されない**。
渡す前にアプリ側で縮小しておく。

## 使い分け

| したいこと | 使うもの |
|---|---|
| 1枚を全画面で出す | このページ |
| 複数枚を座標指定で並べる | [自由配置キャンバス](canvas.html) の `sendCanvasImage()` |
| 地図を出す | [ナビ](navigation.html) の `sendNavi()` / `sendNaviLargeImage()` |

## 関連 API

- `enterImageDisplayPage()`
- `sendImage(width: Int, height: Int, grayscale: ByteArray)`
