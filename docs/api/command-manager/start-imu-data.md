---
title: startImuData
parent: CommandManager
grandparent: API リファレンス
nav_order: 67
---

# CommandManager.startImuData

```kotlin
fun startImuData()
```

## 概要

6DoF の送信を開始する。値は imuData に流れる。FEATURE_VERSION 2.0.0 以上のファームが対象で、それ未満では何も起きない。切断するとグラス側で止まるため、再接続後も続けるなら呼び直す。

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.startImuData -->
<!-- WIP -->
<!-- /snippet -->

## 関連

- [imuData](imu-data.html)
- [stopImuData](stop-imu-data.html)
