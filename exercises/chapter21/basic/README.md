# 第21章 基本課題

## 学習目標

- JUnit 5の`@Test`・`@BeforeEach`・`@DisplayName`・`@Nested`と、`assertEquals`・`assertTrue`・`assertThrows`などのアサーションを使う
- 正常な値だけでなく、境界の値（0、負の数、空の配列）と異常な値をテストする
- テストを先に書き、失敗を確かめてから実装する（Red・Green・Refactor）
- Mockitoで依存するオブジェクトを差し替え、呼び出しを`verify`で確かめる

## 課題一覧

このフォルダーのJavaファイルは、テストや実装を書き足して完成させる雛形です。
課題2と課題3の解答例は[solutions/](../solutions/)にあります。

### 課題1: JUnitの基本

**ファイル**: `JUnitBasics.java`（テスト対象の`SimpleCalculator`が入っている）、`SimpleCalculatorTest.java`（新しく作る）

できあがっている`SimpleCalculator`のテストを書きます。

**要求仕様**:

- `@BeforeEach`で、テストごとに新しい`SimpleCalculator`を作る
- `add`・`subtract`・`multiply`・`divide`を`assertEquals`で確かめる。`divide`は`double`を返すので、許す誤差を3つ目の引数に渡す
- 0で割ると`IllegalArgumentException`になり、メッセージが「除数は0にできません」であることを`assertThrows`で確かめる
- `isEven`・`isPositive`は`assertTrue`・`assertFalse`で、負の数と0も確かめる
- `max`は、負の数だけの配列と、空の配列・`null`（例外になる）を確かめる
- テストを`@Nested`で「四則演算」「判定」「最大値」にまとめ、`@DisplayName`で日本語の名前を付ける

**実行例**（9つのテストを書いた場合。JUnitのコンソールランチャーの最後の集計）:

```
[         9 tests found           ]
[         0 tests skipped         ]
[         9 tests started         ]
[         0 tests aborted         ]
[         9 tests successful      ]
[         0 tests failed          ]
```

テストの表示順は、書いた順とは限りません。テストは互いに頼らず、どの順に動いても通るように書きます。

### 課題2: テスト駆動開発で作る計算機

**ファイル**: `TestDrivenCalculator.java`（解答例は`solutions/TestDrivenCalculator.java`と`solutions/TestDrivenCalculatorTest.java`）

`calc.add(10).multiply(2).subtract(5)`のようにつなげて計算できる計算機を、テストを先に書きながら作ります。

**要求仕様**:

- `add`・`subtract`・`multiply`・`divide`は計算した結果を今の値にし、`this`を返す（メソッドをつなげて呼べる）
- 0で割ったら`hasError()`が`true`になり、`clear()`で今の値と誤りの状態が元に戻る
- 計算のたびに「10 + 5 = 15」の形（整数は小数点以下を付けない）で履歴を残し、`getHistory()`で返す。返すリストを書き換えても、計算機の中の履歴は変わらない
- 1つの機能ごとに「テストを書く → 失敗を確かめる → 通るだけの実装を書く → 整える」を繰り返す

**実行例**: 解答例のテスト（`TestDrivenCalculatorTest`）は32件で、すべて通ります。解答例の計算機には、べき乗・平方根・百分率など、要求仕様より多くの機能があります。

### 課題3: モックを使ったテスト

**ファイル**: 解答例`solutions/MockExample.java`を読み、同じ形のテストを自分で書く

メールの送信やデータベースのように、テストのたびに本物を使えない相手を、Mockitoのモックに差し替えてテストします。

**要求仕様**:

- テスト対象のクラスは、依存する相手をインターフェイスで受け取る（コンストラクターで渡す）
- `@ExtendWith(MockitoExtension.class)`と`@Mock`でモックを作り、`when(...).thenReturn(...)`で返す値を決める
- 処理のあと、`verify(モック).メソッド(...)`で、決めた引数で呼ばれたことを確かめる。呼ばれてはいけない場合は`never()`を使う

**実行例**: 解答例のテストは13件で、すべて通ります。

## コンパイルと実行

JUnitとMockitoのJARが要ります。JARの入手と、MavenやGradleでの設定は、第21章の本文と[docs/code-verification.md](../../../docs/code-verification.md)を参照してください。

```bash
# junit-platform-console-standalone-1.14.4.jar を同じフォルダーに置いた場合
javac -cp junit-platform-console-standalone-1.14.4.jar -d out JUnitBasics.java SimpleCalculatorTest.java
java -jar junit-platform-console-standalone-1.14.4.jar execute -cp out --scan-class-path
```

## ヒント

- テストの名前（`@DisplayName`）には、「何をすると、どうなるか」を書く。失敗したときに、何が壊れたかがすぐわかる
- 1つのテストで確かめることは1つにする。複数をまとめて確かめたいときは`assertAll`を使うと、1つ目が失敗しても残りの結果が見られる
- `double`の計算結果は誤差を含むので、`assertEquals(期待値, 実際の値, 誤差)`で比べる
- モックで確かめるのは「呼ばれたか」だけにし、テスト対象の結果は`assertEquals`で確かめる。モックの設定が多すぎるときは、テスト対象のクラスの責任が多すぎないかを見直す

## 完了チェックリスト

- [ ] 課題1: 正常な値・境界の値・異常な値のテストを書ける
- [ ] 課題1: 例外が起きることを`assertThrows`で確かめられる
- [ ] 課題2: テストを先に書き、失敗を確かめてから実装できる
- [ ] 課題3: 依存する相手をモックに差し替え、呼び出しを`verify`で確かめられる

**次のステップ**: 基本課題が終わったら、[advanced/](../advanced/)の発展課題に進みましょう。
