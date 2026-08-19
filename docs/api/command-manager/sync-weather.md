---
title: syncWeather
parent: CommandManager
grandparent: API リファレンス
nav_order: 72
---

# CommandManager.syncWeather

```kotlin
fun syncWeather(type: CommandManager.WeatherType, value: Int)
```

## 概要

天気情報をグラスに同期する。気温とアイコンは別々に送る。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `type` | `CommandManager.WeatherType` | `TEMPERATURE` か `ICON` |
| `value` | `Int` | 気温、または天気アイコンの種別 |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.syncWeather -->
<!-- WIP -->
<!-- /snippet -->
