---
title: sendLayout
parent: CommandManager
grandparent: API リファレンス
nav_order: 31
---

# CommandManager.sendLayout

```kotlin
fun sendLayout(
    mode: CommandManager.LayoutMode,
    texts: Map<Int, String> = emptyMap(),
)
```

## 概要

分割レイアウトを開いて、分割と初期テキストを送る。送るだけで画面が切り替わるので、先にページを開く必要はない。モードを送るとレイアウトは作り直され、全領域のテキストが消えたうえで texts が反映される。領域番号は分割ごとに意味が変わり、`TOP_BOTTOM` なら 0=上・1=下、`QUAD` なら 0=左上・1=右上・2=左下・3=右下。渡さなかった領域は空のまま。テキストは領域内で折り返し、あふれた分は切られる。分割して送れないため、テキストの合計は190バイト程度までに収める。FEATURE_VERSION 2.0.0 以上のファームが対象。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `mode` | `CommandManager.LayoutMode` | `FULL` / `TOP_BOTTOM` / `LEFT_RIGHT` / `QUAD` |
| `texts` | `Map<Int, String>` | 領域番号ごとの表示テキスト |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendLayout -->
```kotlin
// モードを送ると全領域がクリアされ、同じパケットのテキストが反映される
commandManager.sendLayout(
    mode = CommandManager.LayoutMode.LEFT_RIGHT,
    texts = mapOf(0 to "左", 1 to "右"),
)
```
<!-- /snippet -->

## 関連

- [sendLayoutTexts](send-layout-texts.html)
- [closeLayout](close-layout.html)
