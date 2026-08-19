---
title: openGlassMic
parent: CommandManager
grandparent: API リファレンス
nav_order: 17
---

# CommandManager.openGlassMic

```kotlin
fun openGlassMic()
```

## 概要

グラスのマイクを開く。開いている間、音声は `GlassClient.addAudioDataEventListener` に届く。マイクのチャンネルは接続中のデバイスに合わせて SDK 側で決まる。

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
