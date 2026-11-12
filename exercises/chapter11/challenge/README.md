# 第11章 チャレンジ課題

## 学習目標
- 引数から条件（`Predicate`）を作って返す高階関数を書く
- `Predicate`の`and`・`or`・`negate`で条件を組み合わせる
- `Function`の`andThen`で表示の形を組み立てる
- `Comparator`で複数の条件の並べ方を作り、メソッドの引数として渡す
- `Supplier`・`UnaryOperator`と、`replaceAll`・`removeIf`・`forEach`のようにラムダ式を受け取るコレクションのメソッドを使う
- ラムダ式から参照できる変数の制限（実質的にfinal）を確かめる

## 課題一覧

### 課題1: ToDoリスト
**ファイル**（このフォルダーのファイルは解答例です）:
```
challenge/
├── Priority.java     優先度（列挙型）
├── Task.java         タスク（不変のレコード）
├── TaskFilters.java  条件を作る高階関数
├── TodoList.java     タスクをまとめて管理するToDoリスト
└── TodoApp.java      mainメソッド
```

タスクを登録し、条件で絞り込み、並べ替え、まとめて更新・削除できるToDoリストを作成してください。
日付は「1日」「12日」のような日の番号（整数）で扱い、今日は5日とします。

`TodoList`は「どのタスクを」「どの順に」「どう変えるか」「どう表示するか」を自分では決めず、すべて関数として引数で受け取ります。
新しい絞り込み方や表示の形を足しても、`TodoList`を直さずに済むようにするのがねらいです。

**要求仕様**:
- `Priority`は高・中・低の3つの列挙型にする。宣言した順（高 → 低）を並べ替えに使う
- `Task`（タスク）
    - タスク番号・題名・優先度・期限・完了したかどうかを持つレコードにする
    - 完了にする`complete()`と、期限を延ばす`postpone(int days)`は、元のタスクを変えずに新しい`Task`を返す
- `TaskFilters`（条件を作る高階関数）
    - `notDone()`・`dueBy(int day)`・`priorityIs(Priority p)`・`titleContains(String word)`は、どれも`Predicate<Task>`を返す
- `TodoList`（ToDoリスト）
    - タスク番号（T1、T2、…）は、`Supplier<String>`で作る
    - `select(Predicate<Task>)`と`select(Predicate<Task>, Comparator<Task>)`で、条件に合うタスクを返す（後者は並べ替えて返す）
    - `update(Predicate<Task>, UnaryOperator<Task>)`は、条件に合うタスクを変換した結果で置き換え、置き換えた数を返す（`replaceAll`を使う）
    - `remove(Predicate<Task>)`は、条件に合うタスクを消し、消した数を返す（`removeIf`を使う）
    - `print(List<Task>, Function<Task, String>)`は、タスクを関数で文字列にして表示する
- `TodoApp`（mainメソッド）
    - 「未完了かつ今日までが期限」「未完了かつ期限切れ」「優先度が低くないかつ未完了」の条件を、`TaskFilters`の条件を`and`・`or`・`negate`で組み合わせて作る
    - 「数学」または「図書館」を題名に含むタスクを、メソッド参照`Task::complete`で完了にする
    - 期限切れのタスクの期限を、ラムダ式`task -> task.postpone(3)`で延ばす
    - 完了したタスクを、メソッド参照`Task::done`を条件にして消す
    - 表示の形は、基本の形の`Function`に`andThen`で字下げを加えて作る

**実行例**:
```
=== すべてのタスク（優先度順） ===
  T4 [高] 数学の課題を解く（5日まで）
  T1 [高] レポートを書く（7日まで）
  T2 [中] 図書館に本を返す（4日まで）
  T5 [中] 英語の単語テストの勉強（6日まで）
  T6 [低] 自転車を修理に出す（3日まで）
  T3 [低] サークルの会費を払う（10日まで）

=== 今日（5日）までにやること（期限順） ===
  T6 [低] 自転車を修理に出す（3日まで）
  T2 [中] 図書館に本を返す（4日まで）
  T4 [高] 数学の課題を解く（5日まで）

=== 完了にする ===
  2件を完了にしました
  T6 [低] 自転車を修理に出す（3日まで）
  T2 [中] 図書館に本を返す（4日まで） 完了
  T4 [高] 数学の課題を解く（5日まで） 完了
  T5 [中] 英語の単語テストの勉強（6日まで）
  T1 [高] レポートを書く（7日まで）
  T3 [低] サークルの会費を払う（10日まで）

=== 期限切れで残っているタスク ===
  T6 [低] 自転車を修理に出す（3日まで）

=== 期限切れのタスクを3日延ばす ===
  1件の期限を延ばしました

=== 優先度が低くないもの（未完了、期限順） ===
  T5 [中] 英語の単語テストの勉強（6日まで）
  T1 [高] レポートを書く（7日まで）

=== 完了したタスクを片付ける ===
  2件を削除しました

=== 残りのタスク（期限順） ===
  T5 [中] 英語の単語テストの勉強（6日まで）
  T6 [低] 自転車を修理に出す（6日まで）
  T1 [高] レポートを書く（7日まで）
  T3 [低] サークルの会費を払う（10日まで）
```

## コンパイルと実行

```bash
javac *.java
java TodoApp
```

## 実装のヒント

```java
public class TaskFilters {
    // 引数のdayをラムダ式の中で使う。dayは実質的にfinalなので参照できる
    public static Predicate<Task> dueBy(int day) {
        return task -> task.dueDay() <= day;
    }
}

// 条件を組み合わせて、新しい条件を作る
Predicate<Task> dueToday = TaskFilters.notDone().and(TaskFilters.dueBy(today));
Predicate<Task> notLow = TaskFilters.priorityIs(Priority.LOW).negate();

// 表示の形をつなげる
Function<Task, String> withMark = basic.andThen(text -> "  " + text);
```

- 列挙型は宣言した順が自然な順序なので、`Comparator.comparing(Task::priority)`で高 → 中 → 低の順になる。続けて`thenComparingInt(Task::dueDay)`で、同じ優先度の中を期限順にする
- `Task::complete`は不特定のインスタンスのメソッド参照で、`task -> task.complete()`と同じ意味になる。`UnaryOperator<Task>`として渡せる
- `Task::done`は`boolean`を返すので、`Predicate<Task>`として`removeIf`に渡せる
- ラムダ式の中からローカル変数を書き換えることはできない（実質的にfinalでなければならない）。タスク番号の連番はフィールド`nextNumber`に持たせると、`Supplier`のラムダ式の中で増やせる
- `update`で置き換えた数を数えるには、ラムダ式の外の変数を書き換えられないため、要素1つの配列を使うか、`replaceAll`を使わずに拡張for文とインデックスで書く
- Stream APIは第12章で学ぶので、ここでは拡張for文と、ラムダ式を受け取るコレクションのメソッドだけで書く

## 応用のポイント

1. 高階関数
    - `dueBy(today)`のように、値を受け取って条件を返すメソッドがあると、似た条件を何度も書かずに済む
2. 関数の合成
    - `and`・`or`・`negate`・`andThen`・`thenComparing`は、どれも元の関数を変えずに新しい関数を作る。小さな部品を組み合わせて複雑な条件を表せる
3. 不変のデータと副作用の位置
    - タスクは不変にし、書き換えは`TodoList`の`update`と`remove`だけで行う。ラムダ式の中で外の状態を書き換える箇所を少なくすると、処理の流れを追いやすい

## 発展（任意）

- `TaskFilters`に「期限が今日から3日以内」の条件を作る`dueWithin(int today, int days)`を加える
- 表示の形を、`Map<String, Function<Task, String>>`に名前を付けて登録し、名前で切り替えられるようにする
- 独自の関数型インターフェイス`TaskAction`（`void run(Task task)`）を作り、選んだタスクに順に処理を行うメソッドを`TodoList`に加える。`Consumer<Task>`を使う場合と比べる

## 完了チェックリスト

- [ ] `TodoList`が、絞り込み・並べ替え・更新・表示の方法をすべて引数の関数で受け取っている
- [ ] `TaskFilters`のメソッドが`Predicate<Task>`を返している
- [ ] `and`・`or`・`negate`で組み合わせた条件で絞り込めている
- [ ] `Task::complete`や`Task::done`のメソッド参照を使っている
- [ ] タスクを変えるとき、元の`Task`を書き換えずに新しい`Task`で置き換えている
- [ ] Stream APIを使わずに書けている

**次のステップ**: チャレンジ課題が完了したら、第12章「Stream APIと高度なコレクション操作」に進みましょう。
