---
title: sendLayoutTexts
parent: CommandManager
grandparent: API リファレンス
nav_order: 36
---

# CommandManager.sendLayoutTexts

```kotlin
fun sendLayoutTexts(texts: Map<Int, String>)
```

## 概要

分割を保ったまま、指定した領域のテキストだけ差し替える。レイアウトが閉じているときは全画面1領域として開く。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `texts` | `Map<Int, String>` | 領域番号ごとの表示テキスト。空文字でその領域を消す |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendLayoutTexts -->
```kotlin
// 分割は変えず、右側だけ差し替える
commandManager.sendLayoutTexts(mapOf(1 to "書き換え"))
```
<!-- /snippet -->

## 関連

- [sendLayout](send-layout.html)
- [closeLayout](close-layout.html)
