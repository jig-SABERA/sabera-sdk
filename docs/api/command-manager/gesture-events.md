---
title: gestureEvents
parent: CommandManager
grandparent: API リファレンス
nav_order: 2
---

# CommandManager.gestureEvents

```kotlin
val gestureEvents: SharedFlow<GestureType>
```

## 概要

グラスのタッチ操作が流れる。シングルタップ・ダブルタップ・長押しの3種。どのページを開いていても届くので、アプリが背面にいる間の操作も拾える。

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
