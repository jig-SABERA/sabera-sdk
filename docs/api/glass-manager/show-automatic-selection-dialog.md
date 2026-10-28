---
title: showAutomaticSelectionDialog
parent: GlassManager
grandparent: API リファレンス
nav_order: 7
---

# GlassManager.showAutomaticSelectionDialog

```kotlin
suspend fun showAutomaticSelectionDialog(context: Context): GlassClient?
suspend fun showAutomaticSelectionDialog(
    context: Context,
    beforeConnect: suspend () -> Boolean,
): GlassClient?
```

## 概要

OS のデバイス選択 UI を開き、選ばれたグラスを返す。選ばれなかった場合は `null`。
Android は返された相手に `connect` を呼ぶ。iOS は SDK 内部で接続を開始するので、
`connectedDevice` で接続完了を受け取る。iOS の `context` は `Unit` を渡す。

1.1.2 以降は `beforeConnect` を指定できる。iOS でアクセサリへの接続を始める直前に呼ばれ、
`false` を返すと接続せず `null` を返す。登録済みのアクセサリへつなぐ場合にも呼ばれる。
Android ではこの処理を呼ばず、従来と同じ選択動作になる。
引数が `context` だけの呼び出しも引き続き使える。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `context` | `Context` | Android は Activity、iOS は Unit |
| `beforeConnect` | `suspend () -> Boolean` | iOS の接続開始前の確認。true で続行、false で中止 |

## 戻り値

`GlassClient?`

## 使用例

<!-- snippet: GlassManager.showAutomaticSelectionDialog -->
```kotlin
// 第1引数は Activity。Application Context を渡すとダイアログが出ない
val client = manager.showAutomaticSelectionDialog(activity)
```
<!-- /snippet -->

## 関連

- [connect](connect.md)
- [createClientFromDeviceID](create-client-from-device-id.md)
