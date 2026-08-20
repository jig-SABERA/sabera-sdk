---
title: closeGlassMic
parent: CommandManager
grandparent: API リファレンス
nav_order: 22
---

# CommandManager.closeGlassMic

```kotlin
fun closeGlassMic()
```

## 概要

グラスのマイクを閉じる。openGlassMic と対で呼ぶ。閉じ忘れるとグラスは録音を続けるので、画面を離れるときに必ず呼ぶ。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.closeGlassMic -->
```kotlin
// 画面を離れるときに必ず閉じる。閉じ忘れるとグラスは録音を続ける
commandManager.closeGlassMic()
```
<!-- /snippet -->

## 関連

- [openGlassMic](open-glass-mic.html)
