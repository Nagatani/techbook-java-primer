---
source: manuscripts/chapter21-unit-testing.md
summarized: 2026-11-11
sections:
  - key: "本章の学習目標"
    hash: 0e5ab77e
  - key: "単体テストの基礎"
    hash: 20abcd83
  - key: "テストの基本形（AAAパターン）"
    hash: d8882771
  - key: "テストを妨げる「依存関係」"
    hash: 457e89bd
  - key: "疎結合設計（Dependency Injection）"
    hash: 07168858
  - key: "DIによるテスト容易性の向上"
    hash: e874b14f
  - key: "テスト駆動開発（TDD）"
    hash: 43c75f7e
  - key: "JUnitによる効率的なテスト"
    hash: 9936e3b0
  - key: "テストのベストプラクティス"
    hash: d8a6a05e
  - key: "継続的インテグレーション（CI）との統合"
    hash: dd24d19a
  - key: "よくあるエラーと対処法"
    hash: a871107b
  - key: "まとめ"
    hash: 1cc54646
  - key: "章末演習"
    hash: a361d3f8
---

# 第21章 ユニットテストと品質保証 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter21-unit-testing.md` を参照する。

## 章の概要

- 単体テストの意義、AAAパターン、密結合がテストを妨げる理由、DI（コンストラクター・セッター・フィールドインジェクション）とスタブによる差し替え、TDDのサイクル、JUnit 5とMockito、FIRST原則、CIとカバレッジを扱う
- 中心の題材はCalculatorとUserService/UserRepository。JUnitを使わない手動テストでAAAとスタブを示してから、JUnit・Mockitoへ進む
- 662行と短く、各トピックは概観にとどまる
- 書籍全体でJUnit、Mockito、DI、TDDを扱うのは本章だけで、他章との重複はほぼない

## 構成上の問題

- 前提知識と章順の矛盾
    + 推奨知識に第22章（Maven、Gradle）を挙げていた問題は、推奨から外して解消した（2026-11-08）
    + JUnit・Mockitoをプロジェクトに追加する方法がなかった問題は、IntelliJ IDEAでの追加手順と、Mavenでの書き方は第22章で学ぶという誘導をL413–419に加えて解消した（2026-11-11）
    + 21-14・21-15はGradleを前提にする。本文（L551–553）は「ビルドツールの役割は、Mavenを例に第22章で学びます」と断るが、Gradle自体は本書で扱う章がない（第23章はjarとjpackageが中心）
- TDDの実践例が1サイクルだけ
    + Red（21-9）とGreen（`return 0;`の21-10）のあと、Refactoringは「不要」として終わる。三角測量や2つ目のテストがなく、サイクルを回す感覚が伝わらない
- 21-11はCalculatorの`divide`をテストするが、21-2のCalculatorには`add`しかない。21-11を全文公開のサンプルにし、21-2に整数の`divide`を加えたCalculatorを全文に置き、本文で説明した（2026-11-12に解消）
- カバレッジは21-15の設定値（80%）として出るだけで、カバレッジの種類や読み方の説明はない

## 節ごとの要約

- 本章の学習目標（L3–31）
    + 単体テストの重要性、JUnit、TDD、疎結合とDI、モックの5項目
    + 前提は第4章・第7章・第13章、推奨は第15章・デザインパターン
- 単体テストの基礎（L32–90）
    + 単体テストの定義と4つのメリット（早期発見、リファクタリングの安全網、動く仕様書、開発の高速化）をH4ごとに文章で説明する
    + 早期発見の例として、平均を求めるメソッドを書いた直後に確かめ、int同士の割り算の誤りに気付くプログラム（21-1、2026-11-11にコメントだけのコードから置き換えた）
- テストの基本形（AAAパターン）（L91–160）
    + Arrange・Act・Assertの3ステップ
    + テスト対象のCalculator（21-2。単体では実行できないことと、テストしやすい理由を述べる）と、JUnitを使わない手動テスト（21-3）
- テストを妨げる「依存関係」（L161–199）
    + UserServiceが内部でUserRepositoryを`new`する密結合の例（21-4）。テストするとDB接続が走ることを問題として示す
- 疎結合設計（Dependency Injection）（L200–270）
    + インターフェイスへの依存、DIとIoCの定義、密結合とDIの図（chapter21-dependency-injection）
    + コンストラクターインジェクション（推奨、21-5、finalにできる・生成時にそろう）、セッターインジェクション（21-6、任意にできるがNullPointerExceptionの危険）、フィールドインジェクション（コードなし、フレームワーク向け）
- DIによるテスト容易性の向上（L271–348）
    + UserRepositoryをインターフェイス化したUserService（21-7）
    + 手書きのスタブリポジトリでUserServiceをDBなしでテストする（21-8）
- テスト駆動開発（TDD）（L349–406）
    + Red・Green・Refactoringのサイクルと図（chapter21-tdd-cycle）
    + StringCalculatorの空文字列で0を返すテスト（21-9）と最小実装（21-10）
- JUnitによる効率的なテスト（L407–497）
    + JUnitをIntelliJ IDEAで追加する手順、Mockitoの追加手順、Mavenでの依存関係は第22章で学ぶこと
    + JUnit 5の`@Test`、`@BeforeEach`、`assertEquals`、`assertThrows`（21-11）
    + Mockitoの`@Mock`、`when().thenReturn()`、`verify()`と、`@ExtendWith(MockitoExtension.class)`を付け忘れるとNullPointerExceptionになること（21-12）
- テストのベストプラクティス（L498–545）
    + FIRST原則
    + `@Nested`と`@DisplayName`による正常系・異常系の構造化（21-13）
- 継続的インテグレーション（CI）との統合（L546–606）
    + GitHub Actionsでプッシュごとに`./gradlew test`を実行するワークフロー（21-14）
    + JaCoCoで80%のカバレッジを要求するbuild.gradle（21-15）
- よくあるエラーと対処法（L607–622）
    + 本文に挙げる誤り: テストケースの不足、アサーションの使い方の誤り、モックの設定不足、テスト間の依存。詳細は`appendix/h-common-errors/chapters/chapter21.md`
- まとめ（L623–641）
    + 単体テスト、疎結合、DI、TDD、ツール活用の5点
- 章末演習（L642–662）
    + 演習リンクは`exercises/chapter21/`。基礎（JUnit）、発展（TDD）、チャレンジ（モック）の3段構成。次のステップとして第22章を案内する

## 他章との関係

- 第4章・第7章: クラスとインターフェイスへの依存（21-7）の前提
- 第11章: 21-11の`assertThrows`でラムダ式を使う（説明はない）
- 第13章: `assertThrows`やArithmeticExceptionの前提
- 第22章: 外部ライブラリとクラスパス、Mavenでの依存関係の書き方を扱う。本章のL419とL552から誘導する
- 第23章: GitHub Actionsによる自動ビルドに触れる程度。本章21-14と題材が接するが重複は小さい
- 付録B.15「テスト戦略」: 本章から参照していない。BDDやレガシーコードへのテスト追加の受け皿になりうる

## 校正で見つけた個別の問題

- L26: 「推奨される知識：」はコロンでリストを導入している（2026-11-12に解消）
- L195–196、L209–210、L461–464: 文の途中で改行しており、読点で行を分けている（`vfm-line-breaks.js`で和文の改行は除かれるので紙面への影響はない）（2026-11-12に解消）
- L413–417: JUnitとMockitoの追加手順がIntelliJ IDEAに限られ、付録Aの環境構築（SDKMAN・ターミナル中心）との対応が書かれていない
- 21-14: `actions/setup-java@v2`は`distribution`の指定が必須で、このままでは失敗する。`checkout@v2`も古く、JDKは17（本書の対象はJava 25）。リポジトリのCIと同じ`checkout@v7`・`setup-java@v6`、`temurin`、25に直した（2026-11-12に解消）
- 21-15: JaCoCoの`xml.enabled`/`html.enabled`はGradle 8で削除済み（`required`を使う）。CLAUDE.mdの「Gradle 9で動く設定にする」方針とも合わない。`required`に直し、javaプラグイン・JUnitの依存・`useJUnitPlatform()`を加え、Docker（gradle:jdk25、Gradle 9.8）で`gradle check`が通ることを確かめた（2026-11-12に解消）
- L662: 「次のステップ： 基礎課題が完了したら」は項目名＋コロンの形（2026-11-12に解消）
- 21-1のコメントだけのコードと「実行結果」、手戻りコストの「100分の1」の断定、21-12のMockitoの初期化漏れ、DIの説明での「モック」と「スタブ」の混同、TDDサイクルの絵文字は、2026-11-11に解消した。エラー集の21-19は付録Hへ移した
