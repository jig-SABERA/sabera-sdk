---
title: batteryLevel
parent: CommandManager
grandparent: API リファレンス
nav_order: 68
---

# CommandManager.batteryLevel

```kotlin
val batteryLevel: StateFlow<Int?>
```

## 概要

グラスのバッテリー残量を 0〜100 のパーセントで通知する。未取得・切断時や範囲外の値を受信した場合は null。接続すると SDK が一度状態を要求するので、購読するだけでよい。再取得する場合は requestSystemStatus を呼ぶ。

## 戻り値

`StateFlow<Int?>`

## 使用例

<!-- snippet: CommandManager.batteryLevel -->
```kotlin
scope.launch {
    commandManager.batteryLevel.collect { level ->
        Log.d("sample", level?.let { "$it%" } ?: "未取得")
    }
}
```
<!-- /snippet -->

## 関連

- [requestSystemStatus](request-system-status.md)
- [charging](charging.md)
