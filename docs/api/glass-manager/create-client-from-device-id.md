---
title: createClientFromDeviceID
parent: GlassManager
grandparent: API リファレンス
nav_order: 11
---

# GlassManager.createClientFromDeviceID

{: .warning }
> このページは執筆中です。

```kotlin
fun createClientFromDeviceID(deviceId: String): GlassClient?
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `deviceId` | `String` | <!-- WIP --> |

## 戻り値

`GlassClient?`

## 使用例

<!-- snippet: GlassManager.createClientFromDeviceID -->
```kotlin
val client = manager.createClientFromDeviceID(deviceId)
if (client != null) {
    manager.connect(client)
}
```
<!-- /snippet -->

## 関連

- [connect](connect.html)
