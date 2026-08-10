---
title: lastConnectedDevice
parent: GlassManager
grandparent: API リファレンス
nav_order: 3
---

# GlassManager.lastConnectedDevice

{: .warning }
> このページは執筆中です。

```kotlin
val lastConnectedDevice: GlassClient?
```

## 概要

<!-- WIP -->

## 戻り値

`GlassClient?`

## 使用例

<!-- snippet: GlassManager.lastConnectedDevice -->
```kotlin
val last = manager.lastConnectedDevice
if (last != null) {
    scope.launch { manager.connect(last) }
}
```
<!-- /snippet -->
