#!/usr/bin/env python3
"""コンパイル済みのコード例を docs/ のページへ写す。

コード例の出処は samples/kmp/snippets の Kotlin ソース。コンパイルと ktlint を通るため、
SDK の API が変わればビルドが落ちて気づける。docs/ 側は写した結果を持つだけ。

    // #snippet CommandManager.sendTeleprompterContent   ← Kotlin 側
    commandManager.enterTeleprompterPage()
    // #endsnippet

    <!-- snippet: CommandManager.sendTeleprompterContent -->   ← Markdown 側
    <!-- /snippet -->

--check は書き込まずに、写した結果と現状が食い違っていれば終了コード 1 を返す（CI 用）。
"""

import argparse
import re
import sys
import textwrap
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SNIPPET_SRC = ROOT / "samples/kmp/snippets/src/main/kotlin"
DOCS_DIRS = (ROOT / "docs/api", ROOT / "docs/pages")

SNIPPET_RE = re.compile(
    r"^[ \t]*// #snippet[ \t]+(?P<id>\S+)[ \t]*\n(?P<body>.*?)^[ \t]*// #endsnippet[ \t]*$",
    re.MULTILINE | re.DOTALL,
)
MARKER_RE = re.compile(
    r"(?P<open><!-- snippet: (?P<id>[^>]+?) -->\n)(?P<body>.*?)(?P<close><!-- /snippet -->)",
    re.DOTALL,
)


def collect_snippets():
    snippets = {}
    for path in sorted(SNIPPET_SRC.rglob("*.kt")):
        text = path.read_text(encoding="utf-8")
        for match in SNIPPET_RE.finditer(text):
            key = match.group("id")
            if key in snippets:
                sys.exit(f"スニペット id が重複している: {key}")
            snippets[key] = textwrap.dedent(match.group("body")).strip("\n")
    return snippets


def rewrite(text, snippets, used):
    def replace(match):
        key = match.group("id").strip()
        code = snippets.get(key)
        if code is None:
            return match.group(0)
        used.add(key)
        return f"{match.group('open')}```kotlin\n{code}\n```\n{match.group('close')}"

    return MARKER_RE.sub(replace, text)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true", help="書き込まず、差分があれば失敗する")
    args = parser.parse_args()

    snippets = collect_snippets()
    used = set()
    stale = []
    updated = 0

    targets = sorted(path for directory in DOCS_DIRS for path in directory.rglob("*.md"))
    for path in targets:
        before = path.read_text(encoding="utf-8")
        after = rewrite(before, snippets, used)
        if after == before:
            continue
        if args.check:
            stale.append(path.relative_to(ROOT))
        else:
            path.write_text(after, encoding="utf-8")
            updated += 1

    orphans = sorted(set(snippets) - used)
    for key in orphans:
        print(f"警告: 貼り先が見つからないスニペット: {key}", file=sys.stderr)

    if args.check:
        if stale:
            print("docs が古いままです。scripts/sync-snippets.py を実行してください:", file=sys.stderr)
            for path in stale:
                print(f"  {path}", file=sys.stderr)
            return 1
        print(f"ok: snippets={len(snippets)} 貼り付け済み={len(used)}")
        return 1 if orphans else 0

    print(f"snippets={len(snippets)} updated={updated} 貼り付け済み={len(used)}")
    return 1 if orphans else 0


if __name__ == "__main__":
    sys.exit(main())
