---
title: selectionDialogPresented
parent: GlassManager
grandparent: API リファレンス
nav_order: 5
---

# GlassManager.selectionDialogPresented

{: .warning }
> このページは執筆中です。

```kotlin
val selectionDialogPresented: SharedFlow<Unit>
```

## 概要

<!-- WIP -->

## 戻り値

`SharedFlow<Unit>`

## 使用例

<!-- snippet: GlassManager.selectionDialogPresented -->
```kotlin
scope.launch {
    manager.selectionDialogPresented.collect {
        // OS のデバイス選択ダイアログが表示された
    }
}
```
<!-- /snippet -->
