---
title: connected
parent: GlassClient
grandparent: API リファレンス
nav_order: 1
---

# GlassClient.connected

{: .warning }
> このページは執筆中です。

```kotlin
val connected: StateFlow<Boolean>
```

## 概要

<!-- WIP -->

## 戻り値

`StateFlow<Boolean>`

## 使用例

<!-- snippet: GlassClient.connected -->
```kotlin
scope.launch {
    client.connected.collect { connected ->
        Log.d("sample", "connected: $connected")
    }
}
```
<!-- /snippet -->
