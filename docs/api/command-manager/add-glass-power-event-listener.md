---
title: addGlassPowerEventListener
parent: CommandManager
grandparent: API リファレンス
nav_order: 28
---

# CommandManager.addGlassPowerEventListener

{: .warning }
> このページは執筆中です。

```kotlin
fun addGlassPowerEventListener(listener: () -> Unit)
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `listener` | `() -> Unit` | <!-- WIP --> |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.addGlassPowerEventListener -->
```kotlin
val listener: () -> Unit = { Log.d("sample", "power button") }
commandManager.addGlassPowerEventListener(listener)
```
<!-- /snippet -->

## 関連

- [removeGlassPowerEventListener](remove-glass-power-event-listener.html)
