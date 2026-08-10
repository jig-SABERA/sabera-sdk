---
title: GlassesSDK
parent: API リファレンス
nav_order: 1
has_children: true
---

# GlassesSDK

SDK 全体の初期設定を行うシングルトン。`Application.onCreate()` で呼ぶ。

| メソッド | シグネチャ |
|---|---|
| [setLogger](set-logger.html) | `fun setLogger(sink: (tag: String, message: String) -> Unit)` |
| [setProd](set-prod.html) | `fun setProd(isProd: Boolean)` |
| [setDevicePersistence](set-device-persistence.html) | `fun setDevicePersistence(persistence: SdkDevicePersistence)` |
