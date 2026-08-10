---
title: gestureEvents
parent: CommandManager
grandparent: API リファレンス
nav_order: 2
---

# CommandManager.gestureEvents

{: .warning }
> このページは執筆中です。

```kotlin
val gestureEvents: SharedFlow<GestureType>
```

## 概要

<!-- WIP -->

## 戻り値

`SharedFlow<GestureType>`

## 使用例

<!-- snippet: CommandManager.gestureEvents -->
```kotlin
scope.launch {
    commandManager.gestureEvents.collect { gesture ->
        when (gesture) {
            GestureType.SINGLE_TAP -> Log.d("sample", "tap")
            GestureType.DOUBLE_TAP -> Log.d("sample", "double tap")
            GestureType.HOLD -> Log.d("sample", "hold")
        }
    }
}
```
<!-- /snippet -->
