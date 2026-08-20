---
title: enterHomePage
parent: CommandManager
grandparent: API リファレンス
nav_order: 7
---

# CommandManager.enterHomePage

```kotlin
fun enterHomePage()
```

## 概要

グラスをホーム画面に戻す。開いていたページは閉じ、表示していた内容は破棄される。機能を止めるときの後片付けに使う。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.enterHomePage -->
```kotlin
commandManager.enterHomePage()
```
<!-- /snippet -->
