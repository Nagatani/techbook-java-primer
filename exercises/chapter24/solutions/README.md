# 第24章 統合プロジェクトの設計 - 解答例

設計の課題の解答例は、考え方の一例です。
要求の前提が違えば、別の答えになります。

## 構成

```
solutions/
├── library-design.md          基礎課題1: 図書の貸し出しシステムの要求と設計判断記録
├── grade-history/             基礎課題2: 成績の訂正の履歴
│   ├── GradeChange.java         訂正の記録（Record）
│   ├── Transcript.java          履歴を残すように変えた成績表
│   ├── Student.java             getGradeHistory()を加えた
│   ├── GradeHistoryCheck.java   動作を確かめるプログラム
│   └── （Grade・Enrollmentなどはサンプルコード24-2と同じ）
├── repository-interface/      発展課題1: フェーズ3のリポジトリのインターフェイス化
│   ├── StudentRepository.java          インターフェイスにした
│   ├── InMemoryStudentRepository.java  LinkedHashMap 1つで保存する実装
│   ├── StudentService.java             実装をコンストラクターで受け取り、集計を引き受けた
│   ├── RepositorySwapCheck.java        別の実装を渡せることを確かめるプログラム
│   └── （Person・Phase3Testなどはフェーズ3と同じ）
└── challenge-guide.md         チャレンジ課題1: 解答の方針（コードはなし）
```

## 実行方法

```bash
cd grade-history
javac -d out *.java
java -cp out GradeHistoryCheck

cd ../repository-interface
javac -d out *.java
java -cp out RepositorySwapCheck
java -cp out Phase3Test
```

## 基礎課題2のポイント

- `Map.put`は、置き換えた前の値を返す。前の値があり、新しい値と異なるときだけ履歴に加える
- `Grade`はRecordなので、`equals`で科目名・単位数・評価点がすべて等しいかを比べられる
- GPAは成績からいつでも計算できるので保存しない。訂正前の成績は、あとから計算で求められない事実なので保存する

## 発展課題1のポイント

- インターフェイスには保存に必要な4つのメソッドだけを置き、検索と集計は`StudentService`で`findAll`から計算する。こうすると、データベースの実装を作るときに書くメソッドが少なくて済む
- 引数なしのコンストラクターを残したので、`Phase3Test`は1行も変えずに動く
- 学部別の人数は`TreeMap`に集めて学部名の順にした。フェーズ3は`HashMap`だったので、表示の順だけが変わる（人数は同じ）
- `RepositorySwapCheck`は、呼ばれた回数を数える実装を渡し、`StudentService`を変えずに実装を差し替えられることを示す
