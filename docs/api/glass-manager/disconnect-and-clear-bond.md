---
title: disconnectAndClearBond
parent: GlassManager
grandparent: API リファレンス
nav_order: 10
---

# GlassManager.disconnectAndClearBond

{: .warning }
> このページは執筆中です。

```kotlin
suspend fun disconnectAndClearBond(glassClient: GlassClient)
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `glassClient` | `GlassClient` | <!-- WIP --> |

## 戻り値

`Unit`

## 使用例

<!-- snippet: GlassManager.disconnectAndClearBond -->
```kotlin
// ペアリング情報も消すため、次回はデバイス選択からやり直しになる
manager.disconnectAndClearBond(client)
```
<!-- /snippet -->

## 関連

- [disconnect](disconnect.html)
