---
title: 分割レイアウト
parent: ページごとの使い方
nav_order: 6
---

# 分割レイアウト

画面を等分して、領域ごとにテキストを出す画面。FEATURE_VERSION 2.0.0 以上のファームが対象。

## 開いて送る

`sendLayout()` を送るだけで画面が切り替わる。先にページを開く必要はない。モードを送ると
レイアウトは作り直され、全領域のテキストが消えたうえで `texts` が反映される。

<!-- snippet: pages.layout -->
```kotlin
// 開く操作は要らない。モードを送るとその場で切り替わる
commandManager.sendLayout(
    mode = CommandManager.LayoutMode.QUAD,
    texts = mapOf(0 to "左上", 1 to "右上", 2 to "左下", 3 to "右下"),
)
// 分割はそのまま、右下だけ書き換える
commandManager.sendLayoutTexts(mapOf(3 to "書き換え"))
// 空文字でその領域を消す
commandManager.sendLayoutTexts(mapOf(3 to ""))
commandManager.closeLayout()
```
<!-- /snippet -->

領域番号の意味は分割ごとに変わる。

| mode | 領域番号 |
|---|---|
| `FULL` | 0=全画面 |
| `TOP_BOTTOM` | 0=上、1=下 |
| `LEFT_RIGHT` | 0=左、1=右 |
| `QUAD` | 0=左上、1=右上、2=左下、3=右下 |

渡さなかった領域は空のまま。テキストは領域内で折り返し、あふれた分は切られる。

## 一部だけ書き換える

`sendLayoutTexts()` は分割を保ったまま、指定した領域のテキストだけ差し替える。空文字を
渡すとその領域が消える。レイアウトが閉じているときに呼ぶと、全画面1領域として開く。

## やめる

`closeLayout()` で閉じ、表示していたテキストは破棄される。リモコンの戻る操作や
ホームへの遷移でも閉じる。

## 制限

分割して送れないコマンドなので、**テキストの合計は190バイト程度まで**。超えた分は届かない。
それより多くの文字を出したいなら領域を分けるのではなく [テレプロンプター](teleprompter.html) を使う。

## 関連 API

- `sendLayout(mode: CommandManager.LayoutMode, texts: Map<Int, String>)`
- `sendLayoutTexts(texts: Map<Int, String>)`
- `closeLayout()`
