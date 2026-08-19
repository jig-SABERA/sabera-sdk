---
title: closeLayout
parent: CommandManager
grandparent: API リファレンス
nav_order: 33
---

# CommandManager.closeLayout

```kotlin
fun closeLayout()
```

## 概要

分割レイアウトを閉じてホームなどに戻す。表示していたテキストは破棄される。リモコンの戻る操作やホームへの遷移でも閉じる。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.closeLayout -->
```kotlin
commandManager.closeLayout()
```
<!-- /snippet -->

## 関連

- [sendLayout](send-layout.html)
