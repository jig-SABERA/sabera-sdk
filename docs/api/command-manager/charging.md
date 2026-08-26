---
title: charging
parent: CommandManager
grandparent: API リファレンス
nav_order: 7
---

# CommandManager.charging

```kotlin
val charging: StateFlow<Boolean?>
```

## 概要

グラスが充電中なら true。状態がまだ届いていないうちは null。接続すると SDK が一度状態を要求するので、購読するだけでよい。以降はグラス側の変化通知で更新される。切断すると null に戻る。

## 戻り値

`StateFlow<Boolean?>`

## 使用例

<!-- snippet: CommandManager.charging -->
```kotlin
scope.launch {
    // 接続するとSDKが状態を要求するので、購読するだけでよい。届く前は null
    commandManager.charging.collect { charging ->
        Log.d("sample", if (charging == true) "充電中" else "充電していない")
    }
}
```
<!-- /snippet -->

## 関連

- [requestSystemStatus](request-system-status.html)
