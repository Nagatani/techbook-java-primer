# ConcurrentModificationException

## 概要

`ConcurrentModificationException`は、コレクションの反復処理中に、そのコレクションの構造を変更（要素の追加・削除）した際に発生する例外です。これは必ずしもマルチスレッド環境でのみ発生するわけではなく、シングルスレッドでも発生します。

## 発生原因

1. **拡張for文中の構造変更**: 反復処理中の要素削除・追加
2. **複数イテレーターの同時使用**: 同じコレクションに対する複数のイテレーター
3. **マルチスレッド環境**: 同期化されていないコレクションへの並行アクセス
4. **ネストした反復処理**: 内側のループで外側のコレクションを変更

## 典型的な発生パターン

### パターン1：拡張for文での削除

```java
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

// 間違い：拡張for文中での削除
for (String item : list) {
    if (item.equals("B")) {
        list.remove(item);  // ConcurrentModificationException
    }
}
```

### パターン2：条件付き削除

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 85);
scores.put("Bob", 70);
scores.put("Charlie", 60);

// 間違い：反復処理中の削除
for (Map.Entry<String, Integer> entry : scores.entrySet()) {
    if (entry.getValue() < 75) {
        scores.remove(entry.getKey());  // ConcurrentModificationException
    }
}
```

### パターン3：ネストしたループ

```java
List<List<String>> groups = new ArrayList<>();
// groupsにデータを追加...

for (List<String> group : groups) {
    for (String member : group) {
        if (member.startsWith("remove")) {
            groups.remove(group);  // 外側のコレクションを変更
        }
    }
}
```

### パターン4：複数イテレーター

```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
Iterator<Integer> iter1 = numbers.iterator();
Iterator<Integer> iter2 = numbers.iterator();

iter1.next();
iter1.remove();  // iter1で削除
iter2.next();    // ConcurrentModificationException（iter2は変更を検知）
```

## 解決策

### 1. イテレーターのremove()メソッド

```java
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

Iterator<String> iter = list.iterator();
while (iter.hasNext()) {
    String item = iter.next();
    if (item.equals("B")) {
        iter.remove();  // イテレーター経由で安全に削除
    }
}
```

### 2. removeIf()メソッド（Java 8以降）

```java
// Listの場合
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
list.removeIf(item -> item.equals("B"));

// Mapの場合
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 85);
scores.put("Bob", 70);
scores.put("Charlie", 60);
scores.entrySet().removeIf(entry -> entry.getValue() < 75);
```

### 3. 新しいコレクションの作成

```java
// フィルタリングして新しいリストを作成
List<String> original = Arrays.asList("A", "B", "C", "D");
List<String> filtered = original.stream()
    .filter(item -> !item.equals("B"))
    .collect(Collectors.toList());

// または従来の方法
List<String> newList = new ArrayList<>();
for (String item : original) {
    if (!item.equals("B")) {
        newList.add(item);
    }
}
```

### 4. インデックスを使った削除（逆順）

```java
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

// 後ろから削除することでインデックスのずれを防ぐ
for (int i = list.size() - 1; i >= 0; i--) {
    if (list.get(i).equals("B")) {
        list.remove(i);
    }
}
```

### 5. ConcurrentCollectionの使用

```java
// マルチスレッド環境での解決策
ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
concurrentMap.put("Alice", 85);
concurrentMap.put("Bob", 70);

// 並行アクセスに対して安全
for (Map.Entry<String, Integer> entry : concurrentMap.entrySet()) {
    if (entry.getValue() < 75) {
        concurrentMap.remove(entry.getKey());  // 安全に削除可能
    }
}
```

## 高度な解決策

### 1. CopyOnWriteコレクション

```java
// 読み取りが多く、書き込みが少ない場合に適している
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
list.addAll(Arrays.asList("A", "B", "C", "D"));

// 反復処理中の変更が可能（ただしパフォーマンスに注意）
for (String item : list) {
    if (item.equals("B")) {
        list.remove(item);  // 例外は発生しない
    }
}
```

### 2. 遅延削除パターン

```java
public class LazyRemovalList<E> {
    private final List<E> list;
    private final Set<E> toRemove = new HashSet<>();
    
    public LazyRemovalList(List<E> list) {
        this.list = new ArrayList<>(list);
    }
    
    public void markForRemoval(E element) {
        toRemove.add(element);
    }
    
    public void applyRemovals() {
        list.removeAll(toRemove);
        toRemove.clear();
    }
    
    public void process() {
        for (E element : list) {
            if (shouldRemove(element)) {
                markForRemoval(element);  // 即座に削除せず、マークのみ
            }
        }
        applyRemovals();  // 反復処理後に一括削除
    }
}
```

### 3. 変更できないコレクション

```java
// List.ofで作ったリストは変更できない
List<String> immutableList = List.of("A", "B", "C", "D");
// immutableList.remove("B");  // UnsupportedOperationException

// 要素を除きたいときは、元のリストを変えずに新しいリストを作る
List<String> filtered = immutableList.stream()
    .filter(item -> !item.equals("B"))
    .toList();  // 結果も変更できないリスト
```

変更できないコレクションは反復処理中に構造が変わらないので、ConcurrentModificationExceptionは起きません。

## デバッグのコツ

### 1. fail-fastイテレーターの理解

```java
public void demonstrateFailFast() {
    List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
    
    Iterator<String> iter = list.iterator();
    
    // modCountの変更を監視
    IO.println("要素を追加前");
    list.add("D");  // modCountが増加
    
    try {
        iter.next();  // modCountの不一致を検出
    } catch (ConcurrentModificationException e) {
        IO.println("期待通りの例外: " + e);
    }
}
```

### 2. デバッグログの追加

`logger`は外部ライブラリSLF4J（Maven Centralの`org.slf4j:slf4j-api`）のロガーで、`{}`の位置に後ろの引数の値が入ります（使い方は[デバッグ手法](../debugging/debugging-techniques.md)の「ログを使ったデバッグ」）。

```java
public void processWithDebug(List<String> list) {
    logger.debug("処理開始 - リストサイズ: {}", list.size());
    
    List<String> toRemove = new ArrayList<>();
    
    for (String item : list) {
        logger.debug("処理中: {}", item);
        if (shouldRemove(item)) {
            toRemove.add(item);
            logger.debug("削除予定に追加: {}", item);
        }
    }
    
    logger.debug("削除実行 - 削除数: {}", toRemove.size());
    list.removeAll(toRemove);
    logger.debug("処理完了 - リストサイズ: {}", list.size());
}
```

### 3. 同期化リストの反復処理

`Collections.synchronizedList`で包んだリストは、`add`や`get`などの1回ごとの操作はスレッドセーフになりますが、反復処理全体は保護されません。
次のコードでは、`iterator`スレッドが拡張for文で回している間に`adder`スレッドが要素を追加するため、ConcurrentModificationExceptionが起きることがあります。

```java
public class ThreadSafetyChecker {
    private final List<String> list = Collections.synchronizedList(new ArrayList<>());
    
    public void checkThreadSafety() {
        // スレッド1: 要素追加
        Thread adder = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                list.add("Item" + i);
                Thread.yield();
            }
        });
        
        // スレッド2: 反復処理
        Thread iterator = new Thread(() -> {
            try {
                for (String item : list) {
                    IO.println(item);
                    Thread.sleep(1);
                }
            } catch (Exception e) {
                logger.error("反復処理中にエラー", e);
            }
        });
        
        adder.start();
        iterator.start();
    }
}
```

`Collections.synchronizedList`のJavadocにあるとおり、反復処理はリスト自身をロックして行います。

```java
synchronized (list) {
    for (String item : list) {
        IO.println(item);
    }
}
```

ロックしている間はほかのスレッドが追加できないので、反復処理が長いときは`CopyOnWriteArrayList`を使うか、コピーを作ってから回します。

## ベストプラクティス

1. **適切なメソッドの選択**: removeIf()やイテレーターのremove()を優先
2. **イミュータブルの活用**: 可能な限り不変コレクションを使用
3. **並行コレクションの使用**: マルチスレッド環境では専用クラスを使用
4. **防御的コピー**: 反復処理前にコレクションのコピーを作成
5. **ドキュメント化**: 並行アクセスの可能性がある場合は明記

## パフォーマンスの考慮

ArrayListから多くの要素を削除するとき、`Iterator`の`remove()`は削除のたびに後ろの要素を詰めるため、要素数が多いと遅くなりやすいです。
`removeIf()`は削除する要素を先に調べてからまとめて詰めるので、この詰め直しが1回で済みます。
新しいリストを作る方法は元のリストを変えずに済む代わりに、結果の分だけメモリを使います。
実際の差はデータの量や環境で変わるので、気になるときは同じ条件のリストを方法ごとに作り直して測ります。

```java
public void performanceComparison() {
    // 方法1: removeIf
    List<Integer> list1 = createList(100_000);
    long start1 = System.nanoTime();
    list1.removeIf(n -> n % 2 == 0);
    long time1 = System.nanoTime() - start1;

    // 方法2: Iterator
    List<Integer> list2 = createList(100_000);
    long start2 = System.nanoTime();
    Iterator<Integer> iter = list2.iterator();
    while (iter.hasNext()) {
        if (iter.next() % 2 == 0) {
            iter.remove();
        }
    }
    long time2 = System.nanoTime() - start2;

    // 方法3: 新しいリストを作る
    List<Integer> list3 = createList(100_000);
    long start3 = System.nanoTime();
    List<Integer> newList = list3.stream()
        .filter(n -> n % 2 != 0)
        .toList();
    long time3 = System.nanoTime() - start3;

    IO.println("removeIf: %d ms, Iterator: %d ms, Stream: %d ms".formatted(
        time1 / 1_000_000, time2 / 1_000_000, time3 / 1_000_000));
}

private List<Integer> createList(int size) {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < size; i++) {
        list.add(i);
    }
    return list;
}
```

1回だけの計測はJITコンパイルなどの影響を受けるので、目安として見てください。

## 関連項目

- [ArrayIndexOutOfBoundsException](ArrayIndexOutOfBoundsException.md)
- [NullPointerException](NullPointerException.md)