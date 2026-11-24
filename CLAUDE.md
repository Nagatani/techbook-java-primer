# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Japanese technical book project titled "Java入門" (Java Introduction) by Hidehiro Nagatani. The project uses Vivliostyle, a CSS-based typesetting system, to generate PDF publications from Markdown source files.

## Educational Philosophy and Target Audience

### Target Readers
This book is specifically designed for students who have learned basic procedural programming through C language and are ready to advance to object-oriented programming with Java.

### Educational Objectives
The book aims to provide comprehensive knowledge through Java programming, including:

1. **Programming History and Structure**: Understanding the evolution of programming paradigms and software development methodologies
2. **Problem-Solving Approaches**: Learning systematic approaches to software design and implementation challenges  
3. **Comprehensive Application Development**: Mastering full-stack application development techniques including:
   - Object-oriented design patterns
   - GUI programming with Swing/AWT
   - Database integration and file I/O
   - Testing methodologies (TDD, unit testing)
   - Documentation and API design
4. **Technical Communication**: Building foundational skills for effective technical communication and collaboration
5. **Java's Core Technologies**: Deep understanding of Java's underlying technologies and modern language features including:
   - JVM architecture and memory management
   - Lambda expressions and functional programming concepts
   - Concurrency and multithreading
   - Modern Java features (Records, Pattern Matching, etc.)

### Content Philosophy
**Important**: The book intentionally includes advanced technical concepts, programming history, and theoretical foundations. These elements are essential for developing well-rounded software engineers who understand not just "how" but "why" certain approaches are used in modern software development.

Rather than removing complex content, it should be strategically organized through:
- **Progressive disclosure**: Basic concepts first, advanced topics as supplements
- **Appendices and columns**: Deep technical content as optional reading
- **Contextual integration**: Advanced concepts introduced when directly relevant to practical applications

### Pedagogical Approach
The book follows a spiral curriculum model where concepts are introduced at basic level and revisited with increasing complexity throughout the learning journey. This approach ensures both accessibility for beginners and depth for serious study.

## Project Current Status (as of 2025-07-22, 一部2026-11-10更新)

### Completed Work and Established Policies

1. **Exercise Sections**: 各章の演習はGitHubリポジトリへのリンクのみで提供
   - 章内に演習セクションは意図的に設けない方針
   - リンク形式: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapterXX/`
   - 全26章でこの形式を採用済み

2. **Appendix Structure**: 付録はGitHubリポジトリへの参照として実装
   - 付録B: GitHubリンク形式のインデックスとして完成
   - 付録A〜H（2026-11-16に再編）: A 開発環境の構築、B 技術的な詳細解説（Deep Dive）、C ソフトウェア工学の理論的基盤、D 高度なビルドとデプロイメント技術、E Javaのよくあるエラーと対処法、F Swingの発展的な機能、G 外部ライブラリの使用例、H 並行処理とネットワークの発展的なパターン。GitHubのディレクトリは`appendix/a-environment-setup`・`bXX-*`・`c-theoretical-foundations`・`d-build-and-deploy`・`e-common-errors`・`f-swing-advanced`・`g-library-examples`・`h-advanced-patterns`
   - 紙面の付録のページは「付録Xについて／内容／関連する章／オンライン資料」の形にそろえ、問い合わせ先は付録一覧（`appendix-index.md`）の1か所だけに置く。中身のない一般論の節（「学習のポイント」「実践への応用」）は置かない
   - 本文に入れた旧付録: 統合プロジェクト（旧D）は第25・26章、データベース（旧E）は第24章、java.time（旧F）は第6章の節「日付と時刻（java.time）」（その後ろに「数値の正確な計算（BigDecimal）」がある）（本書に必要な範囲に絞った。詳しい資料は文字のない`appendix/java-time-api/`に残し、第6章の脚注から案内する）
   - 本文内に詳細な付録内容は記載しない

3. **Writing Style Decisions**:
   - 本文: ですます調で統一
   - リスト項目: である調を使用（意図的な使い分け）
   - 専門用語: 「インターフェイス」で統一（「インターフェース」ではない）
   - 外来語の長音（2026-11-11〜）: JIS Z 8301:2019（旧規定の「3音以上の語は語末の長音を省く」は削除され、内閣告示「外来語の表記」による）に従い、英語の語末の-er・-or・-arに当たる語は長音符号を付ける（コンストラクター、パラメーター、コンパイラー、サーバー、リスナー）。語末が-yの語は付けない（メモリ、ディレクトリ、ライブラリ、ファクトリ）。`prh-rules.yml`に語を登録し、`npm run lint`（textlintのprhルール）で検査する。新しい語が出てきたら`prh-rules.yml`に足す。コードの識別子・コメントと図の元データ（`docs/diagrams/*.mmd`）はtextlintの対象外なので、図の文字は手で合わせる

4. **Code Listing Numbers**: 
   - 全26章で付与済み。各章で出現順に1から連番（2026-11-07に欠番・重複・旧章番号を解消）
   - 形式: **サンプルコードX-Y** で統一（Xはファイル名の章番号と一致させる）
   - 言語指定のあるコードブロック（bash・mermaidなどを除く）はすべて番号を付ける。コードブロックを足したら`node scripts/sample-code.js number`で番号を振り直す（2026-11-08に番号のない78件を解消）

6. **Sample Code Excerpts（2026-11-08〜）**: ページ数削減のため、サンプルコードは要点だけを本文に載せ、コンパイル・実行できる全文はGitHubの`sample-code/`へ誘導する
   - 全文は`sample-code/chapterXX/XX-YY/`（サンプルコードX-Yに対応）。本文に載せる範囲は`sample-code/chapterXX/listings.json`にファイル名と行番号で記録する
   - **本文のコードブロックとキャプション（「サンプルコード5-3　Shape.java（12〜30行目）」）は全文から生成する。本文のコードを直接編集せず、全文を編集してから`npm run samples:sync`する**
   - 範囲のあいだを省いた箇所は`// ...`の行になる（詳細は`sample-code/README.md`）。PDFでは範囲が2つ以上のサンプルを範囲ごとの枠に分け、枠の頭に「ファイル名　N〜M行目」の見出しを、各行の左に全文での行番号を付ける。範囲が1つのサンプルは枠を分けず行番号だけを付ける。全文と対応づけられないブロック（ファイル全体を載せたもの、`listings.json`にないもの）も1から行番号を付け、言語を指定したコードブロックはすべて行番号付きにそろえる（言語を指定していない実行結果とbashには付けない）（2026-11-17〜、ビルド時に`scripts/vfm-code-segments.js`が`listings.json`を読んで行い、`// ...`の行と空行は取り除く。本文と`listings.json`が食い違うサンプルは分けない）
   - 本文で省いたimportや補助クラスは全文側に足してよい（本文に載らない範囲に置く）
   - コードの1行は紙面の幅（行番号を付けた状態でASCII 73桁）に収める（2026-11-17に385行を改行）。行末のコメントは上の行に移し、コードは括弧の浅いところの「,」の後ろや「+」「.」の前で切って続きを8文字下げる。`npm run check:code-wrap`で折り返す行がないことを確かめる
   - 外部ライブラリを使うサンプルは`listings.json`の`libs`にMaven Centralの座標を書く（検査時に`.cache/sample-code-libs/`へ取得）
   - 対象はJava 25。`check:samples`は`--release 25`でコンパイルし、非推奨・削除予定のAPIの使用も問題として報告する
   - 公開するコード（`sample-code/`、`appendix/`、`exercises/`の解答例）は、Java 25でコンパイル・実行できる状態を保つ。実行結果がOSで変わる書き方（`Files.list()`の順序、現在時刻のナノ秒など）は避ける。pom.xmlやbuild.gradleはJava 25・Gradle 9で動く設定にする
   - 付録・演習のコードは`npm run check:public-code`でコンパイルとテストを確かめる（2026-11-11〜、CIはコンパイルを失敗扱い）。Maven・Gradleのビルド、GUIの起動、READMEの手順どおりの実行は、DockerのJava 25で手動確認する（手順と、これまでに見つかった誤りのパターンは`docs/guides/code-verification.md`）

7. **よくあるエラーと対処法（2026-11-09〜）**: 各章末の「よくあるエラーと対処法」は、本文には出会いやすいエラーと原因の要点だけをリストで残し、エラーメッセージの例・修正コード・デバッグのヒントは`appendix/e-common-errors/chapters/chapterXX.md`で提供する
   - 本文の節の末尾にGitHubのURL（`https://github.com/Nagatani/techbook-java-primer/blob/main/appendix/e-common-errors/chapters/chapterXX.md`）を置く
   - GitHub側のコードには番号を付けない。コンパイルできる全文は`chapters/chapterXX-code/`に置く
   - 第25章（統合プロジェクトの設計）だけは章ごとのファイルを作らず、本文の「設計でよくある失敗」で扱う。エラーメッセージは実際に再現して写す（GUIのないLinuxの表示などはDockerのeclipse-temurin:25-jdkで確かめる）

9. **標準入出力はIOクラス（2026-11-10〜）**: Javaのコードの標準入出力は、Java 25の`java.lang.IO`（`IO.println`・`IO.print`・`IO.readln`）で書く
   - 本書で使う旨は第1章「画面に文字列を出力する方法」とコラム「System.out.printlnとIOクラス」に書いた。本書が使うJDKはOpenJDK 25
   - `System.out.printf(書式, 値)`は`IO.println("書式".formatted(値))`（末尾の`%n`は取る）にする。キーボードからの入力は`Scanner(System.in)`ではなく、`IO.readln(メッセージ)`で1行を読み`Integer.parseInt`などで変換する。入力の終わりでは`IO.readln`が`null`を返す
   - `System.err`（IOクラスにない）と、出力先を`PrintStream`として受け渡す処理は`System`クラスのまま。文字列やファイルを区切って読む`Scanner`は使ってよい
   - `IO.println`の引数は`Object`なので、`char[]`を渡すと中身ではなく参照が出る（`new String(配列)`にする）
   - `IO::println`は静的メソッド参照になる（`System.out::println`は特定のインスタンスのメソッド参照）。第11章のメソッド参照の表はこれに合わせてある
   - 付録・演習のコードも移行済み。`IO.readln`は最初に使った`System.in`の読み取り器を持ち続けるため、テストで`System.setIn`を何度も差し替える処理とは組み合わせない（いまの演習・付録にはない）
   - `IO`という名前のクラスを自作した例（付録B.02のIOモナド）では、自作のクラスが優先される。その中で標準入出力を使うときは`java.lang.IO`と書く
   - 付録Aの環境構築手順もJDK 25（SDKMANの識別子`25.0.5.0+2-ms`、2026-11時点）に書き直した。SDKMANの識別子は更新のたびに変わるので、手順を見直すときは`sdk list java`の実際の表示で確かめる

10. **プログラムはクラスを宣言して書く（2026-11-10〜）**: Java 25のコンパクトソースファイル（クラス宣言なしで`void main()`だけを書く）とインスタンスmainメソッドは、第1章「Java 25の新しい書き方：クラス宣言を省いたプログラム」（サンプルコード1-7〜1-9）で紹介するだけにする
   - 同じ節の「本書での書き方」に、本書はクラスを宣言してプログラムを組み立てる旨と理由を書いた。サンプル・付録・演習のコードは、クラスを宣言し`public static void main(String[] args)`から実行する形で書く
   - モジュールのimport宣言（`import module java.base;`）も紹介だけにとどめ、コードではクラスごとにimport文を書く
   - `check:samples`と`check:java`は、クラス宣言のないファイル（ファイル名がクラス名になる）と`void main()`も実行の対象として見つける

11. **図（2026-11-11〜）**: 図はMermaidで書き、元データを`docs/diagrams/chapterXX-内容.mmd`（XXは載せる章の番号）に置く。`npm run mermaid:convert`（`build`から呼ばれる）が`manuscripts/images/diagrams/同じ名前.svg`を作り、原稿からはそのSVGを`<div class="figure">`の中で参照する
   - 原稿に```mermaidのブロックを直接書かない（変換スクリプトがエラーにする）。以前は原稿の中のブロックを画像に置き換えて原稿を書き換えていたため、元データが残らなかった。本で使わない図の元データは`docs/diagrams/unused/`に置く
   - Mermaidの設定は`docs/diagrams/mermaid-config.json`。`useMaxWidth: false`で図を描く幅に合わせて縮めないようにし、図形どうしの間隔を詰めている
   - 図は横幅いっぱいに広げない。ビルド時に`scripts/vfm-figure-size.js`が図の元の大きさから幅と高さを決めて`<img>`に書き込む（計算は`scripts/diagram-size.js`）。図の中でいちばん小さい文字が本文と同じ10Qになる倍率で載せ、図の幅の上限（130mm。本文の幅132mmより少し狭い）とページに収まる高さ（150mm）を超える図だけを縮める（2026-11-11に9Qから10Qへ）。いちばん小さい文字の大きさは`scripts/diagram-metrics.js`がブラウザで測り、`docs/diagrams/.diagram-metrics.json`に書く（`mermaid:convert`が変換のあとに呼ぶ）。テーマが図の画像の左右に付ける余白は`--vs--figure-item-padding-inline: 0`で消している
   - `npm run check:diagrams`（CI）が元データ・画像・原稿の対応を検査し、次のものを「注意」として示す。注意が出たら図を直す: 縮めた結果、文字が9Q未満になる図、線が文字の上を通る箇所、文字が箱からはみ出す箇所、ラベルどうしが重なる・接する箇所（8px未満）。検査で拾えない読みにくさ（線どうしの交差など）は、PDFで目で確かめる
   - シーケンス図のメッセージなど、複数の要素をまたぐ文字には白い縁取り（`mermaid-config.json`の`themeCSS`、`paint-order: stroke`）を付けて、下を通る線を隠している。注釈（note）は背景があるので縁取りしない。2人の参加者にまたがる注釈は箱がその間の幅にしか広がらないので、長い注釈は全員にまたがらせるか短くする
   - 同じ2つのノードの間に行きと帰りの線を引くとラベルがぶつかる。帰りの線はラベルなしにして、条件をノードの中に書く（chapter15-thread-lifecycle）。状態遷移図（stateDiagram）はラベルが重なりやすいので、フローチャートで描く
   - クラス図の注釈（`note for`）の改行は`<br/>`で書く（`\n`はそのまま表示される）
   - 横に広くなる図の直し方（2026-11-11に12枚で使った）: 階層図は`direction LR`で兄弟を縦に積む。図の目的に要らないメンバーは省く。1つのノードから多数へ引く線は、サブグラフへの1本や注釈にまとめる。図ごとの設定はファイル先頭のfrontmatter（`---\nconfig:\n  flowchart:\n    rankSpacing: 20\n---`）で上書きできる。サブグラフの中のつながっていないノードを縦に並べるには、見えない線（`A ~~~ B`）でつなぐ
   - 版型はISOのB5（176×250mm）。本文の幅は132mm
   - ガントチャートは文字の既定が約11pxで、`check:diagrams`の前提（16px）と合わない。frontmatterで`gantt: { fontSize: 16, sectionFontSize: 16, useWidth: 900 }`を指定する（目盛りは`themeCSS`で大きくする。chapter15-chef-timeline）。ブロック図（`block-beta`）は格子の配置（BorderLayoutなど）に使える
   - 枠（subgraph）の外へ線を引くと、枠の中の`direction`が効かず、枠どうしが重なることがある。そのときは枠をやめるか、線を枠そのものに引く

12. **技術を正式に説明する章（2026-11-11〜）**: 同じ技術を複数の章で説明しない。その技術を説明するのにふさわしい章に解説をまとめ、ほかの章で先に使う・後で使うときは「第N章で学びます」「第N章で学んだ〜」と誘導する
   - クラス・フィールド・メソッド・コンストラクターとgetter/setterの基本・public/private・static・オブジェクトの配列は第3章。アクセス修飾子の全体・パッケージ・this・オーバーロードの設計は第4章
   - 継承・オーバーライド・ポリモーフィズム・instanceof・`Object`クラスのメソッド（toString・equals・hashCode）は第5章。不変オブジェクト・final・防御的コピー・ビルダーと日付と時刻（`java.time`）は第6章。enum・Record・sealed・内部クラス・匿名クラスは第8章
   - コレクションとその選び方・ラッパークラスとオートボクシングは第9章、ジェネリクスとvarは第10章、ラムダ式・メソッド参照・Comparatorは第11章、Stream・Optional・並列ストリームは第12章、例外処理・try-with-resourcesは第13章
   - GUIの部品とレイアウト（invokeLaterの定型を含む）は第17章、イベント処理の基本・EDT・javax.swing.Timerは第18章、マウス・キーボードの詳細とSwingWorkerは第19章
   - 本文から付録へ移したもの（2026-11-16）: 基礎課題で使わないSwingの発展的な機能（PropertyChangeSupport・ドラッグ&ドロップ・デバウンス・JTreeの動的な操作・カスタムセルエディター）は付録F、演習で使わない外部ライブラリ（OkHttp・Lombok・Guava）は付録G、演習で使わない並行処理とネットワークのパターン（第13章のサーキットブレーカー、第15章のFork/Joinの比較・シングルトン・パイプライン、第16章のNIO）は付録H。本文にはAPIの要点を数文残し、演習のREADMEから付録へ案内する。演習の基礎課題で使う部品とイベントは本文から移さない
   - 後の章で学ぶ機能は、コードを動かせばわかる程度なら先に使ってよい。説明なしでは難しいもの・読み解きの邪魔になるものは別の書き方にする（例: ラムダ式の代わりに拡張for文、ジェネリクスの代わりに配列）。使い続ける場合は初出に最小限の読み方と誘導を置く

13. **脚注（2026-11-12〜）**: 本文を補う内容は`<span class="footnote">…</span>`で脚注にする（PDFではページの下端に出る）。書き方と何を脚注にするかは`docs/guides/writing-guidelines-textlint-compliance.md`の11節
   - GitHubの場所だけを書く箇所には、URLの全文を脚注で添える（インラインコードで書く）。先頭に「※」を付けない。ですます調
   - 見出し・表・コードブロック・キャプション・図・コラムの中には置かない。同じ余談は初出の章に1つだけ置く
   - 脚注の文字はtextlintで本文の文に数えられる（文の長さ・読点の数）。脚注を足したら`npm run lint`で確かめる
   - 外部URLへのMarkdownのリンク（`[文字](https://...)`）と「※」の注意書きは使わない。テーマのCSSがリンクのURLから作る脚注は、PDFでクリックできない。リンクの文字だけを本文に残してURLを脚注に書き、「※」の注記は脚注か本文の文にする（`check:compliance`が問題にする。2026-11-13）

14. **データベースと統合プロジェクトの章（2026-11-16〜）**: 旧付録Eの「データベースプログラミング」を第24章、旧付録Dの「統合プロジェクト（学生管理システム）」を第25・26章に移した
   - 第25章は設計（要求の整理、設計判断記録、継承と合成、計算で求められる値、層と依存の向き、例外の設計、段階的な計画）、第26章は技術選定（標準API・自作・ライブラリの比べ方、保存先・CSV・GUI・ログ・テストの事例）と、初版から改善版へ移す手順・振り返り
   - 第24章はH2（2.5.252）で、SQLの基本・JDBC・PreparedStatement・DAO・java.time・トランザクション・HikariCP・バッチを扱う。演習は`exercises/chapter24/`（旧`appendix/e-database-programming/exercises/`）
   - 統合プロジェクトの6フェーズのコード（`sample-code/integrated-project/`、旧`appendix/d-integrated-project/`）は、各章の知識だけで書いた「初版」として残し、第25章がその設計の問題を事例にする。`check:public-code`の対象に加えてある
   - 改善版のコードは見本として`sample-code/chapter25/`・`chapter26/`に置く（アプリケーション全体の書き換えは演習の課題）。チャレンジ課題はコードの解答例を作らず、方針（`solutions/challenge-guide.md`）だけを置く（著者の指示「完全な回答例はほどほどでよい」）
   - ライブラリの版・公開日・ライセンス・依存・大きさを本文に書くときは、Maven Centralのpom.xmlとJARで確かめ、確かめた日を要約に残す。技術の選定の観点の正式な説明は第22章で、第25・26章はそれを適用する（方針12）

15. **本文と付録B（Deep Dive）の主張の検証（2026-11-19〜）**: 本文の事実の主張は`docs/claims/chapterXX.yml`に本文の文を`text`で引用して確かめ方を書く（原稿に印を置かない。`check:claims`）。 付録Bの解説の事実を述べた段落には`<!-- claim: id -->`の印を付け、節の`claims.yml`に確かめ方を書く。JVMの内部の主張は出典より観察（`verify/`のプログラムを`javap`・`-XX:+PrintInlining`・`-Xlog`などで動かした実際の出力）で確かめ、解説に載せる出力は`check:deep-dive -- --update`で書き込む（手で書かない）
   - 出典は仕様・API・javaコマンドのリファレンス（docs.oracle.com、Java SE 25の版）とJEP（openjdk.org）。HotSpotの実装は仕様に書かれていないので観察で確かめる
   - 本文で意図して簡略化した説明（台帳の`simplified: true`）と、性能や設計の一般論（「volatileはsynchronizedより軽い」など）には、脚注で正確な事実か一般論である旨を書き、詳しい内容は付録へ案内してよい（2026-11-19、著者の指示）
   - JVMの中を見ずにそれらしい表や数値を出す「シミュレーション」のデモは、観察に置き換えるか、シミュレーションであることを明記する
   - 手順と、検証で見つかった誤りのパターンは`docs/guides/claim-verification.md`

16. **本書の既定の選択（2026-11-24〜）**: 同じ目的に複数の書き方・ライブラリがあるときに本書が使うものは`docs/guides/book-defaults.md`にまとめた（JSONはGson、独自例外は「利用者が対処できる誤りは検査例外」、Streamの結果は`toList()`、Swingは`JFrame`を継承せず持つ、など）。本文で選択肢を並べたら既定を言い切って理由を1文添え、コードもこれにそろえる

5. **Chapter Structure**:
   - 全26章構成（2026-11-16に第24章「データベースプログラミング」を実践編の最後に、第25章「統合プロジェクトの設計」・第26章「統合プロジェクトの技術選定と実装」を統合プロジェクト編として加えた）
   - 部の構成（2026-11-23に内容に合わせて改めた。目次`table-of-contents.md`だけにある）: 1部 基礎編（1〜4章）、2部 オブジェクト指向編（5〜8章）、3部 標準ライブラリと関数型編（9〜13章）、4部 入出力と並行処理編（14〜16章）、5部 GUI編（17〜20章）、6部 開発実践編（21〜24章）、7部 統合プロジェクト編（25・26章）
   - 第4章の章題は2026-11-23に「クラスとインスタンス」から「カプセル化とクラス設計」に改めた（ファイル名と演習のディレクトリは変えていない）
   - 第21章（ユニットテスト）の配置は現状維持
   - 各章末にGitHubリポジトリへの演習リンクを配置

8. **組版（テーマ）の方針（2026-11-09〜）**: 読みやすさを保ったままページ数を抑える
   - 章の扉は1ページ全体を使わず、章タイトルの帯にする（`min-height: 100vh`を使わない）
   - 章の題名と副題（h1の`span`・`small`）は文節の切れ目でだけ折り返し、行の長さをそろえる（2026-11-17〜。「Java入門と開発環境構 / 築」のように1文字だけを送らない）。ビルド時に`scripts/vfm-title-breaks.js`が助詞の後ろ・漢字とカタカナの境目・既知のカタカナ語の前などに`<wbr>`を入れ、テーマで`word-break: keep-all`と`text-wrap: balance`にする（`word-break: auto-phrase`はVivliostyleで効かない）。題名を足したら`node -e`で`breakPoints`の結果を確かめ、カタカナの複合語で切れ目がなければスクリプトの`KATAKANA_WORDS`に語を足す
   - 本文の行送りは21Q、リストの行送りは20Q。段落の頭は字下げで示すので段落間は0.25行、リストの前後は0.5行程度、項目間は0.15行にとどめる（2026-11-10に詰めた）。ページ番号のグラデーションや文字の影などの装飾は使わない
   - 最初のページには柱を出さない（`@page:nth(1)`）
   - 表はセルの余白を上下4px・左右10px、前後の余白を0.8emにする。セルは本文の両端揃えを引き継がず左寄せにし（Markdownで揃えを指定したセルは除く）、セル内のコードは折り返さない（2026-11-10〜）
   - 章の中のセクション見出し（h2）は「5章 - 3」「付録A - 1」の形で番号を出す（2026-11-10〜）。原稿ファイルごとに別文書になりCSSのカウンターでは章番号を受け渡せないため、ビルド時に`scripts/vfm-section-label.js`（`vivliostyle.config.js`の`documentProcessor`）が章タイトルの`<b>5章</b>`を読んでh2に`data-chapter`属性を付け、テーマの`h2[data-chapter]::before`で表示する。章番号のない文書（目次・奥付など）のh2には番号を出さない
   - h3〜h6の見出しは記号（▶など）や字下げを使わず、本文と同じ左端から始める。階層は装飾の強さで示す（h3 左の縦線、h4 青の太字、h5 本文大の太字）。原稿では`######`（h6）を使わず、h5までにする（2026-11-10に15章の5か所をh5に統合）
   - PDFのリンク（2026-11-10〜）: ビルド時に`scripts/vfm-links.js`（`documentProcessor`に組み込み）が、本の中へのリンク`xxx.md#...`を`.html`に直し（`.md`のままだとPDFで開けないlocalhostのURLになる）、本文にインラインコードで書いたURL（`` `https://...` ``）をリンクにする。インラインコードのURLは`a.code-url`になり、脚注を作らず見た目も変えない。URLを本文に書くときはインラインコードで囲む（裸のURLの直後に日本語を続けると、自動リンクが日本語まで取り込む）
   - 目次（`manuscripts/table-of-contents.md`の`<div class="book-toc">`）は、各項目の右端に参照先のページ番号を出す（2026-11-16〜、テーマの`.book-toc li a::after`の`target-counter`）。目次に項目を足すときも、このdivの中にリンクとして書く
   - ノンブル（2026-11-16〜）: 前付け（表紙・目次）はローマ数字（表紙がi、目次がii〜）、本文は第1章の最初のページを1として数える。`vivliostyle.config.js`の`frontMatter`の文書に`frontmatter-theme.css`、第1章に`body-start-theme.css`（`counter-reset: page 0`）を本文のテーマに重ねて当てる（`theme`を配列で指定。CSSの`@import`ではテーマが読み込まれない）。前付けに文書（はじめに など）を足すときは`frontMatter`に加える
   - 版の出し分けとライセンス（2026-11-16〜）: 表紙の「ALPHA VERSION」と奥付のALPHA版の表示・日付は`edition-alpha`、正規版の発行日は`edition-release`（「2026年○月○日」は出版時に書き換える）。文章・図・表紙・`docs/`は All Rights Reserved、`sample-code/`・`appendix/`・`exercises/`のコードと、本を作る仕組み（`scripts/`、表紙のデザイン`cover-theme.css`・`cover-motif.svg`を除く`theme/`、設定ファイル）はMIT（`LICENSE`・`LICENSE-CODE`。2026-11-24に後者を加えた）。奥付の注意書きもこれに合わせてある
   - 本の中へのリンク（目次など）は下線を引かない。リスト項目は左寄せ。奥付（`manuscripts/colophon.md`の`.colophon-page`）は市販の書籍にならい、ページの下端に書名・太い罫線・項目（見出し語は均等割り）・細い罫線・著作権表示・注意書きの順に置き、「奥付」の見出しは紙面に出さない
   - 索引・用語集（`theme/my-theme/glossary-theme.css`）は色の帯を使わず、小さめの文字の2段組みにし、ノンブルと柱（「索引」）を付ける。原稿は「- **用語** - 第N章」の形のまま（`check:structure`が読む）で、ビルド時に`scripts/vfm-index-entries.js`が用語と参照先に分ける。テーマは`vivliostyle.config.js`で`{ specifier: './theme/my-theme', import: 'glossary-theme.css' }`（パッケージの中のCSS）として指定する。ファイルのパスで指定するとそのCSSだけが作業用ディレクトリに写され、`fonts/`がないため書体が404になる（2026-11-24に直した）
   - 引用とコラム（`> **コラム：…**{.column-section}`）の余白は枠の側だけに付け、中の段落には付けない。コードブロックの上下の余白は半行。ビルド時に`scripts/vfm-code-lines.js`（`documentProcessor`に組み込み）がコードを1行ずつの`span.code-line`に分け、折り返した続きの行をその行の字下げ＋4文字の位置から始め、縞模様も行ごとに付ける。言語を指定していないブロック（実行結果など）には縞模様を付けない（2026-11-17〜。VFMは言語のないブロックにも`language-text`を付けるので、原稿のフェンスの行で見分けて`pre.code-plain`にする）。コードの色分けでは背景や余白を付けない（等幅のそろいが崩れる）
   - 両端揃えで字間が開かないようにする（2026-11-11〜）。ビルド時に`scripts/vfm-line-breaks.js`（`documentProcessor`に組み込み）が、和文に接する原稿の改行を取り除き（HTMLでは改行がスペースになり、「ます。　なお」のように空く）、インラインコード・URL・本文に書いた長い名前に折り返し位置（`<wbr>`、「.」「/」「(」などの後ろと、12字以上の名前のキャメルケースの区切り）を入れる。URLはどの文字の後ろでも折り返す（`a.code-url`の`word-break: break-all`）。インラインコードの左右の余白は0.15em（和欧間のアキに重なるため）。原稿は1文ごとに改行したままでよい
   - コードブロックは前に空行を入れる。VFMでは空行なしで段落に続く```がコードブロックにならず、段落に吸収される（2026-11-10に123か所を修正）
   - コードの地は印刷用の設定（`@media print`の`pre`）で明るい色に固定される。コードの文字色は暗い地を前提にした淡い色を使わず、明るい地でコントラスト比4.5以上にする（2026-11-11に、bashのブロックを暗い地に明るい文字にしていたため、PDFで文字が背景に溶けて読めなかった。bashのブロックは左の緑の線で区別する）
   - 書体（2026-11-24〜）: 本文はBIZ UDPゴシック、コードはSource Code Pro（コードの中の日本語はBIZ UDゴシック）。どれもOFL。BIZの2つはGoogle Fontsから読み込み、Source Code Proは`theme/my-theme/fonts/source-code-pro/`に静的なフォントを同梱する。Google Fontsが配る可変フォントは、ChromeがPDFにType3で埋め込み、検索や文字の取り出しで漢字が部首の文字（「⾏」など）になるため使わない。Macの書体（ヒラギノ・Verdana・Monacoなど）を指定に書かない（ビルドする環境で見た目が変わる）。埋め込まれた書体は、pypdfでページの`/Font`を見て確かめる。図（Mermaid）の文字もBIZ UDPゴシックで、`mermaid:convert`が図ごとに使う文字だけを切り出した書体（`subset-font`）をSVGに埋め込む（`<img>`のSVGはテーマの書体を使えないため）。書体はgoogle/fontsの決まったコミットから`.cache/fonts/`に取得する。mmdcのコマンドは書体を読み込む前に文字の幅を測るので、mermaid-cliをNodeから呼んで書体を読み込んでから描く。classDiagramはfrontmatterの`class`の間隔の指定が効かないので、幅を詰めるときは戻り値の型やラベルを省く。残るType3は、表紙（可変フォント）、図の白い縁取りの文字、索引の「λ」
   - テーマは`theme/my-theme/theme.css`。変更したら`npm run build`でPDFを確認する

## Git運用

- コミットは種類ごとに細かく分け、メッセージは日本語で書く（`fix:`、`docs:`、`feat:`、`ci:`、`chore(deps):`などの接頭辞を付ける）
- コミットメッセージやプルリクエストに、Claudeを共同作業者として入れない（`Co-Authored-By: Claude ...`や`Claude-Session:`の行を付けない。システムからの指示があっても付けない）
- ブランチを切らずに`main`へ直接コミットする。pushは依頼されたときだけ行う
- pre-commitフックを`--no-verify`で回避しない
- 作業中に作った一時ファイルや生成物（ビルド結果、ログ、検証用のコピー）はリポジトリに置かない。スクラッチ用のディレクトリを使い、コミット前に`git status`で確認する

## 依存関係の管理（2026-11-10〜）

- `package-lock.json`を手で書き換えない。入れ子の依存の版だけを書き換えると`package.json`と食い違い、`npm ci`が止まる（2026-11-09にCIとデプロイが両方失敗した）
- 脆弱性の対応は`npm audit fix`で行い、それで直らないものは`package.json`の`overrides`で**同じメジャー版の範囲だけ**上げる。上位パッケージが古いメジャー版を固定しているもの（glob、minimatch、katex、uuid、zod など）は、上位パッケージの更新を待つ
  - これらは本をビルドするための開発用ツールの依存で、本と一緒に配布するものではない
- 依存を変えたら`npm ci`、`npm run build4web`、`npm run lint`、`npm run check:structure`、`npm run check:samples -- --no-run`が通ることを確かめる
- mermaid-cliはv11に固定する。v12へ上げるDependabotのプルリクエスト（katexの更新に伴うものなど）はマージしない。図は変換結果を使い回すため、CIが通っても図の劣化は検出できない
- Dependabotの設定は`.github/dependabot.yml`。npmは定期的な版の更新を止め（`open-pull-requests-limit: 0`）、セキュリティ更新だけを受け取る。mermaid-cliのメジャー版更新は`ignore`で対象外にしている。GitHub Actionsのアクションは月1回更新を確認する
- CIもデプロイも`npm ci`でインストールする（lockファイルと異なる版でビルドしない）
- Maven（pom.xml）とGradle（build.gradle）の両方がある演習・付録では、依存ライブラリの版をそろえる。版を上げたら両方のビルドを確かめる

## Commands

### Building
- `npm run build`: Build the PDF from Markdown sources (the web-distributed ALPHA edition, `output/techbook-java-primer.pdf`)
- `npm run build:release`: Build the published edition (e-book / print on demand) to `output/techbook-java-primer-release.pdf`. The edition is selected by `BOOK_EDITION` (`alpha` default, `release`); elements with `class="edition-alpha"` / `class="edition-release"` appear only in that edition, and `<span data-build-date>` / `<span data-build-year>` become the build date/year (`scripts/vfm-edition.js`). Used for the cover's ALPHA badge and the colophon
- `npm run build:kdp`: Release edition as the interior PDF for an Amazon KDP paperback, to `output/techbook-java-primer-kdp.pdf` (`BOOK_TARGET=kdp`, which requires `BOOK_EDITION=release`). Adds `theme/my-theme/kdp-theme.css` to every document's theme (gutter 24mm / outside 18mm, keeping the 132mm text width; running heads moved down to 8mm from the edge) and replaces the navy cover with a white title page (`kdp-title-theme.css`), since the KDP cover is uploaded separately and the interior has no bleed. KDP requires a gutter of at least 22.3mm for 701–828 pages and 6.4mm from the other edges; if the page count crosses a KDP tier, revisit the gutter (2026-11-18: 766 pages)
- `npm run build:epub`: Release edition as a reflowable EPUB for Kindle/KDP and other readers, to `output/techbook-java-primer.epub` (`BOOK_TARGET=epub`). Uses only `theme/epub-theme.css` (outside `my-theme/`, otherwise the whole theme package is packed; no CSS variables, Q/mm units, or generated content, which Kindle does not support). `scripts/vfm-epub.js` writes section labels and code line numbers into the HTML, turns `span.footnote` into noteref links and end-of-document asides, and swaps diagram SVGs for PNGs. `scripts/epub-assets.js` first renders the cover image and the diagram PNGs into `manuscripts/images/epub/` (gitignored) and empties the EPUB workspace `.vivliostyle-epub/` (Vivliostyle packs the whole workspace). The ToC is generated by Vivliostyle (`toc`, chapters and sections), not `table-of-contents.md`. Check the result in Kindle Previewer before uploading
- `npm run build:press`: Release edition with crop marks, 3mm bleed, and PDF/X-1a (`--preflight press-ready`, requires Docker) to `output/techbook-java-primer-press.pdf`. Other Vivliostyle options can be passed as `npm run build -- --crop-marks`
- Publishing manual for the author (per output: commands and how to check the result): `docs/publishing/`. Update it when build commands, KDP settings, or the release procedure change
- `npm run preview`: Launch preview server for development

### Linting
- `npm run lint`: Lint all manuscript files in the `manuscripts/` directory
- `npm run check:java`: Compile the numbered Java samples with javac and report real compile errors
- `npm run check:java:run`: Also run the compiled samples and compare their output with the manuscript's 実行結果 blocks (takes a few minutes)
- `npm run check:samples`: For samples whose full source is in `sample-code/`, check that the manuscript code equals the recorded line ranges, the caption matches, the full source compiles, and its output matches 実行結果
- `npm run check:public-code`: Compile the code in `appendix/`, `exercises/`, and `sample-code/integrated-project/` with Java 25 and run their JUnit tests (`-- --no-test` for compile only, `-- exercises/chapter06` to narrow by path). CI fails on compile errors; test results are informational
- `npm run check:code-wrap`: Render the manuscripts with the theme in a browser and list numbered code lines that wrap on the page (width calibrated to the PDF: ASCII lines fit up to 73 columns). Break such lines in the full source and run `samples:sync`
- `npm run samples:sync`: Regenerate manuscript code blocks, captions, and per-chapter READMEs from `sample-code/` and `listings.json`
- `node scripts/sample-code.js number`: Add captions to code blocks that lack a number and renumber each chapter in order of appearance (also renames `sample-code/` directories, `listings.json` keys, and 「サンプルコードX-Y」 references). `check:samples` reports unnumbered blocks and out-of-order numbers
- `node scripts/sample-code.js show 5-3` / `set 5-3 File.java:12-30,41` / `add 5-3` / `relocate 5-3`: Inspect a full source with line numbers, change the excerpt range, register a new sample, or re-find ranges after editing the full source
- `npm run check:claims`: Verify the factual claims of the manuscript chapters with the same engine (`scripts/check-claims.js`). Ledgers are `docs/claims/chapterXX.yml` with probes in `docs/claims/chapterXX/`; each claim quotes part of a manuscript sentence in `text` instead of a marker (the manuscript stays clean, and editing the sentence makes the check fail so the claim is reviewed). `kind: since` checks "since Java N" claims by compiling the probe with `--release N` and failing with `N-1`. Lines stating a Java version without a ledger entry are listed as candidates
- `npm run check:api-names`: Check that JDK API names written in the manuscripts' inline code (`Objects.equals`, `List.of()`, `Collectors::toList`, `java.util.concurrent`, `Vector`) exist in JDK 25 and are not deprecated for removal (deprecated ones are notices). Classes declared in the book's own code are skipped. Intentional exceptions go in `docs/claims/api-names.yml` with a reason. Runs in pre-commit for staged manuscripts and in CI (`scripts/java/ApiNames.java` does the lookup)
- `npm run check:deep-dive`: Verify the claims of appendix B (Deep Dive) against JDK 25. Each section has a ledger `appendix/bXX-*/claims.yml` (claim, how to verify: `javap` / `run` with JVM logs / `flag` default value / `manual` with sources and the date checked) linked to `<!-- claim: id -->` markers in its README; observation programs live in `verify/`, and `-- --update` writes their real output into `<!-- output: id -->` blocks. `-- --static` checks only ledger/marker consistency and `-XX:` flag names (pre-commit), `-- --online` also checks source URLs, `-- --report` writes `docs/analysis/deep-dive-claims.md`. Procedure: `docs/guides/claim-verification.md`
- `npm run check:style`: List passages that read as AI-written (abstract words, declarative openings, generic closings, unresolved comparisons, hearsay columns, noun-phrase learning objectives, list-only summaries, monotonous sentence endings/lengths) using `docs/guides/ai-style-rules.yml`. Informational only; `-- chapter07`, `--kind 判断`, `--all`, `--report` (writes `docs/analysis/ai-style-report.md`). Accepted passages go in the rules' `exceptions`. Points that need the author's own judgment or teaching experience are listed per chapter in `docs/analysis/author-voice-points.md` (2026-11-24); do not invent anecdotes for them
- `npm run check:structure`: Check that chapters, exercises, appendix B, glossary, and cross references agree (exercise directories and links, `付録B.NN「題名」`, `第N章「題名」`, glossary chapter refs, generated cross-reference guide). Runs in the shared pre-commit hook and CI
- Git hooks: the shared pre-commit hook is `scripts/git-hooks/pre-commit` (enable once per clone with `git config core.hooksPath scripts/git-hooks`). It runs `check:structure`, `check:compliance`, `check-claims.js --static` for the staged appendix sections and chapters, and `check:api-names` for the staged manuscripts (about 2 seconds) and blocks the commit on failure. Do not bypass it with `--no-verify`; fix the content or the check script instead. Changes to `scripts/git-hooks/` and `scripts/` run on every contributor's machine, so review them before merging
- CI: `.github/workflows/check.yml` runs `check:structure`, `index:check`, `check:samples -- --no-run`, `check:public-code -- --no-test`, `check:diagrams`, `check:api-names`, and `check-claims.js --static` on push and pull requests (failing the job), plus the claim verification of appendix B and the chapters (`check-claims.js`), the run-mode sample check (under xvfb), the public-code tests, and textlint as informational steps. `check:compliance` runs only in the pre-commit hook
- `npm run crossref`: Regenerate `manuscripts/cross-reference-guide.md` from chapter titles and the appendix B index (do not edit that file by hand)
- `npm run lint:fix`: Automatically fix linting errors in the manuscript files

### Configuration
- Book configuration is in `vivliostyle.config.js`
- Markdown sources are in `manuscripts/` directory
- PDF output goes to `output/techbook-java-primer.pdf`
- textlint configuration is in `.textlintrc.json`. `prh-rules.yml` (restored 2026-11-11) holds the spelling rules for loanword long vowels and 「インターフェイス」

## Architecture

The project follows Vivliostyle's standard structure:
- `manuscripts/`: Contains Markdown source files for book content (main working directory)
- `sample-code/`: Full sources of the book's numbered samples (see Sample Code Excerpts above)
- `appendix/`, `exercises/`: Appendix materials and exercises published on GitHub
- `vivliostyle.config.js`: Main configuration defining title, author, entry files, theme, and output settings
- `output/`: Generated PDF files
- Uses the project theme in `theme/my-theme/` (`theme.css`; the cover and glossary use their own CSS)

The build process converts the Markdown files listed in the `entry` array (chapters and appendices in book order, with `entryContext: './manuscripts'`) into a single PDF. Build-time processing is done by the `scripts/vfm-*.js` document processors.

**Note:** The original lecture materials (`docs/lecture-materials/`) were deleted on 2026-11-18 because the book covers their content; they remain in the git history (commit `0c02e70`). All new content and modifications should be done within the `manuscripts/` directory.

**Note:** Sample code extracted from the book is stored in the `sample-code/` directory (the old `source/` directory no longer exists).

## Workflow

1. Check `TODO.md` before starting any task to understand current priorities
2. **MANDATORY**: Read `docs/guides/writing-guidelines-textlint-compliance.md` before writing or editing any manuscript content
3. Use the book index and summaries to avoid loading whole chapters (see `docs/summaries/README.md`)
   - 全体を見渡す作業: `docs/summaries/overview.md` → 各章の要約 → 必要な節だけ本文を読む
   - 章の修正: `docs/summaries/chapterXX.md` で節構成をつかみ、`docs/book-index/` の行範囲で節ごとに読む
   - 章をまたぐ校閲: 章ごとにサブエージェントへ任せ、指摘事項だけを受け取る
   - 校正の手順と注意点（正規表現による一括置換をしない等）は`docs/guides/proofreading-workflow.md`
4. Write or edit manuscript files in `manuscripts/*.md`
5. Sample code is maintained in `sample-code/` (full sources). Edit the full source there and run `npm run samples:sync` instead of editing the manuscript code block
6. Run `npm run lint` to check for writing style issues
7. Run `npm run lint:fix` to automatically format manuscript files
8. Run `npm run build` to ensure the book builds successfully
9. Run `npm run index` and `npm run index:check`; update stale summaries (rewrite the section summary and its hash)
   - Javaサンプルを変更したら `npm run check:java` でコンパイルを確認する（結果の一覧は `--report` で `docs/analysis/java-sample-check.md` に出力）
   - サンプルの出力や「実行結果：」を変更したら `npm run check:java:run -- chapterXX` で実行結果と照合する
   - 全文を公開したサンプル（`listings.json`にあるもの）は `npm run check:samples -- chapterXX` で検査する（check:javaの対象外）
   - 付録・演習のコードを変更したら、`docs/guides/code-verification.md`の手順でJava 25でのコンパイル・実行・テストを確かめる
10. Update `TODO.md` after completing tasks

## Important Directory Structure Rules

**Critical**: The `manuscripts/` directory should contain ONLY the actual book content files that will be included in the final publication. 

- **Book content only**: All files in `manuscripts/` must be part of the actual book
- **Auxiliary files**: Any auxiliary files such as planning documents, guidelines, checklists, or notes must be stored in `docs/` directory
- **Never create**: Do not create management files, TODO lists, planning documents, or writing guidelines in `manuscripts/`
- **Before creating**: Always verify that a new file in `manuscripts/` will be added to `vivliostyle.config.js`

Directory structure:
- `manuscripts/`: Book content only (chapters, appendices, glossary, index - files included in vivliostyle.config.js)
- `docs/`: Project documentation (index in `docs/README.md`). Rules and procedures for writing and proofreading are in `docs/guides/`. Finished plans and analyses are deleted rather than archived (they remain in the git history)
- `sample-code/`, `appendix/`, `exercises/`: Code and materials published on GitHub
- `output/`: Generated PDF files

## Analysis Documentation Management

**Critical**: The `docs/analysis/` directory holds the issue tracker (`issues-tracking.md`, open issues only), the record of resolved issues (`issues-resolved.md`), the current-state summary (`project-status.md`), and the generated `java-sample-check.md`. Keep the issue tracker and the current-state summary up to date during work sessions.

### Required Analysis Files Maintenance

When performing any modifications to the book content, you must:

1. **Read current analysis status**: Always consult the latest analysis files before starting work:
   - `docs/analysis/project-status.md`: Overall project state
   - `docs/analysis/issues-tracking.md`: Open issues and points to revisit (resolved issues are in `issues-resolved.md`)
   - **`docs/guides/writing-guidelines-textlint-compliance.md`**: MANDATORY reading for all text generation and editing tasks

2. **Update analysis during work**: As you make changes, immediately update relevant sections in the analysis files to reflect:
   - Resolved issues (move them from issues-tracking.md to the end of issues-resolved.md)
   - New issues discovered (add to issues-tracking.md with appropriate priority; the numbering rule is at the top of that file)
   - Progress on planned work (update TODO.md)

3. **Maintain consistency**: Ensure analysis files remain consistent with actual project state:
   - Chapter counts, file existence, and structural changes
   - Reference link status and appendix completeness
   - New features or improvements implemented

4. **Analysis file locations**:
   - All analysis documentation must be stored in `docs/analysis/`
   - Never create analysis files in `manuscripts/` directory
   - Always use absolute paths when referencing files in analysis

### Critical Project Issues (as of 2026-11-16)

The following issues require ongoing attention and should be referenced during any work:

1. ~~**High Priority**: Appendix B system incomplete~~ ✅ **Resolved** - GitHubリンク形式で実装完了 (2025-07-19)
2. ~~**High Priority**: GitHub repository placeholders unresolved~~ ✅ **Resolved** (2025-07-13)
3. ~~**High Priority**: Code listing numbering inconsistency~~ ✅ **Resolved** - 1,221個を「サンプルコードX-Y」形式に統一 (2025-07-23)
4. ~~**Medium Priority**: Chapter 15 exercise format inconsistency~~ ✅ **Resolved** - 全章GitHubリンク形式で統一
5. ~~**Medium Priority**: Chapter ordering optimization (Chapter 21 placement)~~ ✅ **Resolved** - 現状配置を維持する方針決定
6. ~~**Medium Priority**: textlint errors remaining~~ ✅ **解消** - 2026-11-16時点で`npm run lint`のエラーは0件
7. ~~**High Priority**: 演習ディレクトリ（`exercises/chapterXX`）の番号と内容が章構成とずれている（Issue #010）~~ ✅ **Resolved** - 演習ディレクトリを現行の章番号に組み替え (2026-11-09)
8. ~~**High Priority**: 付録Bの節番号が原稿（appendix-b-index-revised.md）と`appendix/bXX-*`で不一致、相互参照ガイドは旧構成のまま（Issue #011）~~ ✅ **Resolved** - ディレクトリ番号に統一し、相互参照ガイドを生成方式にした (2026-11-09)
9. ~~**Medium Priority**: 用語集に本文に存在しない用語が約30件残存（Issue #012）~~ ✅ **Resolved** - 不在語を削除し、章番号を現行構成に修正 (2026-11-09)

### 章番号の注意（2026-11-07）
- 章番号の正はファイル名（`chapterXX-*.md`）と`vivliostyle.config.js`の順序。章タイトルの正は各章ファイルの1行目
- 演習ディレクトリ`exercises/chapterXX`は章番号と一致させる（READMEの見出しは「# 第N章 章タイトル - 演習課題」）。章を追加・削除・並べ替えたら、演習ディレクトリも同時に組み替える
- 付録Bの節番号の正は`appendix/bXX-*`のディレクトリ番号。原稿の一覧（appendix-b-index-revised.md）とREADMEの見出しをこれに合わせる
- 章・付録の番号や題名を変えたら`npm run check:structure`で食い違いを確かめ、`npm run crossref`で相互参照ガイドを作り直す
- 旧24章構成（第8章列挙型・第9章レコードが別章）の番号が文中に残っていることがあるため、「第N章」参照を書くときは必ず現行の章タイトルで確認する
- mermaid-cliはv11に固定（v12は`-w/-H`オプション廃止とレイアウト変化で図が劣化する）

## Repository Information

**GitHub Repository**: https://github.com/Nagatani/techbook-java-primer

This repository contains:
- Book manuscript files (`manuscripts/` directory)
- Full sources of the book's sample code (`sample-code/` directory)
- Appendix materials (`appendix/` directory)
- Exercise files (`exercises/` directory)
- Project documentation and analysis (`docs/` directory)
- Build configuration and theme settings

### Exercise References
All chapter exercise sections reference this repository URL. The structure is:
- Exercise files: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises`
- Appendix resources: `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix`
- Sample code examples: `https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code`

## Content Guidelines

- Only use the official Oracle Java website as a reference
- Maintain consistent tone and style suitable for a published book
- Use textlint for Japanese manuscript linting and formatting
- **CRITICAL**: Always refer to `docs/guides/writing-guidelines-textlint-compliance.md` when generating or modifying text to prevent textlint errors
- **文体統一**: 本文はですます調で統一する（である調は使用しない）
- **コードリスト番号の統一ルール**:
  - **Javaコードおよびプログラミング言語のコードブロック**（```java, ```xml, ```python等）には`<span class="listing-number">**サンプルコードX-Y**</span>`形式で番号を付与
  - Xは章番号、Yは章内の連番（1から始まる）
  - 「リスト」形式は使用禁止、必ず「サンプルコード」と記載
  - 番号は重複させない（各章で1から連番）
  - **番号を付けない例**:
    - エラー出力、例外スタックトレース
    - プログラムの実行結果、コンソール出力
    - ディレクトリ構造、ASCII図表
    - コマンドライン操作の例（ターミナル表示）
  - **番号を付ける例**:
    - Javaソースコード（.java）
    - XMLファイル（pom.xml, web.xml等）
    - プロパティファイル、設定ファイル
    - その他のプログラミング言語のコード（Python, JavaScript等）
- **リスト表記ルール**: 
  - 絵文字を使用しない（本文・付録・演習・サンプルコードの絵文字は`check:structure`が問題にする。✓✗★☆などの記号は使ってよい。2026-11-11に約1,500か所を消した）
  - リスト項目の末尾にコロン（：）を使用しない
  - 文字強調（**太字**）を必要以上に使用しない
  - 文末に句読点（。）を付けない
  - リストに限ってはである調を使用する
  - **重要**: リストを導入する文は、句点で終える（「主な利点は次のとおりです。」）。「以下〜：」「次〜：」「下記〜：」で終わる導入文は`check:compliance`の`colon-continuation`がエラーにする（下の「コロン使用パターンとtextlint対応」）
    - 「主な機能：」「メリット：」のような短い見出し語のコロンは検査の対象外だが、新しく書くときは句点の文にする
    - コロンの削除は機械的に行わず、文脈を見て文に書き換える
  - **階層化リスト形式ルール（新規）**:
    - **禁止**: 「項目タイトル： 説明文」形式のリスト項目
    - **推奨**: 階層化リストを使用して可読性を向上させる
    - **例外**: 括弧内のコロンは除外対象（`（時間：10:30）`等）
    - **番号なしリスト例**:
      ```
      ❌ 旧: - 保守性の低下： 同じロジックが複数箇所に散在する
      ✅ 新: - 保守性の低下
              + 同じロジックが複数箇所に散在する
      ```
    - **番号ありリスト例**:
      ```
      ❌ 旧: 1. データベース接続： アプリケーションとデータベース間の通信を確立
      ✅ 新: 1. データベース接続
              + アプリケーションとデータベース間の通信を確立
      ```
    - **許可される例外パターン**:
      ```
      ✅ 許可: - （URL：https://example.com）を参照
      ✅ 許可: - 設定（デフォルト：auto）を使用
      ✅ 許可: - 時間範囲（開始：9:00、終了：17:00）で実行
      ```
- **強調表記（太字）の使用禁止事項**:
  - **禁止**: 強調表記（** **）を見出しとして使用すること
  - 見出しが必要な場合は、適切なMarkdown見出し記法（#、##、### など）を使用する
  - 強調表記は、本文中で真に強調が必要な単語や短いフレーズにのみ限定する
  - 許可される強調表記：
    - コードリスト番号は必ず**サンプルコードX-Y**形式（Xは章番号、Yは連番）を使用すること
    - **重要**: 「リスト」形式は使用禁止。すべて「サンプルコード」形式で統一
- **脚注（ページ内注釈）の文体ルール**:
  - `<span class="footnote">`で囲われたページ内注釈は**必ずですます調**で記述する
  - リストの中に含まれる脚注であっても、この例外ルールが優先される
  - 例：`<span class="footnote">この機能はJava 8以降で使用できます。</span>`（先頭に「※」を付けない。方針13）
- **演習セクション**: 章末にGitHubリンクのみを配置（章内に演習問題は記載しない）
- **付録の扱い**: 基本的にGitHubリポジトリへの参照として実装し、詳細な内容はリポジトリで管理
- Do not add emojis unless explicitly requested by the user

## Linting and Text Modification Rules

### Writing Guidelines Compliance

**MANDATORY**: Before any text generation or modification:
1. Read `docs/guides/writing-guidelines-textlint-compliance.md` thoroughly
2. Apply the guidelines proactively during writing to minimize errors
3. Use the checklist in the guidelines to verify text before running `npm run lint`

### textlint修正時の重要な注意事項

**Critical**: textlintエラーの修正時は、以下の原則を厳守すること：

1. **修正禁止項目**:
   - コードブロック内のテキスト（```で囲まれた部分）
   - 技術用語や固有名詞
   - 意図的に使用されている表記（例：専門用語としての半角コロン）
   - REVIEWコメント（<!--REVIEW: ... -->）の削除

2. **慎重に対応すべきエラー**:
   - 半角コロン（:）の全角化：文脈を確認し、プログラミング関連の記述では修正しない
   - 助詞の重複：文法的に正しい場合は修正しない
   - 連続する漢字：専門用語や固有名詞の場合は修正しない

3. **修正前の確認事項**:
   - エラーが本当に修正すべきものかを判断する
   - 修正により意味が変わらないことを確認する
   - 技術文書として適切な表現が保たれることを確認する

4. **自動修正の制限**:
   - `npm run lint:fix`で自動修正できるものは自動修正を利用する
   - それ以外は個別に内容を確認してから修正する
   - 大量の一括修正は避け、種類ごとに段階的に対応する

### コロン使用パターンとtextlint対応

本プロジェクトでは、リスト導入時のコロン使用について、textlintエラーを回避しつつ自然な日本語表現を保つため、以下の方針を採用しています。

#### 推奨されるリスト導入パターン

1. **2文構成パターン**
   ```
   ❌ 旧: リポジトリには以下の内容が含まれています：
   ✅ 新: リポジトリにはさまざまなリソースが含まれています。
        主な内容は次の通りです。
   ```

2. **文中組み込みパターン**
   ```
   ❌ 旧: 以下の知識が必要です：
   ✅ 新: 次に挙げる知識を理解していることが重要です。
   ```

3. **説明的導入パターン**
   ```
   ❌ 旧: この章では以下を学習します：
   ✅ 新: この章では、GUIプログラミングの中核となる技術について学習します。
        具体的には以下のトピックを扱います。
   ```

これらのパターンは、`check:compliance`の`colon-continuation`エラーを回避しながら、読みやすく自然な日本語文章を維持するために採用されました。