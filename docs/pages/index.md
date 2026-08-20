---
title: ページごとの使い方
nav_order: 4
has_children: true
---

# ページごとの使い方

グラスは画面（ページ）ごとに使えるコマンドが決まっている。ここではページ単位に、開き方・
送るもの・後片付けをまとめる。接続は済んでいて `CommandManager` を作ってある前提で書く。
接続の手順は [Getting Started](../getting-started.html) を参照。

```kotlin
val commandManager = client.createCommandManager()
```

## ページ一覧

| ページ | 開き方 | 主に送るもの | 必要ファーム |
|---|---|---|---|
| [テレプロンプター](teleprompter.html) | `enterTeleprompterPage()` | 原稿・再生状態・経過時間 | — |
| [翻訳](translate.html) | `enterTranslatePage()` | 言語ペア・訳文 | — |
| [AI アシスタント](ai-chat.html) | `enterAiChatPage()` | 吹き出しの本文・生成状態 | — |
| [汎用テキスト表示](text.html) | `enterEmptyScreenPage()` | 本文 | — |
| [画像表示](image.html) | `enterImageDisplayPage()` | 196x196 までの画像 | — |
| [分割レイアウト](layout.html) | `sendLayout()` | 分割と領域ごとのテキスト | 2.0.0 |
| [自由配置キャンバス](canvas.html) | `sendCanvas()` | 座標指定のテキストと画像 | 2.1.0 / 画像は 2.2.0 |
| [ナビ](navigation.html) | `enterNavigationPage()` | 案内情報・進行方向・地図画像 | — |
| [調整・デバッグ](adjust.html) | `enterGlassAngleAdjustmentPage()` など | 傾き閾値・調整画像 | — |

「必要ファーム」は FEATURE_VERSION。満たさないファームはコマンドを読み捨てるので、
アプリ側からは送れたように見えて何も起こらない。

## 共通の約束

**開いてから送る。** ほとんどのページはコンテンツだけ送っても表示されない。例外は
分割レイアウトとキャンバスで、こちらは送ると同時に画面が切り替わる。

**送信は投げっぱなし。** 送信系は同期メソッドだが内部でキューに積むだけで、呼び出しスレッドは
ブロックしない。グラス側が受け取れたかどうかは返ってこない。

**未接続だと捨てられる。** 送る前に `CommandManager.connected` を見る。

**やめるときはホームに戻す。** `enterHomePage()` で開いていたページを閉じ、表示内容も捨てる。

<!-- snippet: pages.home -->
```kotlin
// 開いていたページを閉じて表示内容を捨てる
commandManager.enterHomePage()
```
<!-- /snippet -->

**1パケットの上限がある。** 原稿や本文のように分割送信に対応しているものは長文を渡してよいが、
分割レイアウトとキャンバスのテキストは分割できず、合計190バイト程度で切られる。
