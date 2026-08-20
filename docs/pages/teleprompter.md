---
title: テレプロンプター
parent: ページごとの使い方
nav_order: 1
---

# テレプロンプター

原稿をグラスに出して、読み上げに合わせて流す画面。

## 開く

`enterTeleprompterPage()` を呼ぶ。開く前に送った原稿は表示されないので、必ず先に開く。

## 原稿を出す

全文を渡すなら `sendTeleprompterContent()`。200バイトを超える分は SDK が分割して送る。
`percent` つきの overload はスクロールバーの位置も一緒に送る。

<!-- snippet: pages.teleprompter -->
```kotlin
commandManager.enterTeleprompterPage()
commandManager.sendTeleprompterStatus(
    status = CommandManager.TeleprompterStatus.READY,
    mode = CommandManager.TeleprompterMode.TELEPROMPT,
)
commandManager.sendTeleprompterContent("読み上げる原稿", percent = 0)
```
<!-- /snippet -->

`sendTeleprompterStatus()` は再生状態と表示モードをまとめて送る。別パケットに分けると
動作が安定しないため、片方だけ変えたいときも両方渡す。

| status | 意味 |
|---|---|
| `READY` | 再生前 |
| `STARTED` | 再生中 |
| `PAUSED` | 一時停止 |

| mode | 意味 |
|---|---|
| `TELEPROMPT` | 原稿の読み上げ |
| `TRANSCRIPT` | 文字起こし |
| `TRANSLATION` | 翻訳 |

## 読み上げに合わせて流す

文字起こしのように次の1行が決まっていく使い方では、全文を送り直さずに
`sendTeleprompterLine()` で1行ずつ追記する。`percent` でスクロールバーを進め、
`scrollUp = true` にすると1行上へ戻る。

経過時間は `sendTeleprompterTime()` に `mm:ss` の5文字で渡す。短ければ先頭が0埋めされ、
長ければ切り捨てられる。

<!-- snippet: pages.teleprompter-play -->
```kotlin
commandManager.sendTeleprompterStatus(
    status = CommandManager.TeleprompterStatus.STARTED,
    mode = CommandManager.TeleprompterMode.TELEPROMPT,
)
lines.forEachIndexed { index, line ->
    // 全文を送り直さず、読み上げた分だけ流す
    commandManager.sendTeleprompterLine(
        text = line,
        percent = (index + 1) * 100 / lines.size,
    )
    commandManager.sendTeleprompterTime("00:%02d".format(index))
}
commandManager.clearInscriptionText()
```
<!-- /snippet -->

## 消す・やめる

`clearInscriptionText()` で表示中のテキストが消える。翻訳ページとグラス側のバッファを
共有しているため、消去はどちらのページでも同じコマンドになる。

ページを離れるときは `enterHomePage()`。

## 関連 API

- `enterTeleprompterPage()`
- `sendTeleprompterContent(content: String, percent: Int)`
- `sendTeleprompterLine(text: String, percent: Int, scrollUp: Boolean)`
- `sendTeleprompterStatus(status: CommandManager.TeleprompterStatus, mode: CommandManager.TeleprompterMode)`
- `sendTeleprompterTime(time: String)`
- `clearInscriptionText()`
