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

グラスのマイク音声。PCM16 のリトルエンディアン、16kHz モノラル。startMicStreaming を呼ぶまで何も流れない。デバイスの世代で音声の形式が変わる（Ogg Opus か record stream）が、判別とデコードは SDK 側で行うため利用側は PCM だけ受け取ればよい。グラスの録音は小さいため、SDK が3倍に持ち上げてから流す。購読が遅れると古いデータから捨てるので、録音として貯めるなら受け取り側でバッファする。

{: .note }
> 1チャンクは 640 バイト（20ms 分）で届く。毎秒 32000 バイトに満たないときは
> BLE の取りこぼしを疑う。

## 音声認識に渡すとき

そのまま投げられる形ではないので、送り先に合わせて包み直す。

| 送り先 | 必要な変換 |
|---|---|
| ファイルを受け取る文字起こし API | WAV ヘッダ（44バイト）を付けてファイルにする。サンプルレートはヘッダに書けばよく、変換は要らない |
| 24kHz を要求するリアルタイム API | 16kHz から 24kHz へリサンプルしてから送る。そのまま送ると速度とピッチがずれる |

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
