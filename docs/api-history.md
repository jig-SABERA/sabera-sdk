---
title: 更新履歴
nav_order: 6
---

# 更新履歴

Android / iOS 向け SDK の更新内容。

## 1.1.2

配布先を [sdk.sabera.glass/maven](https://sdk.sabera.glass/maven) に変更。
Android / iOS とも認証なしで取得できる。
Android は Maven リポジトリの URL と依存バージョンを変更する。
iOS は Swift Package のリポジトリ URL を変えず、バージョンを `1.1.2` に上げる。
設定例は [Getting Started](getting-started.md) を参照。

| API | 補足 |
|---|---|
| [isConnecting](api/glass-manager/is-connecting.md) | 接続試行中かどうかを `StateFlow<Boolean>` で購読できる |
| [showAutomaticSelectionDialog](api/glass-manager/show-automatic-selection-dialog.md) | iOS の接続開始前に確認処理を挟む `beforeConnect` 引数付きの呼び出しを追加。従来の引数1つの呼び出しも使える |

- iOS のアクセサリ選択中にアプリが前面へ戻っても、接続前の確認を飛ばして接続しないよう修正。
- iOS で再接続した直後に、古い接続監視の終了通知によって接続試行中の表示が消える問題を修正。
