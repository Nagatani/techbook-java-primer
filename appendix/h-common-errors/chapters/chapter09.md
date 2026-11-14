# 第9章 コレクションフレームワーク：よくあるエラーと対処法

書籍『Java入門』第9章「コレクションフレームワーク」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

コレクションフレームワークを学習する際によく遭遇するエラーとその対処法を以下にまとめます。

## 初期化忘れによるNullPointerException

### エラーメッセージ

```
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "java.util.List.add(Object)" because "this.items" is null
```

Java 14以降は、どの参照が`null`だったかがメッセージに表示されます。

### 原因と対処法

以下のコードは、フィールドのコレクションを初期化せずに使用しようとした例です。
初期化していないフィールドには`null`が入っているため、メソッドを呼び出すと`NullPointerException`になります。

```java
// エラー例
public class ShoppingCart {
    private List<String> items;  // 宣言のみ、初期化していない（nullが入る）

    public void add(String item) {
        items.add(item);  // NullPointerException
    }
}

// 修正版
public class ShoppingCart {
    private List<String> items = new ArrayList<>();  // 初期化を忘れずに

    public void add(String item) {
        items.add(item);  // OK
    }
}
```

なお、メソッドの中で宣言したローカル変数は、初期化せずに使うと実行前にコンパイルエラー（`variable list might not have been initialized`）になります。

## null要素の扱い

### 原因と対処法

次のコードは、null要素を含むSetを扱う際の注意点を示しています。

```java
// エラー例
Set<String> set = new HashSet<>();
set.add(null);  // HashSetはnullを1つだけ保持可能
set.add(null);  // 重複は除去される

String first = set.iterator().next();
int length = first.length();  // first がnullの場合はNullPointerException

// 修正版
Set<String> set = new HashSet<>();
set.add("Hello");
set.add("World");

for (String s : set) {
    if (s != null) {  // null チェック
        IO.println(s.length());
    }
}
```

## ジェネリクスを使わない場合のClassCastException

### エラーメッセージ

```
Exception in thread "main" java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String (java.lang.Integer and java.lang.String are in module java.base of loader 'bootstrap')
```

### 原因と対処法

以下のコードは、ジェネリクスを使用しない場合の問題を示しています。

```java
// エラー例（ジェネリクスなし）
List list = new ArrayList();  // Raw type（非推奨）
list.add("Hello");
list.add(123);  // 異なる型も追加可能

String s = (String) list.get(1);  // ClassCastException

// 修正版（ジェネリクス使用）
List<String> list = new ArrayList<>();  // 型を明示
list.add("Hello");
// list.add(123);  // コンパイルエラーで防げる

String s = list.get(0);  // キャストが不要、型安全
```

## 不変リストの変更によるUnsupportedOperationException

### エラーメッセージ

```
Exception in thread "main" java.lang.UnsupportedOperationException
```

### 原因と対処法

次のコードは、不変リストを変更しようとした場合の問題を示しています。

```java
// エラー例
List<String> list = List.of("A", "B", "C");  // 不変リスト（Java 9+）
list.add("D");  // UnsupportedOperationException

// 修正版1：可変リストを作成
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
list.add("D");  // OK

// 修正版2：不変の特性を理解して使用
List<String> immutableList = List.of("A", "B", "C");
List<String> mutableList = new ArrayList<>(immutableList);
mutableList.add("D");  // OK
```

## 反復中の変更によるConcurrentModificationException

### エラーメッセージ

```
Exception in thread "main" java.util.ConcurrentModificationException
```

### 原因と対処法

以下のコードは、反復処理中にコレクションを変更しようとした例です。

```java
// エラー例
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add("C");
list.add("D");

for (String s : list) {
    if (s.equals("B")) {
        list.remove(s);  // 次の反復でConcurrentModificationException
    }
}

// 修正版1：Iteratorを使用
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (s.equals("B")) {
        it.remove();  // Iteratorのremoveメソッドを使用
    }
}

// 修正版2：removeIfメソッドを使用（Java 8+）
list.removeIf(s -> s.equals("B"));  // 安全な削除

// 修正版3：逆順でインデックスループ
for (int i = list.size() - 1; i >= 0; i--) {
    if (list.get(i).equals("B")) {
        list.remove(i);  // インデックスが前にずれる影響を回避
    }
}
```

最後から2番目の要素を削除したときは、残りの要素数と反復の位置が一致して`hasNext()`が`false`を返すため、例外が出ずにループが終わります。
このとき最後の要素は調べられないので、例外が出なくても正しく動いているわけではありません。
反復中にリストから直接削除する書き方は、どの要素を消す場合でも避けます。

## リストの範囲外アクセスによるIndexOutOfBoundsException

### エラーメッセージ

```
Exception in thread "main" java.lang.IndexOutOfBoundsException: Index 5 out of bounds for length 3
```

### 原因と対処法

次のコードは、リストの範囲外にアクセスしようとした例です。

```java
// エラー例
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add("C");

int index = 5;
String item = list.get(index);  // IndexOutOfBoundsException

// 修正版1：サイズをチェック
if (index >= 0 && index < list.size()) {
    String item = list.get(index);  // 安全なアクセス
} else {
    IO.println("無効なインデックス: " + index);
}

// 修正版2：try-catchで例外処理
try {
    String item = list.get(index);
    IO.println(item);
} catch (IndexOutOfBoundsException e) {
    IO.println("リストの範囲外です: " + e.getMessage());
}
```

## Mapで存在しないキーへのアクセス

### 原因と対処法

Mapで存在しないキーにアクセスする際の注意点を示すコード例です。

```java
// 注意が必要な例
Map<String, Integer> map = new HashMap<>();
map.put("key1", 100);

Integer value = map.get("key2");  // null が返される
int result = value + 10;  // NullPointerException

// 修正版1：null チェック
Integer value = map.get("key2");
if (value != null) {
    int result = value + 10;
} else {
    IO.println("キーが存在しません");
}

// 修正版2：getOrDefaultメソッドを使用
int value = map.getOrDefault("key2", 0);  // デフォルト値0
int result = value + 10;  // 安全

// 修正版3：containsKeyで事前チェック
if (map.containsKey("key2")) {
    int value = map.get("key2");
    int result = value + 10;
} else {
    IO.println("キーが存在しません");
}
```

## 自作クラスをHashSetやHashMapのキーに使うときのequals()とhashCode()

### 原因と対処法

カスタムオブジェクトをHashSetやHashMapで使用する際の問題を示すコード例です。

```java
// 問題のある例
class Person {
    private String name;
    
    public Person(String name) {
        this.name = name;
    }
    
    // equals()とhashCode()をオーバーライドしていない
}

Set<Person> persons = new HashSet<>();
persons.add(new Person("Alice"));
persons.add(new Person("Alice"));  // 同じ名前だが別のオブジェクト

IO.println(persons.size());  // 2（重複削除されない）

// 修正版：equals()とhashCode()を適切に実装
class Person {
    private String name;
    
    public Person(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(name, person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
```

## Raw typeの使用

型引数を省略したRaw typeの問題と対処法は、第10章で扱います。
[第10章の項目「Raw typeの使用エラー」](chapter10.md#raw-typeの使用エラー)を参照してください。

## デバッグのヒント

1. 型安全なジェネリクスの使用
   - Raw typeを避け、常に型パラメーターを指定
   - コンパイル時に型の安全性を確保

2. null安全なコーディング
   - 要素をコレクションに追加する前にnullチェック
   - getOrDefaultやOptionalの活用

3. 安全なイテレーション
   - 拡張for文やStream APIの活用
   - 反復中の変更を避ける

4. equals()とhashCode()の実装
   - カスタムオブジェクトをコレクションで使用する場合は必須
   - IDEの自動生成機能の活用や、値を保持するだけのクラスならRecord（第8章）の利用

5. 例外処理の実装
   - IndexOutOfBoundsExceptionやNullPointerExceptionの適時捕捉と処理
   - 防御的プログラミングの実践

これらの対処法を参考に、安全で正確なコレクション操作を実装してください。
