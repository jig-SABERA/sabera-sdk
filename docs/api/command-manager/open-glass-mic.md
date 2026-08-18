---
title: openGlassMic
parent: CommandManager
grandparent: API リファレンス
nav_order: 19
---

# CommandManager.openGlassMic

```kotlin
fun openGlassMic()
fun openGlassMic(channel: Int)
```

## 概要

グラスのマイクを開く。開いている間、音声は `GlassClient.addAudioDataEventListener` に届く。引数なしの呼び出しは EVT1 互換のチャンネル0を開く。

{: .note }
> チャンネル指定の overload は 0.0.10 には含まれない。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `channel` | `Int` | `0`=MIC0, `1`=MIC1, `2`=MIC2 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.openGlassMic -->
```kotlin
commandManager.openGlassMic()
// … 録音が終わったら閉じる
commandManager.closeGlassMic()
```
<!-- /snippet -->

## 関連

- [closeGlassMic](close-glass-mic.html)
