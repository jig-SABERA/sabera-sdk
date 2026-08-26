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

グラスが充電中なら true。状態がまだ届いていないうちは null。requestSystemStatus を呼ぶと応答で埋まり、以降はグラス側の変化通知で更新される。切断すると null に戻るので、再接続後は取得前として扱える。

## 戻り値

`StateFlow<Boolean?>`

## 使用例

<!-- snippet: CommandManager.charging -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [requestSystemStatus](request-system-status.html)
