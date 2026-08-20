---
title: 翻訳
parent: ページごとの使い方
nav_order: 2
---

# 翻訳

訳文と言語ペアを出す画面。翻訳そのものはアプリ側で行い、結果だけを送る。

## 開く

`enterTranslatePage()` を呼ぶ。次に `sendTranslateLanguage()` で言語ラベルを決め、
`sendTranslateContent()` で本文を送る、の順で使う。

言語コードは `"en"` / `"ja"` のような2文字。ラベルの表示だけを切り替えるコマンドなので、
画面遷移は起こらない。

## 訳文を送る

送るたびに表示は置き換わる。長い本文は SDK が分割して送る。

<!-- snippet: pages.translate -->
```kotlin
commandManager.enterTranslatePage()
commandManager.sendTranslateLanguage(source = "en", target = "ja")
sentences.forEach { sentence ->
    // 送るたび全文が置き換わる
    commandManager.sendTranslateContent(sentence)
}
commandManager.clearInscriptionText()
```
<!-- /snippet -->

## 消す・やめる

`clearInscriptionText()` で消える。テレプロンプターと同じバッファを使っている。

ページを離れるときは `enterHomePage()`。

## 関連 API

- `enterTranslatePage()`
- `sendTranslateLanguage(source: String, target: String)`
- `sendTranslateContent(content: String)`
- `clearInscriptionText()`
