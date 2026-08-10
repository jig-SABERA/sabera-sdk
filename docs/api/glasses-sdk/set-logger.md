---
title: setLogger
parent: GlassesSDK
grandparent: API リファレンス
nav_order: 1
---

# GlassesSDK.setLogger

{: .warning }
> このページは執筆中です。

```kotlin
fun setLogger(sink: (tag: String, message: String) -> Unit)
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `sink` | `(String, String) -> Unit` | <!-- WIP --> |

## 戻り値

`Unit`

## 使用例

<!-- snippet: GlassesSDK.setLogger -->
```kotlin
GlassesSDK.setLogger { tag, message -> Log.d(tag, message) }
```
<!-- /snippet -->
