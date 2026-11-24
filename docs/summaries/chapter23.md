---
source: manuscripts/chapter23-build-and-deploy.md
summarized: 2026-11-24
sections:
  - key: "本章の学習目標"
    hash: 4b20525b
  - key: "なぜビルドと配布が必要か"
    hash: 44eb1d05
  - key: "実行可能JARファイルの作成"
    hash: bf905a6a
  - key: "jpackageによるネイティブアプリケーション化"
    hash: b0c274ee
  - key: "よくあるエラーと対処法"
    hash: fde5e426
  - key: "まとめ"
    hash: a92a7c8b
  - key: "章末演習"
    hash: 54adf871
---

# 第23章 ビルドとデプロイ 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter23-build-and-deploy.md` を参照する。

## 章の概要

- 開発したJavaアプリケーションを、開発環境を持たない人に配布する方法を扱う最終章
- 内容は2本立てで、`javac`→マニフェスト→`jar cvfm`→`java -jar`による実行可能JARの作成と、`jpackage`によるランタイム同梱のネイティブアプリ化
- 外部ライブラリを使うアプリのJARは、マニフェストの`Class-Path`だけを文章で説明する。Maven、Fat JAR、GraalVM、コンテナー、CI/CDはまとめで名前を挙げるだけで、詳細は付録D「高度なビルドとデプロイメント技術」（`appendix/d-build-and-deploy/`）に委ねる
- 450行と短く、コードはほぼコマンド例。サンプルアプリはSwing（第17章の内容）

## 構成上の問題

- 実行可能JARの手順の分断は、ステップ1〜5を先に通し、複数クラスの例とリソースの例をステップ5の後ろへ移して解消した（2026-11-11に解消）
- 前提知識の不一致（第14章にクラスパスの説明がない、第22章のGradleを前提にする、Swingの章が前提にない）は、前提を第17章（Swing）と第22章（クラスパスとJAR）に改めて解消した（2026-11-11に解消）
- 学習目標の「外部ライブラリを含む複雑なアプリケーション」の予告は、学習目標を書き直して解消した
- 「複数のクラスファイルを含むアプリケーションの例」のTodoAppは、入力欄とボタンを`TodoInputPanel`クラス（別ファイル）に分け、`jar tf`で2つのクラスファイルを確かめる手順と、manifest.txtを全文に加えた（2026-11-12に解消）
- 外部ライブラリを含むJARが第22章でも本章でも扱われない空白地帯になっていた問題は、節「外部ライブラリを使うアプリケーションのJAR」（`java -jar`では`-cp`が無視されること、マニフェストの`Class-Path`、`lib`ディレクトリでの配布、Fat JARへの誘導）を加えて解消した。JDK 25で`Class-Path`の動作を確かめた（2026-11-14に解消）
- コマンドライン操作の例（bash）に番号が付いていた問題は解消した（2026-11-12に解消）

## 節ごとの要約

- 本章の学習目標（L3–33）
    + JARのしくみ、マニフェスト、実行可能JAR、jpackage、ビルドとパッケージングの流れの5項目
    + 前提知識は第1章、第14章（基本的なI/O操作）、第17章（Swing）、第22章（クラスパスとJAR）、コマンドライン操作。「次の知識があると、より理解が深まります。」として第21章、CI/CD（継続的インテグレーションと継続的デリバリー）、デプロイ、OS差異
- なぜビルドと配布が必要か（L34–51）
    + ソースコードをそのまま渡せないこと、「ビルド」（コンパイルしてクラスファイルを作る）「パッケージング」（クラスファイルとリソースを1つにまとめる）「配布」（ユーザーに届ける）を分けて定義し、ソースから配布形式までの図（chapter23-build-flow）を示す
- 実行可能JARファイルの作成（L52–299）
    + JARはZIP形式のアーカイブで、第22章のライブラリのJARと同じ形式に自分のプログラムをまとめ、Main-Classを加えると`java -jar`で実行できる
    + ステップ1〜5: Swingの最小アプリ（23-1）、`javac`、マニフェストのMain-Classと半角スペース・末尾改行の注意（23-2）、`jar cvfm`とオプション（脚注で`jar --create --main-class`と`jar cvfe`の書き方も紹介）、`java -jar`での実行
    + 応用: TodoAppとTodoInputPanel（23-3、抜粋にコンストラクターを含む）と複数クラスのJAR作成・`jar tf`での確認
    + 外部ライブラリを使うアプリケーションのJAR: `java -jar`では`-cp`が無視されるため、マニフェストの`Class-Path`（JARからの相対パス）にライブラリを書き、`lib`ディレクトリと一緒に配布する。Fat JARはまとめへ誘導（コードブロックなし）
    + `getResource`で画像を読むResourceApp（23-4、nullでNullPointerExceptionになる注意と、icon.pngとmanifest.txtを全文と同じ場所に用意したこと）とリソース込みのJAR作成
    + IntelliJ IDEAのArtifacts機能でのJAR作成手順
- jpackageによるネイティブアプリケーション化（L300–402）
    + JARはJREが必要という限界と、ランタイムを同梱するjpackage（Java 16で正式導入）の利点
    + 入力ディレクトリと出力ディレクトリを分ける準備とディレクトリ図（罫線は`├──`・`│`・`└──`）
    + app-imageの作成とオプション解説
    + jpackageは実行しているOS向けのものしか作れないこと（クロスビルド不可）を述べたうえで、Windows・macOS・Linux向けの例。インストーラーにはWiX Toolset・dpkg-deb・fakeroot・rpmbuildなどの追加ツールが要ること（脚注。2026-11-14に、Linuxではapp-imageを作るときにもobjcopy（binutils）が要ることを追加）、app-imageはインストーラーでないことを述べる
    + コラム「「自分のPCでは動くのに」をなくす工夫」（2026-11-19）。クラスファイルの版の違い（69と65）とUnsupportedClassVersionError、改行・パス・文字コードの差、maven.compiler.release・CI・jpackage・コンテナで差を減らすこと
- よくあるエラーと対処法（L383–401）
    + メイン・マニフェスト属性がない（Main-Classの欠落、最終行の改行）、`NoClassDefFoundError`（`java -jar`では`-cp`が無視される）、`getResource`や`getResourceAsStream`が`null`を返す、`UnsupportedClassVersionError`、jpackageのエラー。詳細は`appendix/e-common-errors/chapters/chapter23.md`
- まとめ（L402–433）
    + 発展技術としてビルド自動化ツール（第22章で紹介したMaven、`mvn package`）、Fat JAR（Maven Shade Plugin、Spring Bootのプラグインなど）、GraalVMネイティブイメージ、コンテナー化、CI/CD（第21章のGitHub Actions）を列挙し、付録Dへ誘導する。「今後の学習に向けて」で第24章と第25・26章へ案内する
- 章末演習（L434–450）
    + `exercises/chapter23/`へのリンク。基礎（実行可能JAR）・発展（リソース込みJAR）・チャレンジ（各OS向けインストーラー）の3課題

## 他章との関係

- 第1章: javac/javaコマンドとIntelliJ IDEAの導入。本章のIntelliJ手順はその延長
- 第17章: サンプルアプリ（JFrame、JList、DefaultListModel、SwingUtilities.invokeLater）の前提
- 第21章: まとめのCI/CDでGitHub Actionsに触れる
- 第22章: クラスパスとJAR、Mavenを扱い、本章の冒頭・外部ライブラリのJARの節・まとめから参照する。Fat JARの作り方はどちらの章も扱わず、付録Dに委ねる
- 付録D（高度なビルドとデプロイメント技術）: 原稿はGitHubへの誘導だけの最小構成。学習内容（Maven/Gradle自動化、Fat JAR、jpackageの高度な活用）はまとめの列挙とほぼ同じ

## 校正で見つけた個別の問題

- L263: 「（Windowsなら`.exe`、Macなら`.app`）」と書くが、23-10の`app-image`はWindowsでは`.exe`を含むフォルダーで、`.exe`のインストーラーとは別物。L313の見出し「Windows向け（.exeまたは.msi）」と合わせて読むと紛らわしい（2026-11-12に解消）
- L267–270: 1文を読点で4行に分けている（紙面への影響はない）（2026-11-12に解消）
- L362: 「主な高度な技術は次の通りです。」の直前のL360と内容が重なる（2026-11-12に解消）
- L381–382: 付録Eへの誘導を「※」で始め、URLを括弧で次の行に置いている（ほかの章の誘導の書き方と異なる）（2026-11-12に解消）
- 23-1・23-2・23-6の架空の実行結果、jpackageのインストーラーの説明、まとめの重複した文と付録Eへの二重の誘導、擬似見出しの行、余分な空行は、2026-11-09〜11-11の修正で解消した
- 2026-11-14の校閲で、ビルド・パッケージング・配布の定義の混同、23-3の抜粋がコンストラクターを省いて`TodoApp::new`の動きが読めない点、jpackageのクロスビルド不可の記述漏れ、まとめの「第22章で学んだMaven」（第22章はJARの作成を扱っていなかった）、「推奨される知識：」の形、ディレクトリ図の罫線の混在を直した
- 2026-11-16: 「今後の学習に向けて」の末尾に、第25章からの統合プロジェクトへの案内を1文加えた
- 2026-11-16: 「今後の学習に向けて」の案内を、第24章（データベース）と第25・26章（統合プロジェクト）の2文にした
- 2026-11-16: 節「よくあるエラーと対処法」を加えた（メイン・マニフェスト属性、NoClassDefFoundError、getResourceAsStreamのnull、UnsupportedClassVersionError、jpackage）。詳しい内容は`appendix/e-common-errors/chapters/chapter23.md`
- 2026-11-18の校閲: 「仕組み」を「しくみ」に、半角括弧を全角に、CI/CDの説明を「継続的インテグレーションと継続的デリバリー」に、jpackageのコマンドの続きの行の字下げをそろえ、エラーの項目を「`getResource`や`getResourceAsStream`」に、Fat JARの例を「Maven Shade Plugin、Spring Bootのプラグインなど」に直した
- 2026-11-23（流れ・矛盾・過不足の校閲）: ビルドの定義に、ビルドツールではコンパイルからテスト・パッケージングまでをまとめてビルドと呼ぶことが多く本書もその意味で使うことを足した（第21・22章の使い方と合わせた）。複数クラスのJARの後ろに、パッケージに属するクラスの場合（Main-Classに完全修飾名、javac -dとjar -Cでディレクトリごと入れる。Java 25で確かめた）と、mvn packageがこれを行うことを足した
- 2026-11-23（流れ・矛盾・過不足の校閲）: 第21〜23章の低の指摘: 第21章はJUnitの節をTDDの節より前へ移して先取りの断りをなくし、テストメソッドのpublicの有無を説明し、前提を第3・4章のクラスにした。第22章はクラスパスの定義（JARをクラスファイルの一種のように書いていた）、スコープのruntime・provided、Mockitoの座標（第21章と同じmockito-junit-jupiter）、22-6の抜粋にTypeReferenceの行を含め、まとめから本文にない話題（Ivy・サプライチェーンなど）を外した。第23章はapp-imageの補足を一覧に統合し、第26章が使う--java-optionsを足し、前提から第14章を外した。第2章の「JavaDoc」を第22章の「Javadoc」にそろえた
- 2026-11-24: 学習目標を「話題＋〜できるの子項目」に、まとめを本文の再掲から「判断の順序・持ち帰ってほしい1つ・次章へのつなぎ」の段落に書き直した（第7章の見本にそろえた）。初めて読むときは飛ばしてよい節に「発展」の印（{.advanced}）を付けた
