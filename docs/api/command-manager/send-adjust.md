---
title: sendAdjust
parent: CommandManager
grandparent: API リファレンス
nav_order: 51
---

# CommandManager.sendAdjust

```kotlin
fun sendAdjust(
    status: CommandManager.AdjustStatus,
    imageType: CommandManager.AdjustImageType,
)
```

## 概要

画面位置調整用の画像の表示を制御する。

{: .note }
> 0.0.10 には含まれない。次のリリースから使える。

## 引数

| 名前 | 型 | 説明 |
|---|---|---|
| `status` | `CommandManager.AdjustStatus` | `SHOW` / `CLOSE` |
| `imageType` | `CommandManager.AdjustImageType` | `HOME` / `NAVIGATE` / `TELEPROMPT` |

## 戻り値

`Unit`

## 使用例

<!-- snippet: CommandManager.sendAdjust -->
<!-- WIP -->
<!-- /snippet -->
