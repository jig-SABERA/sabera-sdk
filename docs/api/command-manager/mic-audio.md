---
title: micAudio
parent: CommandManager
grandparent: API リファレンス
nav_order: 5
---

# CommandManager.micAudio

```kotlin
val micAudio: SharedFlow<ByteArray>
```

## 概要

グラスのマイク音声。PCM16 のリトルエンディアン、16kHz モノラル。startMicStreaming を呼ぶまで何も流れない。デバイスの世代で音声の形式が変わる（Ogg Opus か record stream）が、判別とデコードは SDK 側で行うため利用側は PCM だけ受け取ればよい。購読が遅れると古いデータから捨てるので、録音として貯めるなら受け取り側でバッファする。

## 戻り値

`SharedFlow<ByteArray>`

## 使用例

<!-- snippet: CommandManager.micAudio -->
```kotlin
scope.launch {
    commandManager.micAudio.collect { pcm ->
        // PCM16 リトルエンディアン、16kHz モノラル
        Log.d("sample", "${pcm.size} bytes")
    }
}
commandManager.startMicStreaming()
```
<!-- /snippet -->

## 関連

- [startMicStreaming](start-mic-streaming.html)
- [stopMicStreaming](stop-mic-streaming.html)
- [micStreaming](mic-streaming.html)
