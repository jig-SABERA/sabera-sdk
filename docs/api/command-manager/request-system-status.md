---
title: requestSystemStatus
parent: CommandManager
grandparent: API リファレンス
nav_order: 67
---

# CommandManager.requestSystemStatus

```kotlin
fun requestSystemStatus()
```

## 概要

バッテリー残量・装着状態・充電状態の通知をグラスに要求する。残量は batteryLevel、充電状態は charging に反映される。装着状態は parseResponse で受ける。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.requestSystemStatus -->
```kotlin
commandManager.requestSystemStatus()
```
<!-- /snippet -->

## 関連

- [batteryLevel](battery-level.md)
- [charging](charging.md)
- [parseResponse](parse-response.md)
