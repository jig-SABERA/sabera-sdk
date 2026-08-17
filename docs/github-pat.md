---
title: GitHub PAT の作り方
nav_order: 3
---

# GitHub PAT の作り方

SDK は private な GitHub Packages で配布しているため、取得には Personal Access Token
(PAT) が必要になる。ここではその発行手順をまとめる。

## 1. トークンを発行する

GitHub にログインした状態で
[Settings > Developer settings > Personal access tokens > Tokens (classic)](https://github.com/settings/tokens)
を開き、**Generate new token (classic)** を選ぶ。

入力する項目は次の3つ。

| 項目 | 値 |
| --- | --- |
| Note | 用途がわかる名前（例: `sabera-sdk packages`） |
| Expiration | 任意。期限切れ後は再発行が必要 |
| Scopes | **`read:packages` のみ**にチェック |

`repo` などほかのスコープは SDK の取得には不要。付けるとトークンが漏れたときの影響が
大きくなるので、`read:packages` だけにする。

**Generate token** を押すと画面にトークンが表示される。この画面を離れると二度と
表示されないので、その場でコピーしておく。

## 2. SSO を認可する（表示されている場合のみ）

トークン一覧に **Configure SSO** ボタンが出ている場合、Organization が SAML SSO を
使っている。発行しただけではパッケージを取得できないので、ボタンを押して `jig-SABERA` を
**Authorize** する。これを忘れると `401 Unauthorized` になる。

## 3. Gradle に設定する

`~/.gradle/gradle.properties` に書く。プロジェクト直下ではなくホーム配下に置くのは、
リポジトリにコミットしてしまう事故を防ぐため。

```properties
gpr.user=<GitHubのユーザー名>
gpr.token=<発行した PAT>
```

環境変数でも代用できる。CI ではこちらを使う。

```bash
export GITHUB_ACTOR=<GitHubのユーザー名>
export GITHUB_TOKEN=<発行した PAT>
```

## 4. 確認する

サンプルの Android プロジェクトをビルドし、SDK の依存が解決できれば設定は完了。

## うまくいかないとき

- **401 Unauthorized** — SSO の認可漏れ、またはトークンの期限切れ。手順 2 を確認する
- **403 Forbidden** — スコープに `read:packages` が入っていない
- **依存が見つからない** — `gradle.properties` の場所が違う。
  `~/.gradle/gradle.properties` に置く
- **認証情報が空のまま** — `gpr.user` は GitHub のユーザー名。メールアドレスではない

## Fine-grained token について

新しい Fine-grained personal access token でもパッケージの読み取りはできるが、
Organization 側で許可設定が必要になる。特に理由がなければ classic を使う。

## 取り扱い

PAT はパスワードと同じ扱いにする。リポジトリにコミットしない、Slack や
Issue に貼らない。漏れた場合はトークン一覧から **Delete** して再発行する。
