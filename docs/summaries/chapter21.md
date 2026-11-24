---
source: manuscripts/chapter21-unit-testing.md
summarized: 2026-11-24
sections:
  - key: "本章の学習目標"
    hash: 22fb1de6
  - key: "単体テストの基礎"
    hash: bdc3e3dc
  - key: "テストの基本形（AAAパターン）"
    hash: dfe868ba
  - key: "テストを妨げる「依存関係」"
    hash: fba29a8d
  - key: "疎結合設計（Dependency Injection）"
    hash: accce6ef
  - key: "DIによるテスト容易性の向上"
    hash: 7ff715d2
  - key: "テスト駆動開発（TDD）"
    hash: 79bee057
  - key: "JUnitによる効率的なテスト"
    hash: 88b7da73
  - key: "テストのベストプラクティス"
    hash: 1c4e7aab
  - key: "継続的インテグレーション（CI）との統合"
    hash: cb45ca8f
  - key: "よくあるエラーと対処法"
    hash: b81b8e91
  - key: "まとめ"
    hash: 0d7d24f3
  - key: "章末演習"
    hash: 4a00c03a
---

# 第21章 ユニットテストと品質保証 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter21-unit-testing.md` を参照する。

## 章の概要

- 単体テストの意義、AAAパターン、密結合がテストを妨げる理由、DI（コンストラクター・セッター・フィールドインジェクション）とスタブによる差し替え、TDDのサイクル、JUnit（Jupiter）とMockito、FIRST原則、CIとカバレッジを扱う
- 中心の題材はCalculatorとUserService/UserRepository。JUnitを使わない手動テストでAAAとスタブを示してから、JUnit・Mockitoへ進む
- 800行と短く、各トピックは概観にとどまる
- 書籍全体でJUnit、Mockito、DI、TDDを扱うのは本章だけで、他章との重複はほぼない

## 構成上の問題

- 前提知識と章順の矛盾
    + 推奨知識に第22章（Maven、Gradle）を挙げていた問題は、推奨から外して解消した（2026-11-08）
    + JUnit・Mockitoをプロジェクトに追加する方法がなかった問題は、IntelliJ IDEAでの追加手順と、Mavenでの書き方は第22章で学ぶという誘導をL413–419に加えて解消した（2026-11-11）
    + 旧21-14・旧21-15はGradleを前提にしていた。本文は「ビルドツールの役割は、Mavenを例に第22章で学びます」と断るが、Gradle自体は本書で扱う章がない（第23章はjarとjpackageが中心）。21-17（GitHub Actionsで`mvn -B verify`）と21-18（pom.xmlにmaven-surefire-pluginとjacoco-maven-pluginのreport・check）をMavenの例にし、第22章へ誘導した。Dockerのmaven:3-eclipse-temurin-25で`mvn -B verify`とJaCoCoのレポート・80%の検査を確かめた（2026-11-12に解消）
- TDDの実践例が1サイクルだけ
    + Red（21-9）とGreen（`return 0;`の21-10）のあと、Refactorは「不要」として終わる。三角測量や2つ目のテストがなく、サイクルを回す感覚が伝わらない。「1,2」の合計を返す2回目のサイクル（ステップ4〜6、21-11〜21-13、全文を登録）を加えた。これに伴い旧21-11〜21-15は21-14〜21-18になった（2026-11-12に解消）
- 21-11はCalculatorの`divide`をテストするが、21-2のCalculatorには`add`しかない。21-11を全文公開のサンプルにし、21-2に整数の`divide`を加えたCalculatorを全文に置き、本文で説明した（2026-11-12に解消）
- TDDの例（21-9）が、JUnitを紹介する節より前で`@Test`と`assertEquals`を説明なしに使っていた。21-9の前に読み方と後半への誘導を置いた（2026-11-14に解消）
- モックの定義がなく、スタブとの違いが書かれていなかった。Mockitoの節の冒頭に定義を加えた（2026-11-14に解消）
- カバレッジは21-15の設定値（80%）として出るだけで、カバレッジの種類や読み方の説明はない。行カバレッジの定義とレポートの読み方を短く加えた（2026-11-12に解消）

## 節ごとの要約

- 本章の学習目標（L3–30）
    + 単体テストの重要性、JUnit、TDD、疎結合とDI、モックの5項目
    + 前提は第4章・第7章・第13章、推奨は第11章のラムダ式（`assertThrows`で使う）とソフトウェア開発プロセス（2026-11-14に、本文と関係の薄い第15章とデザインパターンを外した）
- 単体テストの基礎（L31–91）
    + 単体テストの定義と4つのメリット（早期発見、リファクタリングの安全網、動く仕様書、開発の高速化）をH4ごとに文章で説明する
    + 早期発見の例として、平均を求めるメソッドを書いた直後に確かめ、int同士の割り算の誤りに気付くプログラム（21-1）。1.5は誤差なく表せるので`==`で比べていると断り、章末のエラー集へ誘導する
- テストの基本形（AAAパターン）（L92–161）
    + Arrange・Act・Assertの3ステップ
    + テスト対象のCalculator（21-2。単体では実行できないことと、フィールドも依存もないことを述べる）と、JUnitを使わない手動テスト（21-3）
- テストを妨げる「依存関係」（L162–199）
    + UserServiceが内部でUserRepositoryを`new`する密結合の例（21-4）。`getUserProfile`を呼ぶと、内部で生成したUserRepositoryの`findById`が必ずDBに接続することを問題として示す
- 疎結合設計（Dependency Injection）（L200–271）
    + インターフェイスへの依存、DIの定義、制御の反転（IoC）の一文の説明、密結合とDIの図（chapter21-dependency-injection）
    + コンストラクターインジェクション（推奨、21-5、finalにできる・生成時にそろう。スタブやモックの注入は次の節でインターフェイスにしてからと前置きする）、セッターインジェクション（21-6、任意にできるがNullPointerExceptionの危険）、フィールドインジェクション（コードなし、フレームワーク向け）
- DIによるテスト容易性の向上（L272–349）
    + UserRepositoryをインターフェイス化したUserService（21-7）
    + 手書きのスタブリポジトリでUserServiceをDBなしでテストする（21-8）
- テスト駆動開発（TDD）（L350–485）
    + Red・Green・Refactorのサイクルと図（chapter21-tdd-cycle）。Refactorは、テストが成功する状態を保ったまま重複を取り除くなどして読みやすく書き直すことと説明する
    + 1回目: StringCalculatorの空文字列で0を返すテスト（21-9）と最小実装（21-10）。クラスがなくコンパイルできないこともRedに含めること、`@Test`と`assertEquals`の読み方を述べ、JUnitの導入は章の後半で説明すると誘導する
    + 2回目: "1,2"の合計を返すテスト（21-11）、2つの数を足す実装（21-12）、ループで合計する形へのリファクタリング（21-13）
- JUnitによる効率的なテスト（L486–589）
    + JUnitをIntelliJ IDEAで追加する手順、最新の系列はJUnit 6（2025年9月、Java 17以降）で、本章のコードは5でも6でも動くこと、Mockitoの追加手順、Mavenでの依存関係は第22章で学ぶこと
    + JUnit Jupiterの`@Test`、`@BeforeEach`、`assertEquals`、`assertThrows`（21-14。21-2のCalculatorに`divide`を加えたもの）
    + モックの定義（スタブとの違い: 呼び出しを記録し、あとから検証できる）
    + Mockitoの`@Mock`、`when().thenReturn()`、`verify()`と、`@ExtendWith(MockitoExtension.class)`を付け忘れるとNullPointerExceptionになること、JDK 21以降でMockitoの動的エージェントの警告が出るがテストは成功していること（脚注で理由）（21-15）
- テストのベストプラクティス（L590–637）
    + FIRST原則
    + `@Nested`と`@DisplayName`による正常系・異常系の構造化（21-16）
- 継続的インテグレーション（CI）との統合（L638–745）
    + GitHub Actionsでプッシュごとに`mvn -B verify`を実行するワークフロー（21-17）
    + pom.xmlのmaven-surefire-pluginとjacoco-maven-plugin（prepare-agent・report・行カバレッジ80%のcheck）（21-18）と、行カバレッジの定義・レポートの読み方
- よくあるエラーと対処法（L746–762）
    + 本文に挙げる誤り: テストケースの不足、アサーションの使い方の誤り、モックの設定不足（設定していないメソッドはnullや0、空のコレクションを返す、使わない設定はstrict stubsで`UnnecessaryStubbingException`になる）、テスト間の依存。詳細は`appendix/e-common-errors/chapters/chapter21.md`
- まとめ（L763–781）
    + 単体テスト、疎結合、DI、TDD（Red-Green-Refactor）、ツール活用の5点
- 章末演習（L782–801）
    + 演習リンクは`exercises/chapter21/`。基礎（JUnit）、発展（TDD）、チャレンジ（モック）の3段構成。次のステップとして第22章を案内する

## 他章との関係

- 第4章・第7章: クラスとインターフェイスへの依存（21-7）の前提
- 第11章: 21-14の`assertThrows`でラムダ式を使う。推奨知識に挙げている
- 第13章: `assertThrows`やArithmeticExceptionの前提
- 第22章: 外部ライブラリとクラスパス、Mavenでの依存関係の書き方を扱う。JUnitの節とCIの節から誘導する
- 第23章: GitHub Actionsによる自動ビルドに触れる程度。本章21-17と題材が接するが重複は小さい
- 付録B.15（`appendix/b15-testing-strategies/`）: 本章から参照していない。BDDやレガシーコードへのテスト追加の受け皿になりうる

## 校正で見つけた個別の問題

- L26: 「推奨される知識：」はコロンでリストを導入している（2026-11-12に解消）
- L195–196、L209–210、L461–464: 文の途中で改行しており、読点で行を分けている（`vfm-line-breaks.js`で和文の改行は除かれるので紙面への影響はない）（2026-11-12に解消）
- L413–417: JUnitとMockitoの追加手順がIntelliJ IDEAに限られ、付録Aの環境構築（SDKMAN・ターミナル中心）との対応が書かれていない。なお、クイックフィックスの項目名（Add 'JUnit5…' to classpath）に頼る書き方は、版で文言が変わるため「JUnit 5をクラスパスに追加する項目」に改めた（2026-11-12に解消）。2026-11-14に、JUnit 6が出たため「JUnitをクラスパスに追加する項目」にした
- 現21-17（当時の21-14）: `actions/setup-java@v2`は`distribution`の指定が必須で、このままでは失敗する。`checkout@v2`も古く、JDKは17（本書の対象はJava 25）。リポジトリのCIと同じ`checkout@v7`・`setup-java@v6`、`temurin`、25に直した（2026-11-12に解消）
- 当時の21-15（Gradle）: JaCoCoの`xml.enabled`/`html.enabled`はGradle 8で削除済み（`required`を使う）。CLAUDE.mdの「Gradle 9で動く設定にする」方針とも合わない。`required`に直し、javaプラグイン・JUnitの依存・`useJUnitPlatform()`を加え、Docker（gradle:jdk25、Gradle 9.8）で`gradle check`が通ることを確かめた（2026-11-12に解消）
- L662: 「次のステップ： 基礎課題が完了したら」は項目名＋コロンの形（2026-11-12に解消）
- 21-1のコメントだけのコードと「実行結果」、手戻りコストの「100分の1」の断定、21-12のMockitoの初期化漏れ、DIの説明での「モック」と「スタブ」の混同、TDDサイクルの絵文字は、2026-11-11に解消した。エラー集の21-19は付録（現在の付録E）へ移した
- 2026-11-14の校閲で次を解消した: L196の「`new UserRepository()`でDB接続が走る」という不正確な説明、IoCの用語の説明不足、21-5の時点でスタブを注入できるとした先取り、21-5のコメントの「コンストラクタ」、Calculatorの「テストしやすい」の重複、21-1の`==`による比較と章末の「誤差を許容して比べる」との食い違い、Mockitoの警告の説明不足（JDK 25で確認）、章末の「モックにすべて設定する」という誤解を招く対処（strict stubsの`UnnecessaryStubbingException`をMockito 5.20で確認）、推奨知識の第15章・デザインパターン
- JUnit 6の扱いと、21-18の版（2026-11-14に解消。Maven Centralで、junit-jupiter 6.1.3・maven-surefire-plugin 3.6.1・jacoco-maven-plugin 0.8.16が最新の版として実在することを確かめた。本文はJUnit Jupiterと書き、JUnit 6が最新の系列であることを本文で述べた。`listings.json`の版もjunit-jupiter-api 6.1.3・Mockito 5.24.0に上げた）
- 2026-11-16: 付録の再編（付録D・Eを本文へ移し、残りをA〜Fに振り直した）に合わせ、付録の文字とURLを直した（付録Hを付録F、付録Fを付録D、付録Gを付録Eに。h-common-errorsはf-common-errorsに）
- 2026-11-16: java.timeを第6章へ編入して付録Dを削除し、付録を振り直した（付録Eを付録D、付録Fを付録Eに。f-common-errorsはe-common-errorsに）
- 2026-11-18の校閲で直したもの
    + TDDのサイクルの名前を「Refactoring」から「Refactor」に統一し（見出し・まとめも）、Refactorの説明の「綺麗に、効率的に」を「重複を取り除くなどして読みやすく」に改めた
    + 「Dependency Injection (DI)」「制御の反転 (Inversion of Control - IoC)」の半角括弧を全角括弧に、「他の」を「ほかの」にそろえた
    + よくあるエラーのモックの既定の戻り値に、空のコレクションを加えた
- 2026-11-23（流れ・矛盾・過不足の校閲）: 第22章: 最初のpom.xmlの例の前に「Mavenのプロジェクトを作る」（IntelliJ IDEAでMavenを選ぶ、IDE内蔵のMaven、コマンドラインは付録Aでインストール）を置き、依存関係解決のアルゴリズムをpom.xmlの例の後ろへ移した。「実践的な開発フロー」にフェーズ（compile→test→package→verify）とプラグイン・ゴールの関係を足した。リポジトリの「Repository Pattern」をやめ、データの保存を隠すリポジトリとは別の意味だと脚注で区別した。第21章: 第22章への誘導にプラグインとゴールを含め、JaCoCoの箇所でゴールの読み方を添え、章末の浮動小数点数の比べ方に許容誤差の指定のしかたを書いた
- 2026-11-23（流れ・矛盾・過不足の校閲）: 第21〜23章の低の指摘: 第21章はJUnitの節をTDDの節より前へ移して先取りの断りをなくし、テストメソッドのpublicの有無を説明し、前提を第3・4章のクラスにした。第22章はクラスパスの定義（JARをクラスファイルの一種のように書いていた）、スコープのruntime・provided、Mockitoの座標（第21章と同じmockito-junit-jupiter）、22-6の抜粋にTypeReferenceの行を含め、まとめから本文にない話題（Ivy・サプライチェーンなど）を外した。第23章はapp-imageの補足を一覧に統合し、第26章が使う--java-optionsを足し、前提から第14章を外した。第2章の「JavaDoc」を第22章の「Javadoc」にそろえた
- 2026-11-24: 学習目標を「話題＋〜できるの子項目」に、まとめを本文の再掲から「判断の順序・持ち帰ってほしい1つ・次章へのつなぎ」の段落に書き直した（第7章の見本にそろえた）。初めて読むときは飛ばしてよい節に「発展」の印（{.advanced}）を付けた
- 2026-11-24: 著者の判断で本文に残した節（第15章「新しい並行処理のしくみ」、第21章「継続的インテグレーション（CI）との統合」）の発展の印を外した
