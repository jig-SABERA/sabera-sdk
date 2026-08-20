---
title: connected
parent: CommandManager
grandparent: API リファレンス
nav_order: 1
---

# CommandManager.connected

```kotlin
val connected: StateFlow<Boolean>
```

## 概要

グラスとつながっている間 true。GlassClient.connected と同じ状態を返す。送信メソッドは未接続だと黙って捨てられるので、通知の転送のように取りこぼしたくないものはこれを見てから送る。

## 戻り値

`StateFlow<Boolean>`

## 使用例

<!-- snippet: CommandManager.connected -->
```kotlin
scope.launch {
    commandManager.connected.collect { connected ->
        Log.d("sample", if (connected) "つながった" else "切れた")
    }
}
```
<!-- /snippet -->
