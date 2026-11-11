---
source: manuscripts/chapter09-collections.md
summarized: 2026-11-11
sections:
  - key: "本章の学習目標"
    hash: 82dc17cb
  - key: "なぜコレクションフレームワークが必要か"
    hash: ea5c970e
  - key: "コレクションフレームワークの全体像"
    hash: 08e3220e
  - key: "List インターフェイス"
    hash: 4906b383
  - key: "Set インターフェイス"
    hash: 01b12594
  - key: "Map インターフェイス"
    hash: aa9a7cbc
  - key: "イテレーターとfor-each文"
    hash: b9453ceb
  - key: "コレクションの選択指針"
    hash: bbf0c88d
  - key: "今後の学習へ向けて"
    hash: 8b99be23
  - key: "よくあるエラーと対処法"
    hash: 89ce951f
  - key: "まとめ"
    hash: e71965b3
  - key: "章末演習"
    hash: dbeb6e1f
---

# 第9章 コレクションフレームワーク 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter09-collections.md` を参照する。

## 章の概要

- 配列の限界（固定サイズ、削除の手間、検索などの操作不足）を動機に、List・Set・Mapの使い分けを教える
- 主要な実装はArrayList/LinkedList、HashSet/TreeSet/LinkedHashSet、HashMap/TreeMap/LinkedHashMap
- 走査は拡張for文とイテレーター（走査中の削除）を扱う
- 前提は第2章の配列と第7章のインターフェイス。HashSet・HashMapの節では第5章のequals・hashCodeを参照する（2026-11-11に、equals・hashCodeの説明を第5章へ集め、本章は要点と誘導にした）
- ジェネリクス記法（`List<String>`）は説明なしで使い、第10章冒頭で回収する。型引数を省いたコレクション（raw type）の問題は第10章に任せる（2026-11-11に重複を整理）

## 構成の経緯

2026-11-07に、2つの版が結合されていた状態（同名の「本章の学習目標」が2つ、List・Set・Mapを2回説明）を解消した。
前半の版の流れを骨格にし、後半の版から完全なサンプル（ArrayList・HashSet・HashMap）、配列とListの相互変換、比較表、コレクションの利点、エラー集、まとめを移した。
2026-11-11に、利点や使い分けを並べた箇条書きを文章にした。

## 節ごとの要約

- 本章の学習目標（L3–20）
    + 4項目（基本理解、主要な実装クラス、操作、時間計算量を考慮した選択とエラー対処）。前提として第7章のインターフェイスと第5章のequals・hashCodeを挙げる
- なぜコレクションフレームワークが必要か（L21–196）
    + 学生名簿を配列で書き、サイズ固定・削除時の穴・検索の自作という3つの問題を示す（9-1〜9-5）
    + 同じ処理をArrayListで書き直して解決を示し（9-6）、コレクションの利点（動的サイズ・豊富な機能・ポリモーフィズム・型安全）を文章でまとめる
- コレクションフレームワークの全体像（L197–240）
    + Collection（List・Set・Queue）とMapの階層図と主な実装クラスの一覧。Queueはここで名前が出るだけで以降扱わない
- List インターフェイス（L241–368）
    + ArrayListの完全なサンプル（9-7）
    + ArrayListとLinkedListの比較コード（9-8）と比較表、使い分けの指針、内部構造の図
    + 配列とListの相互変換と`Arrays.asList`の固定サイズの注意（9-9）
- Set インターフェイス（L369–484）
    + HashSetの完全なサンプル。addの戻り値で重複を検出する（9-10）。hashCodeとequalsによる重複の判定と、自作クラスでは両方のオーバーライドが要ることを説明し、第5章へ誘導する
    + HashSet/TreeSet/LinkedHashSetの順序の違い（9-11、図chapter09-set-order）と使い分け（文章）
    + addAll/retainAll/removeAllによる和集合・積集合・差集合（9-12）
- Map インターフェイス（L485–620）
    + HashMapの完全なサンプル。putの上書き、entrySetの走査（9-13）。キーの判定もhashCodeとequalsによると説明する
    + HashMap/TreeMap/LinkedHashMapの違い（9-14）と用途（文章）、HashMap内部構造の図
    + getOrDefault、containsKey、keySetとentrySetの走査（9-15）と、entrySetが速い理由
- イテレーターとfor-each文（L621–696）
    + Iteratorでの走査中の安全な削除（9-16）とイテレーターの利点
    + 拡張for文（9-17）の特徴と、走査中に削除できない制約。拡張for文・イテレーター・従来のforループの使い分け
- コレクションの選択指針（L697–750）
    + 6実装の計算量表と、実装ごとの使用場面・実例。付録B.06への参照
- 今後の学習へ向けて（L751–796）
    + 拡張for文とStream APIの書き比べ（9-18）。ラムダ式・メソッド参照の読み方を短く説明し、第11章・第12章へ誘導する
    + `List.of`などの不変コレクション（9-19）
- よくあるエラーと対処法（L797–818）
    + 出会いやすい例外・誤り7点（NullPointerException、ClassCastException（第10章へ誘導）、UnsupportedOperationException、ConcurrentModificationException、IndexOutOfBoundsException、存在しないキー、equalsとhashCodeの不整合（第5章））の要点と、付録H（`appendix/h-common-errors/chapters/chapter09.md`）のURL
- まとめ（L819–830）
    + List・Set・Mapの比較表。次章のジェネリクスへの導入
- 章末演習（L831–851）
    + `exercises/chapter09/`へのリンクと、基礎・発展・チャレンジ課題の概要。第10章への案内

## 他章との関係

- 第5章（継承とポリモーフィズム）: equals・hashCodeの正式な説明。本章のSet・Map・エラー集から誘導する（2026-11-11）
- 第10章（ジェネリクス）: 本章で説明なしに使った`<String>`を冒頭で回収し、raw typeの問題を扱う
- 第11章・第12章（ラムダ式・Stream API）: 本章の「今後の学習へ向けて」で予告している
- 第12章: 「データ構造の選択」が本章の選択指針と重複していた（2026-11-11に解消。第12章の節が消えた）
- 第13章（例外処理）: 本章のエラー集で扱う例外の体系的な説明がある
- 付録B.06「コレクションの内部実装」: `appendix/b06-collection-internals/`と一致（2026-11-09に解消。Issue #011）

## 校正で見つけた個別の問題

- L304: 9-8の導入文が「基本的な使い方と特性を比較」としているが、直前に9-7で基本的な使い方を示しているため表現が重い
- 9-18がラムダ式を説明なしで使っていた（2026-11-11に解消。読み方の説明と誘導を足した）
- L794: 「不変コレクションはスレッドセーフ」は、要素自体が可変オブジェクトの場合に誤解を招く
- L711–745: 使用場面の番号付きリストの項目が「1. ArrayList:」のようにコロンで終わっている（リスト項目の末尾にコロンを使わない規約に反する）
- L367: 「注意： `Arrays.asList()`…」は全角コロンの後に半角空白がある。L851「次のステップ： 」も同じ
- L22–23、L242–245: 見出しの前後に空行が2つ続いている
- L541–542: 「一般的なデータ検索ではHashMapで最高速度を実現します」は根拠のない誇張に読める（直後のL566–569で平均O(1)と説明している）
