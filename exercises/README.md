# Java入門書 練習課題

この`exercises`ディレクトリには、『Java入門』各章の練習課題が格納されています。
段階的にスキルを習得し、確実にJavaプログラミングの力を身につけるための実践的な課題集です。

## 📋 練習課題の構成

各章は以下の4つのレベルに分かれています：

### 📚 basic/ - 基本課題【必須】
- **目的**: その章の核心概念を確実に習得
- **内容**: 基本的なプログラム作成、概念理解の確認
- **評価**: 正しく動作し、基本概念を理解していれば合格
- **推奨時間**: 30分〜1時間

### 🚀 advanced/ - 発展課題【推奨】
- **目的**: 応用力と問題解決能力の育成
- **内容**: より複雑な問題、複数概念の組み合わせ
- **評価**: 効率的な解法、コードの品質も重視
- **推奨時間**: 1〜2時間

### 💪 challenge/ - チャレンジ課題【任意】
- **目的**: 深い理解と創造性の発揮
- **内容**: 高度な実装、最適化、調査課題
- **評価**: 独創性、技術的深さを評価
- **推奨時間**: 2時間以上

### ✅ solutions/ - 解答例
- **内容**: 模範解答、複数のアプローチ、よくあるミスの解説
- **活用法**: 自分の解答と比較、別解法の学習
- **注意**: まずは自力で取り組んでから参照すること

## 🎯 学習の進め方

### ステップ1: 基本課題に挑戦
1. **教科書の該当章を読む**
2. **basic/の課題に取り組む**
3. **動作確認と自己評価**
4. **解答例で学習ポイントを確認**

### ステップ2: 発展課題で応用力アップ
1. **advanced/の課題に挑戦**
2. **複数のアプローチを検討**
3. **コードの品質を意識**
4. **解答例で最適化手法を学習**

### ステップ3: チャレンジ課題で実力向上
1. **challenge/の課題に挑戦**（時間があるとき）
2. **調査・実験も含めて取り組む**
3. **独自のアイデアを盛り込む**

## 📊 自己評価基準

### 基本課題の合格基準
- [ ] プログラムが正常に動作する
- [ ] その章の核心概念を使用している
- [ ] コードが読みやすく、適切にコメントされている
- [ ] 基本的なエラー処理ができている

### 発展課題の合格基準
- [ ] 基本課題の基準をクリア
- [ ] 効率的なアルゴリズムを選択している
- [ ] 複数の概念を適切に組み合わせている
- [ ] ユーザビリティを考慮している

### チャレンジ課題の評価ポイント
- [ ] 技術的な深さと創造性
- [ ] 最適化とパフォーマンス
- [ ] 拡張性と保守性
- [ ] 調査・学習の成果

## 🔧 開発環境の設定

### 必要なツール
- **Java Development Kit (JDK)**: バージョン17以上推奨
- **統合開発環境**: IntelliJ IDEA、Eclipse、Visual Studio Codeなど
- **ビルドツール**: Maven または Gradle（後の章で使用）

### ディレクトリ構成の活用
```
exercises/chapter01/
├── basic/
│   ├── Exercise01_HelloWorld.java    # 課題ファイル
│   ├── Exercise02_Variables.java     # 課題ファイル
│   └── README.md                     # 課題の説明
├── advanced/
│   ├── Calculator.java               # 発展課題
│   └── README.md
├── challenge/
│   ├── EnvironmentInfo.java          # チャレンジ課題
│   └── README.md
└── solutions/
    ├── basic/                        # 基本課題の解答
    ├── advanced/                     # 発展課題の解答
    └── challenge/                    # チャレンジ課題の解答
```

## 💡 学習のコツ

### 1. 段階的な取り組み
- 無理をせず、基本課題から確実に進める
- 前の章の内容を忘れていたら復習する
- 理解できないときは教科書に戻る

### 2. 実際に手を動かす
- コードを写すだけでなく、自分で考えて書く
- エラーが出ても慌てず、原因を考える
- 動作確認は必ず行う

### 3. 他の解法も考える
- 一つの解法ができたら、別のアプローチも試す
- より効率的な方法はないか検討する
- 他の人のコードも参考にする

### 4. アウトプットを意識
- 解いた課題について、誰かに説明してみる
- 学習ブログやノートにまとめる
- コードにコメントを書いて理解を深める

## 🆘 困ったときは

### よくある質問
1. **コンパイルエラーが出る**
   → エラーメッセージを読み、文法を確認
   
2. **実行結果が期待と違う**
   → デバッガやprint文で変数の値を確認
   
3. **課題の意図がわからない**
   → 教科書の該当箇所を再読、README.mdを確認

### 学習サポート
- 各課題のREADME.mdに詳細な説明があります
- solutions/ディレクトリに解答例とポイント解説があります
- わからない場合は基礎に戻って復習しましょう



**🎓 学習の成果**: すべての基本課題をクリアすることで、実用的なJavaアプリケーションを開発する土台が身につきます。発展課題とチャレンジ課題により、さらに実践的なスキルを磨くことができます。

## 📚 章別課題一覧

### 基礎編
- **第1章 Java入門と開発環境構築**: Hello World、基本的な出力、データ型
- **第2章 Java基本文法**: クラスとオブジェクト、制御構造、配列
- **第3章 オブジェクト指向の考え方**: メソッド設計、学生管理、車のシミュレータ
- **第4章 クラスとインスタンス**: カプセル化、書籍管理、製品管理

### オブジェクト指向編
- **第5章 継承とポリモーフィズム**: 動物クラス階層、図形クラス階層
- **第6章 不変性とfinal**: 不変オブジェクト、スレッドセーフ設計
- **第7章 抽象クラスとインターフェイス**: 抽象化、契約による設計

### コレクション編
- **第8章 コレクションフレームワーク**: List/Set/Map、学生管理システム
- **第9章 ジェネリクス**: 型安全なコレクション、カスタムコレクション
- **第10章 高度なコレクション操作**: Stream API、データ分析

### モダンJava編
- **第11章 ラムダ式と関数型インターフェイス**: 関数型プログラミング
- **第12章 レコード**: 不変データクラス、DTO設計
- **第13章 列挙型**: 状態管理、定数管理

### 実践編
- **第14章 例外処理**: カスタム例外、リソース管理
- **第15章 ファイル入出力**: ファイル処理、CSV処理、NIO.2
- **第16章 マルチスレッドプログラミング**: 並行処理、スレッドプール
- **第17章 ネットワークプログラミング**: TCP/UDP通信、HTTP通信

### GUI編
- **第18章 GUIプログラミング基礎**: Swing基礎、レイアウト
- **第19章 GUIのイベント処理**: イベントハンドリング、インタラクション
- **第20章 高度なGUIコンポーネント**: MVC設計、データ表示

### プロフェッショナル編
- **第21章 ユニットテストと品質保証**: JUnit、TDD、モックオブジェクト
- **第22章 ドキュメンテーションとライブラリ**: Javadoc、ライブラリ開発
- **第23章 ビルドとデプロイ**: Maven/Gradle、JAR作成、CI/CD

### 総合演習
- **comprehensive/**: 複数章の知識を統合した総合的なプロジェクト課題

*良いJavaプログラマーになるための第一歩を、この練習課題とともに踏み出しましょう！*