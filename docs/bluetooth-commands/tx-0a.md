---
title: "0x0A システム状態要求"
parent: Bluetooth コマンドリスト
nav_order: 8
---

# 0x0A システム状態要求

コマンド送信。

| ヘッダ | コマンドID | Type | Length | Value |
|---|---|---|---|---|
| `01` | `0A` | `80` | LE(ペイロード長) | 内側TLV |
| 1 byte | 1 byte | 1 byte | 2 byte | 可変長 |

`LE(n)` は整数nを2 byteのリトルエンディアンで表した値。Nは本文または圧縮データのbyte数。
[共通のパケット形式](../bluetooth-commands.md#通常パケットの形式)を参照。

| Type（1 byte） | Length（2 byte、LE） | Value |
|---|---|---|
| `01` | `02 00` | `00 00` |

状態は受信コマンド `0x87` で返る。

```text
01 0A 80 05 00 01 02 00 00 00
```

## 関連

- [Bluetooth コマンドリスト](../bluetooth-commands.md)
