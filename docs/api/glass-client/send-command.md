---
title: sendCommand
parent: GlassClient
grandparent: API リファレンス
nav_order: 7
---

# GlassClient.sendCommand

{: .warning }
> このページは執筆中です。

```kotlin
suspend fun sendCommand(command: ByteArray)
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `command` | `ByteArray` | <!-- WIP --> |

## 戻り値

`Unit`

## 使用例

<!-- snippet: GlassClient.sendCommand -->
```kotlin
client.sendCommand(byteArrayOf(0x4E, 0x00))
```
<!-- /snippet -->

## 関連

- [sendCommandList](send-command-list.html)
