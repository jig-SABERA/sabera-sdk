---
title: closeCanvas
parent: CommandManager
grandparent: API リファレンス
nav_order: 35
---

# CommandManager.closeCanvas

```kotlin
fun closeCanvas()
```

## 概要

自由配置キャンバスを閉じてホームなどに戻す。表示していた要素は破棄される。リモコンの戻る操作やホームへの遷移でも閉じる。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.closeCanvas -->
```kotlin
commandManager.closeCanvas()
```
<!-- /snippet -->

## 関連

- [sendCanvas](send-canvas.html)
