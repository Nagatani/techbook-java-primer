# Java入門 サンプルコード集

本ディレクトリには、書籍「Java入門」のすべてのサンプルコードが、コンパイル可能な形で整理されています。

## ディレクトリ構造

各サンプルコードは、リスト番号ごとのディレクトリに格納されています：

```
sample-code/
├── chapter01/          # 第1章
│   ├── list1-01/      # リスト1-1
│   │   ├── Product.java
│   │   └── README.md
│   ├── list1-02/      # リスト1-2
│   │   ├── HelloWorld.java
│   │   └── README.md
│   └── ...
├── chapter02/          # 第2章
│   ├── list2-27-28/   # リスト2-27〜2-28（関連クラス）
│   │   ├── BankAccount.java
│   │   ├── BankExample.java
│   │   └── README.md
│   └── ...
└── appendix/          # 付録
    ├── aa/            # 付録A
    ├── ab/            # 付録B
    └── ac/            # 付録C
```

## 特徴

### 1. ファイル名とクラス名の一致
すべてのJavaファイルは、含まれるpublicクラスの名前と一致するファイル名になっています。

### 2. 関連クラスのグループ化
複数のクラスが協調して動作するサンプルは、同一ディレクトリにまとめられています。

### 3. コードスニペットの扱い
完全なクラスではないコード片は、`CodeSnippet.java`または`CodeSnippet[番号].java`として保存されています。

## 使い方

### 1. 単一クラスの実行

```bash
# リスト2-15（Calculator）の例
cd sample-code/chapter02/list2-15
javac Calculator.java
java Calculator
```

### 2. 複数クラスの実行

```bash
# リスト2-27〜2-28（BankAccountとBankExample）の例
cd sample-code/chapter02/list2-27-28
javac *.java
java BankExample
```

### 3. 各ディレクトリのREADME

各リストのディレクトリには、そのコードの説明と実行方法を記載したREADMEが含まれています。

## 注意事項

- 一部のコードは説明用のスニペットのため、そのままでは実行できない場合があります
- CodeSnippet.javaという名前のファイルは、完全なクラスではないコード片を含んでいます
- パッケージ宣言は含まれていないため、該当ディレクトリ内で直接コンパイルしてください
