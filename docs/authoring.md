---
title: ドキュメントの作り方
nav_order: 6
---

# ドキュメントの作り方

このサイトの構成と、ページを増やすときの手順。

## 置き場所

| パス | 中身 |
|---|---|
| `docs/_config.yml` | Jekyll の設定 |
| `docs/Gemfile` | Jekyll とテーマ（just-the-docs）の固定 |
| `docs/index.md` / `getting-started.md` | トップと入門 |
| `docs/api/**` | メソッドごとのページ。`scripts/gen-api-docs.py` が雛形を作る |
| `docs/_sass/color_schemes/` | 配色。`sabera.scss` がライト、`sabera-dark.scss` がダーク |
| `docs/_sass/custom/custom.scss` | 前後リンクと配色切り替えボタンの見た目 |
| `docs/_includes/` | head / ヘッダ / フッタへの差し込み |
| `scripts/gen-api-docs.py` | API ページの雛形を書き出す |
| `scripts/sync-snippets.py` | コード例を Kotlin から `docs/api/**` へ写す |
| `samples/kmp/snippets/` | コード例の出処。コンパイルと ktlint を通る |

公開は GitHub Pages。`main` への push で Actions がビルドしてデプロイする（[デプロイ](#デプロイ)）。

## 本文を書く

`docs/api/**` の Markdown を直接編集する。`<!-- WIP -->` を消して書けばよい。

雛形には次の3つのプレースホルダがある。

- `{: .warning } > このページは執筆中です。` — 書き終えたら消す
- `<!-- WIP -->` — 概要・引数の説明・使用例
- `<!-- snippet: ... -->` — コード例の貼り先（後述）

## API へのリンク

公開 API の名前をバッククォートで囲むだけでよい。ビルド時に該当ページへのリンクへ
変わるので、`[...](...)` は書かない。

```markdown
`connectToLastDevice()` は前回接続したデバイスに接続する。
`GlassManager` から `connectedDevice` を購読する。
```

`foo` / `foo()` / `Type.foo(bar: Baz)` のいずれの書き方でも引ける。ただし `connected`
のように複数の型が持つ名前は、どちらを指すか決められないため `GlassClient.connected`
と型名から書く。

コードブロックの中と、既にリンクになっている箇所は変換しない。

仕組みは `_plugins/api_autolink.rb`。対応表の `_data/api_links.yml` は
`scripts/gen-api-docs.py` が `SPEC` から作るので、手で編集しない。

## メソッドを増やす

SDK に公開メソッドが増えたら `scripts/gen-api-docs.py` の `SPEC` に足して実行する。

```console
python3 scripts/gen-api-docs.py
```

既存ファイルは上書きしないので、書いた本文は残る。雛形そのものを変えたときだけ `--force`
を使うが、本文を書いた後に使うと消える。書き直したいページだけ消してから実行するのが安全。

`SPEC` の `m()` に `summary` を書くと、そのページは概要つきで生成され、執筆中の警告が出ない。
引数の説明は `("名前", "型", "説明")` の3要素で書く。`note` は本文の下に出る注意書き。

シグネチャは AAR を読んで確定させる。

```console
# 0.0.12 の AAR を GitHub Packages から取る
curl -sL -u "<user>:<PAT>" -o core.aar \
  https://maven.pkg.github.com/jig-SABERA/sabera-sdk-packages/jp/jig/sabera/app/sdk/sabera-app-core-android/0.0.12/sabera-app-core-android-0.0.12.aar
unzip -o core.aar -d aar && unzip -o aar/classes.jar -d cls
javap -public cls/app/jigglass/glass/CommandManager.class
```

## コード例

コード例は Markdown に直接書かない。出処は `samples/kmp/snippets` の Kotlin ソースで、
そこは **コンパイルと ktlint が通る**。SDK の API が変われば CI が落ちる。

Kotlin 側にマーカーを置く。

```kotlin
fun teleprompter(commandManager: CommandManager) {
    // #snippet CommandManager.sendTeleprompterContent
    commandManager.enterTeleprompterPage()
    commandManager.sendTeleprompterContent("読み上げる原稿")
    // #endsnippet
}
```

Markdown 側の貼り先は雛形が用意している。

```markdown
<!-- snippet: CommandManager.sendTeleprompterContent -->
<!-- /snippet -->
```

写す。

```console
python3 scripts/sync-snippets.py
```

マーカー間が ` ```kotlin ` ブロックに置き換わる。id が一致するスニペットが無いページは
そのまま残る。1つのスニペットを複数ページに貼りたいときは、貼り先の id を揃える。

Jekyll の `include` を使わずページに直接書き込んでいるのは、GitHub 上で `.md` を読んだときにも
コード例が見えるようにするため。

## 引数名の検証

`javap` はバイトコードから引数名を出せない。そこで
`samples/kmp/snippets/.../ArgumentNames.kt` で全メソッドを**名前付き引数**で1回だけ呼んでいる。

```kotlin
commandManager.sendMessage(name = "app", title = "title", time = 0L, text = "text")
```

ドキュメントの引数名が実際の宣言とずれるとコンパイルが落ちる。このファイルはドキュメントには
写らない（`#snippet` マーカーを置いていない）。

## ローカルで見る

```console
cd docs
bundle install
bundle exec jekyll serve
```

<http://127.0.0.1:4000/> が開く。ファイルを保存すると自動で再ビルドされるが、`_config.yml` を
変えたときは再起動が必要。

SCSS の deprecation 警告（`@import` と `darken()`）が大量に出る。テーマ側の書き方が dart-sass の
新しい記法に追いついていないためで、ビルドは通る。

## デプロイ

`main` への push で Actions（`.github/workflows/docs.yml` の `deploy`）がビルドして
GitHub Pages へ出す。ブランチを公開ソースにする方式は使っていないので、`_site` をコミットする
必要はない。

`baseurl` は `actions/configure-pages` が返す値をビルド時に渡している。リポジトリを引っ越しても
名前から決まるため、設定を直す必要はない。

デプロイの様子は Actions の `deploy` ジョブと、リポジトリの Environments（`github-pages`）で見られる。

### 初回だけ必要な設定

Settings → Pages の **Source** を `GitHub Actions` にする。`Deploy from a branch` のままだと
このワークフローの成果物は公開されない。

private リポジトリで Pages を公開するには GitHub Team 以上のプランが必要。

## コード例を自分で確かめる

```console
cd samples/kmp
GITHUB_ACTOR=<user> GITHUB_TOKEN=<PAT> \
  ./gradlew :snippets:compileDebugKotlin :snippets:ktlintCheck
cd - && python3 scripts/sync-snippets.py --check
```

CI（`.github/workflows/docs.yml`）が同じ3つを回す。`--check` は写した結果と `docs/` が
食い違っていれば失敗する。つまり Kotlin を直して `sync-snippets.py` を忘れると落ちる。

## 配色

`docs/_sass/color_schemes/` の2ファイルに、アプリと同じトークンの値を直接書いている。出処は
Figma の `Color_App`（Light / Dark）と `Color_SABERA_Brand`。`mix()` や `lighten()` は使わず、
どのトークン由来かを行コメントに残す。

ダークは既定で OS 設定に追従し、ヘッダのボタンで明示指定できる（選択は `localStorage`）。
just-the-docs は `color_scheme` をビルド時に1つしか選べないため、ダーク用の CSS を別に出力して
`media="(prefers-color-scheme: dark)"` で切り替えている。

## 前後リンク

ページ末尾の Prev / Next は `docs/_includes/footer_custom.html`。並び順は
`docs/_includes/nav_flat.html` がサイドバーと同じ規則（`parent` / `grandparent` と `nav_order`）で
組み立てる。Liquid に再帰がないため階層は3段までを前提にしている。
