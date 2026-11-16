# 第26章 統合プロジェクトの技術選定と実装 - 演習課題

## 概要
本章では、ライブラリやデータベースを選ぶ基準と、選んだ技術を1つのクラスに閉じ込める設計、初版から改善版へ小さな手順で移す進め方を学びました。
演習では、ライブラリを実際に調べて比べる課題と、選んだ技術で`StudentRepository`やCSVの読み込みを実装する課題に取り組みます。

## 課題構成

### 基礎課題（basic/）
- **課題1**: JSONを読み書きするライブラリの候補を、本章の観点で調べて比べる（解答例は`solutions/json-library-comparison.md`）
- **課題2**: Apache Commons CSVを使って、成績のCSVファイルを読み込むクラスを作る（解答例は`solutions/grade-csv/`）

### 発展課題（advanced/）
- **課題1**: `StudentRepository`を実装し、H2データベースに保存するクラスを作る（解答例は`solutions/h2-repository/`）

### チャレンジ課題（challenge/）
- **課題1**: 初版のフェーズ6（`sample-code/integrated-project/phase6/`）のアプリケーションに、学生の情報を変更する機能を加える（解答の方針は`solutions/challenge-guide.md`。コードの解答例はありません）

## 実行方法
外部ライブラリのJARファイルをMaven Centralから入手し、クラスパスに加えてコンパイル・実行します（Java 25）。
macOS・Linuxではクラスパスの区切りが`:`、Windowsでは`;`です。

```bash
# 基礎課題2（commons-csv 1.14.1、commons-io 2.20.0、commons-codec 1.19.0）
javac -cp "commons-csv-1.14.1.jar:commons-io-2.20.0.jar:commons-codec-1.19.0.jar" -d out *.java
java -cp "out:commons-csv-1.14.1.jar:commons-io-2.20.0.jar:commons-codec-1.19.0.jar" GradeCsvCheck

# 発展課題1（h2 2.5.252）
javac -cp h2-2.5.252.jar -d out *.java
java -cp "out:h2-2.5.252.jar" H2RepositoryCheck
```

## 評価基準
- ライブラリの比較に、確かめた場所（Maven Central、pom.xmlなど）と日付が書かれていること
- 外部ライブラリのクラスを使う場所が、1つのクラスに閉じ込められていること
- 1行の誤りで全体を止めず、誤りのある行と理由を報告すること
- `StudentService`を変えずに、保存先を差し替えられること
