---
title: addRemoteControllerEventListener
parent: CommandManager
grandparent: API リファレンス
nav_order: 30
---

# CommandManager.addRemoteControllerEventListener

{: .warning }
> このページは執筆中です。

```kotlin
fun addRemoteControllerEventListener(listener: CommandManager.RemoteControlListener)
```

## 概要

<!-- WIP -->

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `listener` | `CommandManager.RemoteControlListener` | <!-- WIP --> |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.addRemoteControllerEventListener -->
```kotlin
val listener = object : CommandManager.RemoteControlListener {
    override fun onPrev() {
        Log.d("sample", "prev")
    }

    override fun onNext() {
        Log.d("sample", "next")
    }

    override fun onEsc() {
        Log.d("sample", "esc")
    }
}
commandManager.addRemoteControllerEventListener(listener)
```
<!-- /snippet -->

## 関連

- [removeRemoteControllerEventListener](remove-remote-controller-event-listener.html)
