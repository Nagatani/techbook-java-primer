# 第9章 コレクションフレームワーク - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題の雛形の`TODO`を埋めた形で書いてあり、第9章までに学んだ機能だけを使っています。
後の章で学ぶ機能を使うと短く書ける箇所には、その書き方をコメントで添えています。

## 解答例一覧

| 課題 | ファイル | 使うコレクション |
|------|----------|------------------|
| 課題1: 学生管理 | `Student.java`、`StudentManager.java`、`StudentManagerDemo.java` | `ArrayList`（登録順）と`HashMap`（IDで検索） |
| 課題2: ショッピングカート | `Product.java`、`CartItem.java`、`ShoppingCart.java`、`ShoppingCartDemo.java` | `LinkedHashMap`（入れた順を保つ） |
| 課題3: 単語カウンター | `WordCounter.java` | `HashMap`（回数を数える）と`TreeMap`（アルファベット順） |
| 課題4: 重複の除去と集合演算 | `SetOperations.java` | `LinkedHashSet`・`TreeSet` |

テストは`StudentManagerTest.java`・`ShoppingCartTest.java`・`CollectionTasksTest.java`（課題3・4）の16件です。

各プログラムの出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

解答例はパッケージ`chapter09.solutions`にあるので、`exercises/`フォルダーでコンパイルします。

```bash
cd exercises
javac -d out $(ls chapter09/solutions/*.java | grep -v Test)
java -cp out chapter09.solutions.StudentManagerDemo
java -cp out chapter09.solutions.ShoppingCartDemo
java -cp out chapter09.solutions.WordCounter
java -cp out chapter09.solutions.SetOperations
```

テストには、JUnit 5の`junit-platform-console-standalone`のJAR（Maven Centralから入手）を使います。

```bash
cd exercises
javac -d out -cp junit-platform-console-standalone-1.13.4.jar chapter09/solutions/*.java
java -jar junit-platform-console-standalone-1.13.4.jar execute -cp out --select-package chapter09.solutions
```

## 実装のポイント

- `Student`の`equals`と`hashCode`は、学生IDだけで比べる。`List`の`remove`・`contains`も`equals`で探すので、マップから取り出した学生をリストからも同じように除ける
- `StudentManager`の追加と削除では、リストとマップを必ず一緒に更新する。片方だけを更新すると、一覧と検索の結果が食い違う
- 成績順の表示は`new ArrayList<>(studentList)`で作ったコピーを並べ替え、元のリストの登録順を残す
- `ShoppingCart`の`addItem`は、カートにすでに入っている数量と足す数量の合計を在庫と比べる。足す数量だけを比べると、何回かに分けて入れたときに在庫を超えてしまう
- 集合演算の`addAll`・`retainAll`・`removeAll`は、呼び出した集合そのものを書き換える。元の集合をコピーしてから操作する

## 後の章の機能を使った書き方

コメントに書いた別の書き方をまとめます。
どれも第9章の書き方と同じ結果になります。

| 箇所 | 第9章の書き方 | 後の章の機能を使った書き方 |
|------|---------------|----------------------------|
| 成績の降順の並べ替え | 匿名クラスの`Comparator`（第8章） | ラムダ式`(a, b) -> Double.compare(b.getScore(), a.getScore())`、`Comparator.comparingDouble(Student::getScore).reversed()`（第11章） |
| 学年での絞り込み | 拡張for文と`if` | `stream().filter(...).toList()`（第12章） |
| 平均・合計 | 拡張for文で足す | `mapToDouble(...).average()`・`sum()`（第12章） |
| 単語の数え上げ | `put(word, getOrDefault(word, 0) + 1)` | `merge(word, 1, Integer::sum)`（第11章） |
