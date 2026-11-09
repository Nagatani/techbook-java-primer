---
source: manuscripts/chapter09-collections.md
summarized: 2026-11-08
sections:
  - key: "本章の学習目標"
    hash: db26e2d9
  - key: "なぜコレクションフレームワークが必要か"
    hash: f8fd27fb
  - key: "コレクションフレームワークの全体像"
    hash: 4f53989e
  - key: "List インターフェイス"
    hash: e82a41c0
  - key: "Set インターフェイス"
    hash: 9295f0aa
  - key: "Map インターフェイス"
    hash: 0c6510b4
  - key: "イテレータとfor-each文"
    hash: 7016fbd5
  - key: "コレクションの選択指針"
    hash: 0419f4c4
  - key: "今後の学習へ向けて"
    hash: 7e623039
  - key: "よくあるエラーと対処法"
    hash: 2d4ef3a4
  - key: "まとめ"
    hash: e71965b3
  - key: "章末演習"
    hash: e8b84796
---

# 第9章 コレクションフレームワーク 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter09-collections.md` を参照する。

## 章の概要

- 配列の限界（固定サイズ、削除の手間、検索などの操作不足）を動機に、List・Set・Mapの使い分けを教える
- 主要な実装はArrayList/LinkedList、HashSet/TreeSet/LinkedHashSet、HashMap/TreeMap/LinkedHashMap
- 走査はfor-each文とIterator（走査中の削除）を扱う
- 前提は第2章の配列と第7章のインターフェイス
- ジェネリクス記法（`List<String>`）は説明なしで使い、第10章冒頭で「第9章で見た不思議な記法」として回収している

## 構成の経緯

2026-11-07に、2つの版が結合されていた状態（同名の「本章の学習目標」が2つ、List・Set・Mapを2回説明）を解消した。
前半の版の流れを骨格にし、後半の版から完全なサンプル（ArrayList・HashSet・HashMap）、配列とListの相互変換、比較表、コレクションの利点、エラー集、まとめを移した。
重複していた後半の学習目標、配列の限界、走査方法の説明は削除した。

## 節ごとの要約

- 本章の学習目標（L3–19）
    + 学ぶことを4項目（基本理解、主要な実装クラス、操作、時間計算量を考慮した選択とエラー対処）に整理している
- なぜコレクションフレームワークが必要か（L20–198）
    + 学生名簿を配列で書き、サイズ固定・削除時の穴・検索の自作という3つの問題を示す（9-1〜9-5）
    + 同じ処理をArrayListで書き直して解決を示し（9-6）、コレクションの4つの利点（動的サイズ・豊富な機能・ポリモーフィズム・型安全）をまとめる
- コレクションフレームワークの全体像（L199–241）
    + Collection（List・Set・Queue）とMapの階層図。Queueはここで名前が出るだけで以降扱わない
- List インターフェイス（L242–368）
    + ArrayListの完全なサンプル（9-7）
    + ArrayListとLinkedListの比較コード（9-8）と比較表、使い分けの指針、内部構造の図
    + 配列とListの相互変換とArrays.asListの固定サイズの注意（9-9）
- Set インターフェイス（L369–470）
    + HashSetの完全なサンプル。addの戻り値で重複を検出する（9-10）
    + HashSet/TreeSet/LinkedHashSetの順序の違い（9-11）と使い分け
    + addAll/retainAll/removeAllによる和集合・積集合・差集合（9-12）
- Map インターフェイス（L471–599）
    + HashMapの完全なサンプル。putの上書き、entrySetの走査（9-13）
    + HashMap/TreeMap/LinkedHashMapの違い（9-14）、HashMap内部構造の図
    + getOrDefault、containsKey、keySetとentrySetの走査（9-15）
- イテレータとfor-each文（L600–678）
    + Iteratorでの走査中の安全な削除（9-16）とfor-each文の制約（9-17）
    + 拡張for文・イテレータ・従来のforループの使い分け
- コレクションの選択指針（L679–732）
    + 6実装の計算量表と、実装ごとの使用場面・実例。付録B.06への参照
- 今後の学習へ向けて（L733–774）
    + Stream API（9-18、第11・12章への参照あり）と`List.of`などの不変コレクション（9-19）を紹介する
- よくあるエラーと対処法（L775–796）
    + 2026-11-09に、エラーメッセージの例・修正コード・デバッグのヒントを`appendix/h-common-errors/chapters/chapter09.md`へ移した。本文には出会いやすいエラーと原因の要点のリストとGitHubのURLだけを残す
    + 本文に挙げるエラー: NullPointerException、ClassCastException、UnsupportedOperationException、ConcurrentModificationException、IndexOutOfBoundsException、Mapで存在しないキーへのアクセス、equals()とhashCode()の不整合
- まとめ（L797–808）
    + List・Set・Mapの比較表。次章のジェネリクスへの導入
- 章末演習（L809–829）
    + 演習リンクは`exercises/chapter09/`（2026-11-09に演習ディレクトリを現行の章番号に組み替え、リンクを直した。Issue #010）

## 他章との関係

- 第10章（ジェネリクス）: 本章で説明なしに使った`<String>`を冒頭で回収する
- 第11章・第12章（ラムダ式・Stream API）: 本章の「今後の学習へ向けて」で予告している
- 第12章: 「データ構造の選択」が本章の選択指針と重複している（第12章側の課題）
- 第13章（例外処理）: 本章のエラー集で扱う例外の体系的な説明がある
- 付録B.06: コレクションの内部実装（原稿の付録B索引ではB.07。Issue #011）

## 校正で見つけた個別の問題

- 9-8の導入文が「基本的な使い方と特性を比較」としているが、直前に9-7で基本的な使い方を示しているため表現が重い
- 9-18は第11章で学ぶラムダ式を使っている（予告として許容範囲だが、説明はない）
- 「不変コレクションはスレッドセーフ」は、要素自体が可変オブジェクトの場合に誤解を招く
