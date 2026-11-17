---
source: manuscripts/appendix-f-swing-advanced.md
summarized: 2026-11-18
sections:
  - key: "付録Fについて"
    hash: 0d6d1680
---

# 付録F Swingの発展的な機能 要約

## 概要

- 2026-11-16に新設。第19・20章から、演習の基礎課題では使わないSwingの発展的な機能のコード例（PropertyChangeSupport、ドラッグ&ドロップ、デバウンスとスロットル、JTreeの動的な操作、カスタムセルエディター）を移した
- GitHubの`appendix/f-swing-advanced/README.md`に本文から移した解説とコードの抜粋、`code/`にコンパイルできる全文（`check:public-code`の対象）
- 第19・20章の発展課題・チャレンジ課題のREADMEから案内している

## 節ごとの要約

- 付録Fについて（L3–26）
    + 第19・20章で要点だけを説明した機能を、演習の発展課題とチャレンジ課題で使うと位置づける
    + 内容: `PropertyChangeSupport`（株価モニター）、`TransferHandler`と`DropTarget`のドラッグ&ドロップ、デバウンスとスロットル、`DefaultTreeModel`によるノードの動的な追加・削除、`JTable`のカスタムセルエディターの5項目
    + 関連する章: 第19章「高度なイベント処理パターン」（項目1〜3）、第20章「JTable・JList・JTree」（項目4・5）
    + オンライン資料: `appendix/f-swing-advanced/`のURL

## 校正で見つけた個別の問題

- 2026-11-18の校閲: 「本文の基礎課題では使わず」を「演習の基礎課題では使わず」に直した
