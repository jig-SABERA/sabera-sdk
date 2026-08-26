---
title: sendNaviLargeImage
parent: CommandManager
grandparent: API リファレンス
nav_order: 54
---

# CommandManager.sendNaviLargeImage

```kotlin
fun sendNaviLargeImage(width: Int, height: Int, grayscale: ByteArray)
```

## 概要

ナビ画面に全体ルートの地図画像を送る。sendNavi に載せる地図より大きいサイズを扱える。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `width` | `Int` | 画像の幅 |
| `height` | `Int` | 画像の高さ |
| `grayscale` | `ByteArray` | 1画素1バイトのグレースケール。長さは `width * height` 以上 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendNaviLargeImage -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [sendNavi](send-navi.html)
