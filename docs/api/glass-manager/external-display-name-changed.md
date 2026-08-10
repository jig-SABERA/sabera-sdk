---
title: externalDisplayNameChanged
parent: GlassManager
grandparent: API リファレンス
nav_order: 6
---

# GlassManager.externalDisplayNameChanged

{: .warning }
> このページは執筆中です。

```kotlin
val externalDisplayNameChanged: SharedFlow<String?>
```

## 概要

<!-- WIP -->

## 戻り値

`SharedFlow<String?>`

## 使用例

<!-- snippet: GlassManager.externalDisplayNameChanged -->
```kotlin
scope.launch {
    manager.externalDisplayNameChanged.collect { name ->
        Log.d("sample", "display name: $name")
    }
}
```
<!-- /snippet -->
