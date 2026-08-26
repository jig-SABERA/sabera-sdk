---
title: sendDebugPhoneName
parent: CommandManager
grandparent: API リファレンス
nav_order: 25
---

# CommandManager.sendDebugPhoneName

```kotlin
fun sendDebugPhoneName(phoneName: String)
```

## 概要

開発用。接続元のスマホ名をグラスに送り、グラス側のデバッグ表示でどの端末とつながっているか分かるようにする。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `phoneName` | `String` | スマホ本体の Bluetooth 名 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendDebugPhoneName -->
```kotlin
commandManager.sendDebugPhoneName(phoneName)
```
<!-- /snippet -->
