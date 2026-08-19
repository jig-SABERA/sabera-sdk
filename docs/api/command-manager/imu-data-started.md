---
title: imuDataStarted
parent: CommandManager
grandparent: API リファレンス
nav_order: 4
---

# CommandManager.imuDataStarted

```kotlin
val imuDataStarted: StateFlow<Boolean>
```

## 概要

6DoF が流れている間 true。開始・停止の応答で切り替わる。

## 戻り値

`StateFlow<Boolean>`

## 使用例

<!-- snippet: CommandManager.imuDataStarted -->
```kotlin
scope.launch {
    commandManager.imuDataStarted.collect { started ->
        Log.d("sample", if (started) "6DoF 受信中" else "6DoF 停止中")
    }
}
```
<!-- /snippet -->

## 関連

- [startImuData](start-imu-data.html)
