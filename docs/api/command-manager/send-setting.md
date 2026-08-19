---
title: sendSetting
parent: CommandManager
grandparent: API リファレンス
nav_order: 56
---

# CommandManager.sendSetting

```kotlin
fun sendSetting(name: String, value: Int)
fun sendSetting(name: String, value: Boolean)
fun sendSetting(name: String, value: String)
fun sendSetting(name: String, value: ByteArray)
```

## 概要

グラスの設定値を書き換える。値の型ごとにグラスへ送る型が変わるため、文字列とバイト列は別の overload になっている。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `name` | `String` | 設定キー。`CommandManager.SettingKey` の定数を使う |
| `value` | `Int / Boolean / String / ByteArray` | 設定値 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendSetting -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [requestSettingSync](request-setting-sync.html)
