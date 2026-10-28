---
title: isConnecting
parent: GlassManager
grandparent: API リファレンス
nav_order: 16
---

# GlassManager.isConnecting

```kotlin
val isConnecting: StateFlow<Boolean>
```

## 概要

接続を試している間 true、試行が終わると false。接続済みかどうかは connectedDevice で確認する。Android では接続処理と有限回の再接続ループを含み、ループ終了後の端末検出待ちは含まない。iOS では接続処理とリンク切断後の OS による再接続待ちを含む。

{: .note }
> SDK 1.1.2 以降。

## 戻り値

`StateFlow<Boolean>`

## 使用例

<!-- snippet: GlassManager.isConnecting -->
```kotlin
scope.launch {
    manager.isConnecting.collect { connecting ->
        Log.d("sample", "connecting: $connecting")
    }
}
```
<!-- /snippet -->

## 関連

- [connectedDevice](connected-device.md)
