---
source: manuscripts/chapter22-documentation-and-libraries.md
summarized: 2026-11-08
sections:
  - key: "本章の学習目標"
    hash: 2bedf770
  - key: "Javadocによるドキュメント"
    hash: 99979036
  - key: "外部ライブラリの利用"
    hash: 31b3abe2
  - key: "よくあるエラーと対処法"
    hash: c0a809de
  - key: "まとめ"
    hash: a6941e3c
  - key: "章末演習"
    hash: 19564110
---

# 第22章 ドキュメントと外部ライブラリ 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter22-documentation-and-libraries.md` を参照する。

## 章の概要

- 前半はJavadoc（コメントの書き方、主要タグ、生成方法）、後半は外部ライブラリの利用（クラスパス、Mavenによる依存関係管理、代表的なライブラリの紹介、選定指針）を扱う
- 紹介するライブラリはGson、Jackson、Apache Commons Lang/IO、OkHttp、Lombok、Guavaの6種で、それぞれpom.xmlの依存関係と使用例・実行結果を示す
- 章末に約630行の「よくあるエラーと対処法」があり、Javadoc生成、依存関係の競合、バージョン管理、API非互換、クラスパス、設定ファイル、ライブラリの組み合わせの7分類を扱う
- 前提は第4章（クラス）、第13章（例外処理）、第14章（ファイルI/O）。第21章（ユニットテスト）は推奨扱い
- ビルドツールはMavenだけを扱い、Gradleは名前と年表に出るだけである

## 構成上の問題

- Gradleを予告しているのに扱っていない
    + 学習目標（L11）と「この章で学ぶこと」（L21）で「Maven、Gradleなどのビルドツールの役割と概念」を掲げるが、本文はMavenのみ。build.gradleの例は1つもない
    + 第23章（L37）と付録G（前提知識）は「第22章でMaven、Gradleの基礎を学んだ」ことを前提にしているため、後続章との整合も崩れている
- 前提知識の矛盾
    + 推奨知識に「ビルドツール（Maven、Gradle）の基本概念」（L48）があり、本章で教える内容を前提にしている
    + 第21章は推奨知識に「第22章の外部ライブラリ活用（Maven、Gradle）」を挙げ、本章は推奨知識に第21章を挙げる。相互参照で章順と矛盾する（JUnitの導入にMavenが必要なら、Mavenの最小限の説明を第21章より前に置く必要がある）
- 学習目標と「この章で学ぶこと」の重複は2026-11-08に解消した（散文の言い直しと根拠のない数値を削除）。末尾も「よくあるエラー → まとめ → 章末演習」の順に並べ替えた
- 章内の内容の重複
    + 「外部ライブラリの利用」末尾の「トラブルシューティング」（L1627–1658、22-23）と「よくあるエラーと対処法」の「依存関係の競合」（22-30〜22-33）が、バージョンの明示指定と推移的依存関係の除外を二重に説明している
    + 「外部ライブラリ選定の指針」のバージョン一元管理（22-20）・脆弱性チェック（22-21）と、エラー集の「ライブラリのバージョン管理」（22-34〜22-37）も重なる
- 実行結果がサンプルコードと対応していない
    + 6ライブラリすべての実行結果が「〇〇 ライブラリ使用例（デモンストレーション）」という見出しで始まり、掲載コードとは別のデモ用コードの出力と見られる
    + 2026-11-08に、Lombok（22-15）以外は全文を実行した出力に置き換えた（22-9、22-13は一例）。その際、Gsonの例（すべてのフィールドが除外されていた）とJacksonの例（attributesを読み込めなかった）のコードも直した
- 「よくあるエラーと対処法」の分量
    + 本文の外部ライブラリ節（約1360行）に加えて約630行あり、章全体の4分の1を占める。設定ファイルの読み込み（Properties）など、外部ライブラリと関係の薄い項目もある

## 節ごとの要約

- 本章の学習目標（L3–31）
    + Javadoc、外部ライブラリの選択基準とライセンス、Maven/Gradleの役割、依存関係とバージョン管理の5項目
    + 2026-11-08に、別のH2だった「この章で学ぶこと」を学習目標の下の小見出しにまとめ、学習目標を散文で言い直した部分を削除した
    + 学習目標の4項目めを「Mavenを例にしたビルドツールの役割と概念」に改め、推奨知識からビルドツールを外した（本章で教える内容のため）
    + 必須として第4章・第13章・第14章とHTMLの基礎、推奨として第21章・JSON・開発プロセス
- Javadocによるドキュメント（L32–164、22-1〜22-3）
    + Javadocコメントの書き方（L60–87、22-1）
        * `/** */`の形式と、addメソッドに付けたJavadoc例
        * int引数に「nullを許容せず」と書く、メソッドに`@author`を付けるなど、例として不適切な記述がある
    + 主要なJavadocタグ（L88–102）
        * @param、@return、@throws、@author、@version、@since、@see、@deprecatedの表
        * 22-2で使う`{@code}`、`{@link}`、`<pre>`などのインラインタグは説明がない
    + 実践的なJavadocの例（L103–271、22-2）
        * Taskクラス（Priority列挙型、検証付きコンストラクタ、@Deprecated(forRemoval = true)のtoString）に網羅的なJavadocを付けた例
        * クラスコメントの`@see TaskManager`は本文中に存在しないクラスを参照する
    + Javadocの生成方法（L272–297、22-3）
        * javadocコマンドのオプション（-d、-sourcepath、-encoding/-charset、-windowtitle）とIntelliJ IDEAの手順
        * 小見出しが「コマンドライン (javac)」で、実際のコマンド（javadoc）と食い違う

- 外部ライブラリの利用（L165–1087、22-4〜22-23）
    + オープンソースエコシステムの歴史（L302–316）
        * Apache財団設立、Apache Commons、Maven Central、Spring、GitHubの年表
    + ライブラリ利用の技術的利点（L317–333）
        * 再利用性、品質、開発速度、標準化、保守性の5点
    + クラスパスとは（L334–339）
        * JVMがクラスを探す場所としての定義のみ。-cpオプションの使い方は本文になく、エラー集（22-44、22-45）で初めて出る
    + 依存関係管理ツール：Maven（L340–396、22-4）
        * Mavenの設計原則（Convention over Configuration など）、依存関係解決の手順（番号なしのテキストブロック）、pom.xmlへのGsonの記述と座標（groupId、artifactId、version）
        * pom.xmlの全体構成、ディレクトリ規約、ライフサイクル（compile、test、package）、mvnコマンドの基本は説明しない
    + 外部ライブラリの利用例 (Gson)（L397–534、22-5）
        * @SerializedName、transientによる除外、GsonBuilderによる整形出力、コレクションのシリアライズ、テキストブロックからのデシリアライズ
    + Jackson - 高性能JSONライブラリ（L535–710、22-6、22-7）
        * ObjectMapper、@JsonProperty、@JsonIgnore、TypeReferenceによるジェネリック型のデシリアライズ
    + Apache Commons - ユーティリティライブラリ集（L711–988、22-8〜22-11）
        * Commons Lang（StringUtils、ArrayUtils、RandomStringUtils、ToStringBuilder/EqualsBuilder/HashCodeBuilder）とCommons IO（FileUtils、IOUtils、FilenameUtils）
    + OkHttp - モダンなHTTPクライアント（L989–1148、22-12、22-13）
        * 同期GET/POSTと非同期リクエスト、タイムアウト設定
    + Lombok - ボイラープレートコードの削減（L1149–1304、22-14〜22-16）
        * @Data、@Builder、@NonNull、@ToString.Exclude、@Slf4j、個別アノテーション（@Getter/@Setterなど）
    + Guava - Google製のコアライブラリ（L1305–1463、22-17、22-18）
        * 不変コレクション、Multimap、BiMap、Table、Splitter/Joiner、キャッシュ
    + 外部ライブラリ選定の指針（L1464–1605、22-19〜22-21）
        * 成熟度と信頼性、ライセンス（Apache 2.0、MIT、GPL、LGPL）、propertiesによるバージョン一元管理とtestスコープ、OWASP Dependency Check、よく使われる組み合わせの表（2026-11-08にpom.xmlから表へ変更）
        * 冒頭で「機能性、信頼性、保守性、ライセンスの4つの観点」と述べるが、小見出しの構成と対応していない
    + 実践的な開発フロー（L1606–1626、22-22）
        * mvn archetype:generate、clean install、dependency:tree、dependency:analyze、versions:display-dependency-updates
    + トラブルシューティング（L1627–1658、22-23）
        * バージョンの明示指定、exclusionsによる除外、ローカルリポジトリのクリア

- よくあるエラーと対処法（L1088–1714、22-24〜22-52）
    + Javadocの生成エラー（L1741–1874、22-24〜22-29）
        * クラスコメントでの@param/@returnの誤用、日本語の文字化け（-encoding、Mavenの設定）、HTMLタグの誤用
    + 依存関係の競合（L1875–1972、22-30〜22-33）
        * jackson-coreとjackson-databindのバージョン不一致によるNoSuchMethodError、dependency:treeでの推移的依存関係の確認とexclusions
    + ライブラリのバージョン管理（L1973–2043、22-34〜22-37）
        * 脆弱性のある古いバージョンの更新、上限なしのバージョン範囲の危険性
    + APIの非互換性（L2044–2106、22-38〜22-41）
        * Jacksonのメジャーバージョン変更による例外処理の追加、非推奨の`Date`コンストラクタから`LocalDate`への置き換え
    + クラスパスの問題（L2107–2203、22-42〜22-45）
        * NoClassDefFoundErrorの再現（実行結果もデモ形式）、コンパイル時と実行時のクラスパスの違い、実行可能JAR
    + 設定ファイルの問題（L2204–2289、22-46〜22-50）
        * application.propertiesをクラスパスから読み込む方法、設定値の型変換とデフォルト値
    + ライブラリの組み合わせ問題（L2290–2362、22-51、22-52）
        * SLF4Jの複数バインディングとexclusionsでの解消、JSONライブラリをJsonUtilに集約して統一する方法

- まとめ（L1715–1768）
    + 本章で学んだ技術の核心（L1661–1671）
        * Javadoc、外部ライブラリ、依存関係管理、エコシステム。「文芸的プログラミング」は本文に出てこない用語
    + ドキュメント化の技術的価値（L1672–1684）
        * 「研究によると」として並べていた出典のない数値（保守コスト40–60%削減など）は、2026-11-08に削除し、効果だけを挙げる形にした
    + 依存関係管理の進化（L1685–1697）
        * 手動JAR → Maven/Ivy → Gradle → JPMSの世代区分。Gradle・JPMSは本文で扱っていない
    + セキュリティとコンプライアンス（L1698–1712）
        * 脆弱性管理、ライセンス遵守、サプライチェーン、GDPR・SOC2

- 章末演習（L1769–1789）
    + 演習課題へのアクセス（L1715–1718）
        * リンクは`exercises/chapter22/`で、章番号と一致している
    + 課題構成（L1719–1736）
        * 基礎（Javadoc生成）、発展（Mavenプロジェクトと外部ライブラリ）、チャレンジ（複数ライブラリの組み合わせ）。第23章への導線

## 他章との関係

- 第21章（ユニットテスト）: 相互に推奨知識として参照しあっている（構成上の問題を参照）。22-20でJUnitの依存関係をtestスコープで示す
- 第23章（ビルドとデプロイ）: 前提知識に「第22章（Maven、Gradle）」とあるが、本章はGradleを扱わない。実行可能JARは22-45でも触れる
- 付録G（高度なビルドとデプロイ）: Maven/Gradleによる自動化を扱い、前提に「第22章のMaven、Gradleの基礎」を挙げる。Maven・Gradleの説明の分担（本章は依存関係管理、付録Gはビルド自動化）を明確にする必要がある
- 第16章（ネットワーク）: Java 11のHttpClientとJacksonを使ったJSON APIクライアントがある。OkHttp・Jacksonの紹介と重なるので、標準HttpClientとの比較や相互参照があるとよい
- 第8章（レコード）: データクラスのボイラープレート削減を扱う。Lombokの@Dataと役割が重なるが、本章からレコードへの言及がない
- 第9章（コレクション）・第6章（不変性）: `List.of`などの標準の不変コレクションと、Guavaの不変コレクションの関係に触れていない
- 第14章（ファイルI/O）: NIOのFilesと、Commons IOのFileUtilsの比較がない
- 付録F（Date and Time API）: 22-41の`LocalDate`変換と関連する

## 校正で見つけた個別の問題

- L274: 小見出し「コマンドライン (javac)」は「javadoc」の誤り
- L72、L81（22-1）: int引数に「nullを許容せず」、メソッドに@author。後のエラー例（22-24）でタグの誤用を戒めているのと矛盾する
- L1490–1493: 「Apache License 2.0: 商用利用可…」など、「項目名：説明」形式のリスト（階層化ルール違反）
- L1489「主要なオープンソースライセンス。」、L1547「…よく使われる組み合わせ。」、L1629「よくある問題と解決方法。」: 体言止めの導入文
- L1735「次のステップ：」: コロン付きの見出し風の文
- L292–296: 「#### IntelliJ IDEA」の直後に空行なしでリストが始まる
- 22-19（L1479）: 中身はpom.xmlのXMLなのに言語指定が`java`。22-22、22-23もmvnコマンドやXMLを`java`で囲っている
- 22-3、22-22、22-26、22-27、22-32、22-44、22-45: コマンドライン操作の例に番号が付いている（番号付けルールでは付けない例）
- 22-5: `excludeFieldsWithoutExposeAnnotation()`を指定しつつ@Exposeを付けたのはinternalIdだけなので、実際には全フィールドが除外される。また`setDateFormat`は`LocalDateTime`に効かず、TypeAdapterなしでは`LocalDateTime`をシリアライズできない。実行結果の`"tags": [work, important]`も正しいJSONでない
- 22-2と22-5で別内容の`Task`クラスが同名で登場する
- 22-32: dependency:treeの出力例で、slf4j-apiがcommons-loggingに依存しているのは事実と異なる
- 22-38、22-39: Jackson 1.xと2.xはパッケージ名（org.codehaus.jackson と com.fasterxml.jackson）から異なるので、例として不正確。コメントは「コンパイルエラー」、エラーメッセージは実行時のNoSuchMethodErrorで食い違う
- L365–371: Mavenの依存関係解決手順に「深さ優先探索」とあるが、最短経路優先（nearest wins）の説明と合わない
