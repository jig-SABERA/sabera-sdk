---
title: 汎用テキスト表示
parent: ページごとの使い方
nav_order: 4
---

# 汎用テキスト表示

決まった用途を持たないテキスト画面。専用ページのない情報を出したいときに使う。

## 開いて送る

`enterEmptyScreenPage()` で開き、`sendEmptyScreenContent()` で本文を送る。200バイトを
超える分は SDK が分割して送る。送るたびに表示は置き換わる。

<!-- snippet: pages.empty-screen -->
```kotlin
commandManager.enterEmptyScreenPage()
commandManager.sendEmptyScreenContent("好きな文字列をそのまま出せる")
```
<!-- /snippet -->

## やめる

`enterHomePage()` で閉じる。

文字を置く位置まで決めたいなら [分割レイアウト](layout.html) か
[自由配置キャンバス](canvas.html) を使う。

## 関連 API

- `enterEmptyScreenPage()`
- `sendEmptyScreenContent(content: String)`
