---
title: lastDeviceId
parent: SdkDevicePersistence
grandparent: API リファレンス
nav_order: 1
---

# SdkDevicePersistence.lastDeviceId

{: .warning }
> このページは執筆中です。

```kotlin
var lastDeviceId: String?
```

## 概要

<!-- WIP -->

## 戻り値

`String?`

## 使用例

<!-- snippet: SdkDevicePersistence.lastDeviceId -->
```kotlin
override var lastDeviceId: String?
    get() = prefs.getString("last_device_id", null)
    set(value) {
        prefs.edit().putString("last_device_id", value).apply()
    }
```
<!-- /snippet -->
