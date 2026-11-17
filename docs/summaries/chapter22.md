---
source: manuscripts/chapter22-documentation-and-libraries.md
summarized: 2026-11-18
sections:
  - key: "本章の学習目標"
    hash: a464781d
  - key: "Javadocによるドキュメント"
    hash: 424b7704
  - key: "外部ライブラリの利用"
    hash: 55862cbb
  - key: "よくあるエラーと対処法"
    hash: 57d950a6
  - key: "まとめ"
    hash: 8108ed2e
  - key: "章末演習"
    hash: b5c5207d
---

# 第22章 ドキュメントと外部ライブラリ 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter22-documentation-and-libraries.md` を参照する。

## 章の概要

- 前半はJavadoc（コメントの書き方、主要タグとインラインタグ、生成方法）、後半は外部ライブラリの利用（クラスパス、Mavenによる依存関係管理、代表的なライブラリの紹介、選定指針、Mavenのコマンド、Gradleの紹介）を扱う
- 使用例を示すライブラリはGson、Jackson、Apache Commons Lang/IOで、それぞれpom.xmlの依存関係と使用例・実行結果を示す（使用例は`sample-code/chapter22/`の全文を実行した出力）。OkHttp・Lombok・Guavaは役割と標準APIとの関係の表だけを載せ、使用例は付録G（`appendix/g-library-examples/`）へ誘導する
- 外部ライブラリとクラスパス、Mavenでの依存関係の書き方を正式に説明する章で、第16章（Jackson）と第21章（JUnit・Mockito）から誘導される
- 前提は第4章（クラス）、第13章（例外処理）、第14章（ファイルI/O）。第21章（ユニットテスト）は推奨扱い
- ビルドツールは主にMavenを扱い、「もう1つのビルドツール：Gradle」でbuild.gradle、Gsonの依存関係の例（22-14）、Gradle Wrapper、付録D（`appendix/d-build-and-deploy/`）への誘導を示す

## 構成上の問題

- 第21章のGradleの例と、本章でGradleを説明しない問題は、第21章をMavenの例にし、本章にGradleの紹介を加えて解消した（2026-11-12に解消）
- 前提知識の矛盾、章内の内容の重複（トラブルシューティングとエラー集）、「よくあるエラーと対処法」の分量、選定の指針の観点と小見出しの不一致、クラスパスの節に`-cp`の使い方がない問題は、2026-11-09〜11-12に解消した
- 以下のLombok・OkHttpについての記録は、2026-11-16に両ライブラリの節を付録Gへ移す前のもの
- Lombok（旧22-13〜22-15）がJava 25で動かず、実行結果もコードと別のシミュレーションの出力だった問題は、次のように解消した。JDK 23以降は指定しないと注釈処理が動かないことを本文に書き、22-13に`maven-compiler-plugin`の`<annotationProcessorPaths>`を加えた。22-14・22-15を全文化し（listings.jsonの`processor: true`）、`@Slf4j`をやめて実際の出力に差し替えた。DockerのJDK 25（Maven）で設定の有無による成否を確かめた（2026-11-14に解消）
- 各ライブラリの冒頭の紹介が2回続いていた結合跡（Jackson、OkHttp、Lombok、Guava）は、1つにまとめて解消した（2026-11-14に解消）
- pom.xmlの全体構成は示さない。Java 25を対象にする設定は、「実践的な開発フロー」で`archetype:generate`のひな形の`maven.compiler.release`を25に書き換える説明だけで扱う
- OkHttpを5.5.1に上げた（2026-11-14）。5.x系はMavenでは`okhttp-jvm`を指定する（`okhttp`は中身のないJARで、Gradleの付加情報でokhttp-jvmを指す）ことを脚注に書いた。Jackson 2.xは保守が続いているので据え置いた（2026-11-18に、Jackson 3でgroupIdとパッケージ名が`tools.jackson`に変わったことと、本書がJackson 2の系列で書くことを脚注に加えた）

## 節ごとの要約

- 本章の学習目標（L3–31）
    + Javadoc（しくみと記述）、外部ライブラリの選択基準とライセンス、Mavenを例にしたビルドツールの役割（Gradleの概要を含む）、依存関係とバージョン管理の5項目
    + 必須として第4章・第13章・第14章とHTMLの基礎、推奨として第21章・JSON・開発プロセス
- Javadocによるドキュメント（L32–166）
    + Javadocコメントの書き方: `/** */`の形式（脚注でJava 23のMarkdownコメント）と、addメソッドに付けたJavadoc例（22-1）
    + 主要なJavadocタグ: @param、@return、@throws、@author、@version、@since、@see、@deprecatedの表と、インラインタグ（`{@code}`・`{@link}`）、HTMLのタグ（`<p>`・`<pre>`）、`@deprecated`と`@Deprecated`の違い
    + 実践的なJavadocの例: Taskクラスに網羅的なJavadocを付けた例（22-2）。非推奨の例は独自のメソッド`getSummary()`
    + Javadocの生成方法: javadocコマンドのオプションとIntelliJ IDEAの手順（`Tools` → `Generate JavaDoc...`）
- 外部ライブラリの利用（L167–825）
    + オープンソースエコシステムの歴史（年表）、ライブラリ利用の技術的利点（再利用、品質、開発速度、標準化、保守）
    + クラスパスとは: JVMがクラスを探す場所としての定義、JARファイル、`-cp`の使い方と区切り文字。JARの作り方は第23章へ誘導
    + 依存関係管理ツール: Mavenの設計原則4つ、`pom.xml`に書くだけで依存ライブラリのダウンロードとクラスパスの設定を行うことと図（chapter22-maven-dependency）、依存関係解決の手順（nearest wins、スコープ）、pom.xmlへのGsonの記述（22-3）
    + Gson: IntelliJ IDEAで「Load Maven Changes」を押して読み込み直す手順、@SerializedName、transientによる除外、GsonBuilderによる整形出力（22-4）
    + Jackson - 多機能なJSONライブラリ: Jackson 3（`tools.jackson`）と本書が使うJackson 2の系列の違い（脚注）、依存関係（22-5）、@JsonProperty、@JsonIgnore、TypeReferenceによるジェネリック型のデシリアライズ（22-6）
    + Apache Commons: Commons Lang（22-7、22-8。文字列、null安全、配列、ランダム文字列、日付の書式化、reflectionToString）とCommons IO（22-9、22-10。第14章の`Files`との比較、temp_dirのサイズなど実行ごとに変わらない出力）
    + そのほかのよく使われるライブラリ: OkHttp・Lombok・Guavaの役割と標準APIとの関係（第16章の`HttpClient`、第8章のRecord、`List.of`・`String.join`）の表。演習では使わない。使用例は付録G
    + 外部ライブラリ選定の指針: 成熟度と信頼性、ライセンス（22-11、Apache 2.0・MIT・GPL・LGPL。MITは著作権表示とライセンスの文を残せば改変・商用利用ができると説明）、testスコープとpropertiesによるバージョン一元管理（22-12。JUnitは第21章と同じ6.1.3）、OWASP Dependency-Checkの説明と`mvn verify`での実行、NVDのAPIキー（脚注）（22-13）、よく使われる組み合わせの表（Spring Boot 4に合わせ`spring-boot-starter-webmvc`とJackson 3の`tools.jackson.core:jackson-databind`。その理由を表の後に書く）
    + 実践的な開発フロー: `archetype:generate`（`-DarchetypeVersion=1.5`と`-DinteractiveMode=false`の理由、`maven.compiler.release`を25にする、または`-DjavaCompilerVersion=25`を付ける）、`mvn compile`、`mvn package`、dependency:tree/analyze、versions、purge-local-repository
    + もう1つのビルドツール：Gradle: build.gradle/build.gradle.kts、Gsonの依存関係（22-14）、implementation/testImplementation、Gradle Wrapper、gradlewのコマンド、付録D「高度なビルドとデプロイメント技術」への誘導
- よくあるエラーと対処法（L826–849）
    + 本文に挙げるエラー: Javadocの生成エラー、依存関係の競合（dependency:treeとexclusions）、バージョン管理の問題（22-12・22-13を参照）、APIの非互換性、クラスパスの問題、設定ファイルの読み込みと型変換、ライブラリの組み合わせ。詳細は`appendix/e-common-errors/chapters/chapter22.md`
- まとめ（L850–902）
    + 本章で学んだ技術の核心: Javadoc、外部ライブラリ、依存関係管理（Gradleを含む）、エコシステム
    + ドキュメント化の技術的価値（効果を4つ挙げる）
    + 依存関係管理の進化: 手動での管理 → MavenやApache Ivyによる自動化 → Gradleによる柔軟な設定の3段階（年代は書かない）と、いまはMavenとGradleがともに広く使われていること
    + セキュリティとコンプライアンス: 脆弱性管理（OWASP Dependency-Check）、ライセンス遵守、サプライチェーン、利用状況の把握（ライブラリ・バージョン・ライセンスの一覧管理）
- 章末演習（L903–931）
    + リンクは`exercises/chapter22/`。`documentation/`と`api-design/`の2つの題材に分かれ、それぞれ基礎・発展・チャレンジと解答例がある。第23章への導線

## 他章との関係

- 第16章（ネットワーク）: 16-11がJacksonを使い、本章へ誘導する
- 第21章（ユニットテスト）: JUnit・Mockitoの導入を本章へ誘導する。22-12の前でJUnit・Mockitoをtestスコープで加えると述べる
- 第8章（Record）・第16章（`HttpClient`）: そのほかのライブラリの表でLombok・OkHttpとの関係として触れる
- 付録G（外部ライブラリの使用例）: OkHttp・Lombok・Guavaの使用例を扱う
- 第23章（ビルドとデプロイ）: クラスパスの節からJARの作り方へ誘導する。第23章の「外部ライブラリを使うアプリケーションのJAR」が本章のGsonを例に`Class-Path`を説明し、まとめが`mvn package`に触れる
- 付録D（高度なビルドとデプロイメント技術）: Maven/Gradleによる自動化を扱う。Gradleの節から誘導する
- 第14章（ファイルI/O）: Gsonのtransientの脚注と、Commons IOの説明（`Files.readAllLines`・`writeString`との比較）で参照する

## 校正で見つけた個別の問題

- 22-2と22-4で別内容の`Task`クラスが同名で登場する（2026-11-16に22-4のクラスを`TodoItem`に改名して解消）
- 22-2の全文で、存在しない`setCompleted`を`@see`で参照していた問題は、メソッドを加えて解消した（`javadoc -Xdoclint:all`で警告なし）（2026-11-14に解消）
- 22-2の`toString`に`@Deprecated(forRemoval = true)`を付けていた例、22-8・22-10の実行ごとに変わる出力（ハッシュ値、カレントディレクトリのサイズ）、22-17の実行結果のGuava版Optional、Commons IOの「標準APIでは何行も必要」という記述、22-8の説明の「数値ユーティリティ」、Mavenの基本の段落がアルゴリズムの後にある順序、`mvn clean install`の説明、まとめのJPMS・GDPR・SOC2、「同期・GET/POST」の表記は、2026-11-14に解消した
- L34–35、L200–201ほか: 文の途中で改行して読点で行を分けている（紙面への影響はない）（2026-11-16の校正で解消）
- 2026-11-12以前の個別の問題（小見出しのjavac、22-1の不適切な例、TaskManagerの参照、空行、言語指定、bashへの番号、重複した導入、古い版の表、項目名＋コロン、Gsonの実行結果、Mavenの探索の記述、ライセンスのリスト、エラー集）はすべて解消済み
- 2026-11-16: 付録の再編（付録D・Eを本文へ移し、残りをA〜Fに振り直した）に合わせ、付録の文字とURLを直した（付録Hを付録F、付録Fを付録D、付録Gを付録Eに。h-common-errorsはf-common-errorsに）
- 2026-11-16: java.timeを第6章へ編入して付録Dを削除し、付録を振り直した（付録Eを付録D、付録Fを付録Eに。f-common-errorsはe-common-errorsに）
- 2026-11-16: OkHttp・Lombok・Guavaの節（旧22-11〜22-17、約350行。演習でも後の章でも使わない）を付録G（`appendix/g-library-examples/`）へ移し、本文は役割と標準APIとの関係の表にした。サンプル番号は22-11〜22-14に詰めた（第26章と付録Eの参照も直した）。Gson・Jackson・Apache Commonsは演習で使うので本文に残した
- 2026-11-18の校閲: 「仕組み」を「しくみ」、「ファイル入出力」を「ファイルI/O」、小見出し「外部ライブラリの利用例（Gson）」の括弧を全角に、Jacksonの小見出しを「多機能なJSONライブラリ」に、座標の読点を全角に直した。依存関係管理の進化の年代（1995-2002など）を消し、段階の説明に書き換えた。`-DarchetypeVersion`を指定しない場合の説明を「JUnit 3を使う古いひな形が使われ`mvn package`のテストで失敗する」から「環境によっては古い版のひな形が使われ、できる`pom.xml`の中身が本書の説明と変わる」に直した（この1文は索引の再生成前の変更のため、「外部ライブラリの利用」のハッシュは未更新）
