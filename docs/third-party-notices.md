---
title: サードパーティ表記
nav_order: 8
---

# サードパーティ表記

SDK の配布物に含まれる第三者のソフトウェアと、アプリ側に及ぶ表記義務。

## Opus (libopus)

| 項目 | 内容 |
|---|---|
| バージョン | 1.4 |
| ライセンス | BSD 3-Clause |
| 含まれるバージョン | SDK 0.6.0 以降の Android 版（`sabera-app-core-android` の aar） |
| 実体 | aar 内の `jni/{arm64-v8a,armeabi-v7a}/libopus.so` と、JNI ラッパーの `libopusdecoder.so` |

グラスのマイク音声は Opus で流れてくるため、`startMicStreaming()` で受け取る PCM への
デコードに libopus を使っている。マイクを使わないアプリでも、aar に入っているので
再配布物には含まれる。

### アプリ側でやること

BSD 3-Clause はバイナリでの再配布に対して、**著作権表示・条件文・免責を配布物に付随する
文書等で再現すること**を求める。ソースの公開は求めない。

SDK を組み込んだアプリを配布するときは、下の全文をアプリのライセンス画面や
それに準じる場所に載せる。Android の場合、`libopus.so` は Gradle の依存ではなく aar 同梱の
バイナリなので、**AboutLibraries のような依存から一覧を作るツールでは拾われない**。
手で足す必要がある。

SDK 0.6.1 以降は aar の `META-INF/third-party-notices/opus-LICENSE.txt` にも同じ全文が入って
いるので、そこから読み出してもよい。

特許はロイヤリティフリーで許諾されている（[opus-codec.org/license](https://opus-codec.org/license/)）。

### 全文

```
Copyright 2001-2023 Xiph.Org, Skype Limited, Octasic,
                    Jean-Marc Valin, Timothy B. Terriberry,
                    CSIRO, Gregory Maxwell, Mark Borgerding,
                    Erik de Castro Lopo, Mozilla, Amazon

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

- Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

- Neither the name of Internet Society, IETF or IETF Trust, nor the
names of specific contributors, may be used to endorse or promote
products derived from this software without specific prior written
permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Opus is subject to the royalty-free patent licenses which are
specified at:

Xiph.Org Foundation:
https://datatracker.ietf.org/ipr/1524/

Microsoft Corporation:
https://datatracker.ietf.org/ipr/1914/

Broadcom Corporation:
https://datatracker.ietf.org/ipr/1526/
```
