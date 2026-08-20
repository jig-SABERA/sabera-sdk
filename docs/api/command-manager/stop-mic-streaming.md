---
title: stopMicStreaming
parent: CommandManager
grandparent: API リファレンス
nav_order: 21
---

# CommandManager.stopMicStreaming

```kotlin
fun stopMicStreaming()
```

## 概要

マイクを閉じて micAudio を止める。デコーダも解放する。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.stopMicStreaming -->
```kotlin
commandManager.stopMicStreaming()
```
<!-- /snippet -->

## 関連

- [micAudio](mic-audio.html)
- [startMicStreaming](start-mic-streaming.html)
