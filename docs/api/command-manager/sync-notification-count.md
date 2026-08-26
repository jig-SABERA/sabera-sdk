---
title: syncNotificationCount
parent: CommandManager
grandparent: API リファレンス
nav_order: 24
---

# CommandManager.syncNotificationCount

```kotlin
fun syncNotificationCount(count: Int)
```

## 概要

未読通知の件数をグラスに知らせる。ホームの通知バッジに反映される。未接続だと捨てられるので connected を見てから送る。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `count` | `Int` | 未読の件数 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.syncNotificationCount -->
```kotlin
// 未接続だと捨てられるので、つながっているときだけ送る
if (commandManager.connected.value) {
    commandManager.syncNotificationCount(unreadCount)
}
```
<!-- /snippet -->

## 関連

- [sendMessage](send-message.html)
- [connected](connected.html)
