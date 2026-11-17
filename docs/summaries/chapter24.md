---
source: manuscripts/chapter24-database-programming.md
summarized: 2026-11-16
sections:
  - key: "本章の学習目標"
    hash: 7145499c
  - key: "データベースを使う準備"
    hash: fc04011b
  - key: "SQLの基本"
    hash: 141ebdc8
  - key: "JDBCのしくみ"
    hash: 89e80a0a
  - key: "行を操作する"
    hash: 0736c0ef
  - key: "DAOパターン"
    hash: 0e2ac7d4
  - key: "日付と時刻の列とjava.time"
    hash: 4adcf547
  - key: "トランザクション"
    hash: e0fbc3c2
  - key: "コネクションプールとバッチ処理"
    hash: 58fa0a18
  - key: "よくあるエラーと対処法"
    hash: e91fd7f1
  - key: "まとめ"
    hash: d30bcd1a
  - key: "章末演習"
    hash: f021316d
---

# 第24章 データベースプログラミング 要約

行番号の正確な対応は `docs/book-index/chapter24-database-programming.md` を参照する。

## 章の概要

- 2026-11-16に、付録E「データベースプログラミング」（GitHubのREADMEが約1,200行で、実質1章分だった）を本文に移して新設した章（著者の指示「重要度とページ数を見て本文に入れられる付録は入れる」）
- H2（2.5.252）のメモリ上のデータベースで、SQLの基本、JDBCの接続、PreparedStatementによるCRUD、SQLインジェクション、DAOパターン、java.timeとの連携、トランザクション、HikariCP、バッチ処理を扱う
- 実践編（第21〜24章）の最後に置いた。JDBCドライバーのJARをクラスパスやMavenで加えるので、第22章のあとが自然。第25・26章の統合プロジェクトの前提になる
- サンプル: 24-1（pom.xml）・24-2（DDL）・24-3（DML）は本文だけ。24-4〜24-12は全文を`sample-code/chapter24/`に置き、実行結果を照合している（24-7と24-8は同じDAOの全文を2つのディレクトリに置いた）。演習は付録Eのものを`exercises/chapter24/`へ移した

## 節ごとの要約

- 本章の学習目標（L3–34）
    + 7項目。前提は第4・8・9・12・13・22章
- データベースを使う準備（L35–96）
    + ファイルとの違い、H2の入手と接続URLの表、ほかのデータベースのドライバーの表（2026-11時点の版をMaven Centralで確かめた）
- SQLの基本（L97–156）
    + DDL（主キー・制約・外部キー、方言）とDML（CRUD、WHERE・ORDER BY・GROUP BY・JOIN）
- JDBCのしくみ（L157–217）
    + java.sqlのインターフェイスとドライバー、図（chapter24-jdbc-architecture）、DriverManagerと自動登録（JDBC 4.0は脚注）、24-4
- 行を操作する（L218–347）
    + 24-5でCRUD、ResultSetのカーソル、executeUpdateの戻り値。24-6でSQLインジェクションとPreparedStatement（パスワードのハッシュは脚注）
- DAOパターン（L348–462）
    + エンティティ（Record User）・インターフェイス・JDBC実装、24-7・24-8、getGeneratedKeys、Optional、第25章のリポジトリへの誘導
- 日付と時刻の列とjava.time（L463–507）
    + 型の対応表、setObject・getObject、24-9、半開区間の条件、タイムゾーン。java.timeの詳細は付録D
- トランザクション（L508–561）
    + 自動コミット、24-10の送金と取り消し、DECIMALとBigDecimal（ACIDとdoubleの誤差は脚注）
- コネクションプールとバッチ処理（L562–656）
    + HikariCP 7.1.0（24-11、SLF4Jの警告を実行結果に含めた）、バッチ（24-12、MySQLのrewriteBatchedStatementsは脚注）
- よくあるエラーと対処法（L657–673）
    + 6項目。詳しい内容は付録Eの`chapters/chapter24.md`（2026-11-16に作成）
- まとめ（L674–699）・章末演習（L700–717）

## 他章との関係

- 第14章: ファイルへの保存との違いから入る
- 第22章: ドライバーのJARとMaven
- 第25・26章: DAO（本章）とリポジトリ（第25章）、SQLiteとH2の比較（第26章）
- 付録D: java.timeのクラスの使い方
- 2026-11-16: java.timeの脚注を第6章への誘導にし、「よくあるエラーと対処法」に付録Eの章ごとのファイル（chapter24.md、H2の実際のメッセージ）への案内を加えた
- 2026-11-17: 金額をBigDecimalで扱う脚注に、第6章で学んだ旨を添えた
