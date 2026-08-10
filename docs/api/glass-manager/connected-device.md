---
title: connectedDevice
parent: GlassManager
grandparent: API リファレンス
nav_order: 2
---

# GlassManager.connectedDevice

{: .warning }
> このページは執筆中です。

```kotlin
val connectedDevice: StateFlow<GlassClient?>
```

## 概要

<!-- WIP -->

## 戻り値

`StateFlow<GlassClient?>`

## 使用例

<!-- snippet: GlassManager.connectedDevice -->
```kotlin
scope.launch {
    manager.connectedDevice.collect { client ->
        if (client != null) {
            // 接続済み。ここで CommandManager を作る
        } else {
            // 未接続
        }
    }
}
```
<!-- /snippet -->
