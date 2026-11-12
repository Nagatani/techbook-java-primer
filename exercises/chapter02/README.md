# 第2章 Java基本文法 - 演習課題

## 概要
このディレクトリは第2章「Java基本文法」の演習課題です。
どの課題も、第2章で学ぶ内容（変数とデータ型、演算子、型変換、条件分岐と繰り返し、配列、staticメソッド、文字列）だけで解けます。
クラスを自分で定義する課題（Personクラス、Bookクラス、Circleクラス、図書管理システムなど）は、第3章の演習（`exercises/chapter03/`）にあります。

## 学習目標
- 用途に合ったデータ型を選んで変数を宣言できる
- 算術演算子と整数除算・余りの性質を理解して計算できる
- if文・switch・for文・while文で処理の流れを組み立てられる
- 配列で複数の値をまとめて扱える
- 処理をstaticメソッドに分けられる
- Stringのメソッドで文字列を調べ、加工できる

## 課題構成

### 基礎課題（[basic/](basic/)）
1. **Exercise01_SelfIntroduction.java** - 変数を使った自己紹介
2. **Exercise02_BasicCalculation.java** - 四則演算と余り
3. **Exercise03_DataTypes.java** - データ型とリテラル
4. **Exercise04_ControlFlow.java** - 成績判定とFizzBuzz（条件分岐と繰り返し）
5. **Exercise05_Arrays.java** - 点数の集計（配列）
6. **Exercise06_StaticMethods.java** - staticメソッドの定義
7. **Exercise07_Strings.java** - 文字列の操作

### 発展課題（[advanced/](advanced/)）
1. **Calculator.java** - 高機能計算機
2. **PersonalInfo.java** - 個人情報の計算（BMIなど）
3. **TimeCalculation.java** - 時間の計算

### チャレンジ課題（[challenge/](challenge/)）
1. **NumberGuessingGame.java** - 数当てゲーム

### 解答例（[solutions/](solutions/)）
- 基礎課題1〜7の解答例
- 発展課題とチャレンジ課題は、各フォルダーのファイルそのものが解答例（完成したプログラム）

## 実行方法

```bash
javac Exercise01_SelfIntroduction.java
java Exercise01_SelfIntroduction
```

入力を受け取る課題（Exercise07_Strings、NumberGuessingGame）は、キーボードから入力するほか、パイプで入力を渡しても動かせます。
```bash
echo "I like Java programming" | java Exercise07_Strings
```

## 関連セクション
- 本書 第2章「Java基本文法」
    + 「基本データ型」「変数の宣言と初期化」「演算子」「型変換」
    + 「コンソール入出力」「制御構造」「配列」
    + 「メソッド（staticメソッドの基本）」「文字列処理」

## 評価基準
- [ ] プログラムがエラーなくコンパイルできる
- [ ] 実行例と同じ出力になる
- [ ] 変数名から中身がわかる（`x`より`price`）
- [ ] 整数除算や配列の範囲など、第2章で学んだ注意点を守っている

## 注意事項
- パッケージ宣言は使用しないでください（パッケージは第4章で学びます）
- 解答例は`solutions/`ディレクトリにありますが、まず自分で考えてみましょう
- 基礎課題が終わったら、第3章「オブジェクト指向の考え方」に進みましょう
