---
source: manuscripts/appendix-f-java-time-api.md
summarized: 2026-11-07
sections:
  - key: "付録Fについて"
    hash: 88c68524
---

# 付録F java.time API完全ガイド 要約

行番号の正確な対応は `docs/book-index/appendix-f-java-time-api.md` を参照する。

## 章の概要

- Java 8のjava.time APIの付録。本文は概要のみで、詳細はGitHubの`appendix/f-java-time-api/`にある
- サンプルコードはない
- 本編のどの章も日付時刻APIを体系的には扱わないため、本書で唯一のjava.timeの説明場所になる

## 節ごとの要約

- 付録Fについて（L3–84）
    + 学習内容（L7–18）: Date/Calendarの問題点、主要クラス（LocalDate、LocalTime、LocalDateTime、ZonedDateTime、Instant）、作成と操作、DateTimeFormatter、PeriodとDuration、タイムゾーン、ファイル操作での実践、レガシーAPIとの変換、ベストプラクティスの9項目
    + 前提知識（L19–32）: 第4章、第12章、第13章、第14章
    + オンライン資料（L33–45）: リポジトリの内容5点
    + 関連する付録（L51–55）: 付録D、付録E
    + 学習のポイント（L56–71）: 不変性、クラスの選択、タイムゾーン、スレッドセーフティ、レガシーAPIからの移行
    + 実践への応用（L72–84）

## 他章との関係

- 第4章: 前提知識で「不変オブジェクトの概念」を第4章に帰しているが、不変性を扱うのは第6章（不変性とfinal）
- 第12章: 「コレクションフレームワーク（Stream API）」と書いているが、コレクションフレームワークは第9章、第12章は「Stream APIと高度なコレクション操作」
- 第13章: 13-12、13-14、13-13などでInstantを説明なしに使っている
- 第14章: ファイル時刻属性の操作との関係
- 付録D・付録E: 関連付録として挙げる

## 校正で見つけた個別の問題

- L27: 第12章の名称が現行の章タイトルとずれている
- L25: 不変オブジェクトの前提章は第6章のほうが適切
- L68: 「DateTimeFormatterを含むすべてのクラスがスレッドセーフ」は「java.timeの主要クラス」に限定すべき断定
- L35: 「**GitHub リポジトリ**:」の太字と半角コロン
- タイトル「完全ガイド」は本文が概要のみであることと釣り合わない
