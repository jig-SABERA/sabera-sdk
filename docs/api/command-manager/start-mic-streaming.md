---
title: startMicStreaming
parent: CommandManager
grandparent: API リファレンス
nav_order: 23
---

# CommandManager.startMicStreaming

```kotlin
fun startMicStreaming()
```

## 概要

マイクを開いて、届いた音声をデコードしながら micAudio に流す。すでに流れているときは開き直す。生の Opus を自分で扱いたい場合はopenGlassMic と GlassClient.addAudioDataEventListener を使う。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.startMicStreaming -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [micAudio](mic-audio.html)
- [stopMicStreaming](stop-mic-streaming.html)
