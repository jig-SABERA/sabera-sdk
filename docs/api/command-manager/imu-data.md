---
title: imuData
parent: CommandManager
grandparent: API リファレンス
nav_order: 3
---

# CommandManager.imuData

```kotlin
val imuData: SharedFlow<CommandManager.ImuData>
```

## 概要

6DoF のセンサー値。startImuData を呼ぶまで何も流れない。1サンプルは加速度[mg]・角速度[dps]・ピッチとヨー[度]と、AR起動からの経過時間[ms]を持つ。並べ替えや間隔の計算は受信時刻ではなくこの経過時間を使う。送信キューが詰まるとグラス側がサンプルを捨てるため、指定した周期どおりには届かない。

## 戻り値

`SharedFlow<CommandManager.ImuData>`

## 使用例

<!-- snippet: CommandManager.imuData -->
```kotlin
scope.launch {
    commandManager.imuData.collect { data ->
        // 並べ替えや間隔の計算は受信時刻ではなく timestampMs を使う
        Log.d("sample", "${data.timestampMs}ms pitch=${data.pitchDegrees}")
    }
}
commandManager.startImuData()
```
<!-- /snippet -->

## 関連

- [startImuData](start-imu-data.html)
- [stopImuData](stop-imu-data.html)
