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

マイクを開いて、届いた音声をデコードしながら micAudio に流す。すでに流れているときは開き直す。生の Opus を自分で扱いたい場合は openGlassMic と GlassClient.addAudioDataEventListener を使う。

止めるまでグラスのマイクは開いたままになる。使い終わったら stopMicStreaming を呼ぶ。

{: .warning }
> iOS はデコーダの差し込みが要る。差し込まれていない状態で呼ぶと例外になる。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.startMicStreaming -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [micAudio](mic-audio.html)
- [stopMicStreaming](stop-mic-streaming.html)
