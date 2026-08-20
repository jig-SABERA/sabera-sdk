---
title: parseResponse
parent: CommandManager
grandparent: API リファレンス
nav_order: 32
---

# CommandManager.parseResponse

```kotlin
fun parseResponse(value: ByteArray)
```

## 概要

グラスから届いたパケットを解析する。requestSystemStatus や requestSettingSync の応答を自前で受けるときに使う。ジェスチャーや6DoFのように専用の Flow があるものは、そちらを購読すればよい。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `value` | `ByteArray` | グラスから届いたパケット |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.parseResponse -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [requestSystemStatus](request-system-status.html)
- [requestSettingSync](request-setting-sync.html)
