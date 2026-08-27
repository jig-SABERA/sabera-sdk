---
title: sendImage
parent: CommandManager
grandparent: API リファレンス
nav_order: 49
---

# CommandManager.sendImage

```kotlin
fun sendImage(width: Int, height: Int, grayscale: ByteArray)
```

## 概要

画像表示ページに画像を送る。enterImageDisplayPage で開いてから呼ぶ。渡すのはリサイズ済みのグレースケールで、1画素1バイト・左上から行優先の並び。輝度は 0-255 のままでよく、グラスが読む3bitへの量子化とRLE圧縮は SDK が行う。グラス側のバッファは静的で、196x196 を超えるサイズはファーム側で弾かれ、何も表示されない。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `width` | `Int` | 画像の幅。196まで |
| `height` | `Int` | 画像の高さ。196まで |
| `grayscale` | `ByteArray` | 1画素1バイトのグレースケール。長さは `width * height` 以上 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendImage -->
```kotlin
commandManager.enterImageDisplayPage()
// grayscale は1画素1バイト・左上から行優先。3bitへの量子化とRLE圧縮はSDKが行う
commandManager.sendImage(width = 196, height = 196, grayscale = grayscale)
```
<!-- /snippet -->

## 関連

- [enterImageDisplayPage](enter-image-display-page.html)
