# 第7章 コレクションフレームワーク

この章では、Javaのコレクションフレームワークについて学習します。C言語の配列と比較しながら、より柔軟で強力なデータ構造の使い方を習得しましょう。

## 7.1 コレクションフレームワークとは

### C言語の配列との比較

```c
// C言語の配列（固定サイズ）
int numbers[10];  // サイズ固定
int count = 0;

void addNumber(int num) {
    if (count < 10) {
        numbers[count++] = num;
    }
}
```

```java
// Javaのコレクション（動的サイズ）
List<Integer> numbers = new ArrayList<>();

public void addNumber(int num) {
    numbers.add(num);  // サイズを気にする必要なし
}
```

## 7.2 List インターフェース

### ArrayList

```java
import java.util.*;

public class ArrayListExample {
    public static void main(String[] args) {
        // ArrayListの作成
        List<String> fruits = new ArrayList<>();
        
        // 要素の追加
        fruits.add("りんご");
        fruits.add("バナナ");
        fruits.add("オレンジ");
        
        // 特定位置に挿入
        fruits.add(1, "ぶどう");
        
        // 要素の取得
        String first = fruits.get(0);
        System.out.println("最初の果物: " + first);
        
        // 要素の変更
        fruits.set(2, "みかん");
        
        // 要素の削除
        fruits.remove("バナナ");
        fruits.remove(0);  // インデックスで削除
        
        // サイズの確認
        System.out.println("果物の数: " + fruits.size());
        
        // 全要素の表示
        for (String fruit : fruits) {
            System.out.println(fruit);
        }
    }
}
```

### LinkedList

```java
import java.util.*;

public class LinkedListExample {
    public static void main(String[] args) {
        LinkedList<Integer> numbers = new LinkedList<>();
        
        // 末尾に追加
        numbers.add(10);
        numbers.add(20);
        numbers.add(30);
        
        // 先頭に追加
        numbers.addFirst(5);
        
        // 末尾に追加
        numbers.addLast(40);
        
        // 先頭と末尾の取得
        System.out.println("先頭: " + numbers.getFirst());
        System.out.println("末尾: " + numbers.getLast());
        
        // 先頭と末尾の削除
        numbers.removeFirst();
        numbers.removeLast();
        
        System.out.println(numbers);  // [10, 20, 30]
    }
}
```

### ArrayList vs LinkedList

| 操作 | ArrayList | LinkedList |
|------|-----------|------------|
| インデックスアクセス | O(1) | O(n) |
| 先頭への挿入/削除 | O(n) | O(1) |
| 末尾への挿入/削除 | O(1) | O(1) |
| 中間への挿入/削除 | O(n) | O(n) |

## 7.3 Set インターフェース

### HashSet

```java
import java.util.*;

public class HashSetExample {
    public static void main(String[] args) {
        Set<String> uniqueWords = new HashSet<>();
        
        // 重複を自動的に排除
        uniqueWords.add("Java");
        uniqueWords.add("Python");
        uniqueWords.add("Java");    // 重複は追加されない
        uniqueWords.add("C++");
        
        System.out.println("ユニークな言語数: " + uniqueWords.size());  // 3
        
        // 存在確認
        if (uniqueWords.contains("Java")) {
            System.out.println("Javaが含まれています");
        }
        
        // 全要素の表示（順序は保証されない）
        for (String word : uniqueWords) {
            System.out.println(word);
        }
    }
}
```

### TreeSet

```java
import java.util.*;

public class TreeSetExample {
    public static void main(String[] args) {
        Set<Integer> sortedNumbers = new TreeSet<>();
        
        // 自動的にソートされる
        sortedNumbers.add(30);
        sortedNumbers.add(10);
        sortedNumbers.add(20);
        sortedNumbers.add(10);  // 重複は追加されない
        
        System.out.println(sortedNumbers);  // [10, 20, 30]
        
        // NavigableSetの機能
        TreeSet<Integer> treeSet = (TreeSet<Integer>) sortedNumbers;
        System.out.println("最小値: " + treeSet.first());
        System.out.println("最大値: " + treeSet.last());
        System.out.println("15より大きい最小値: " + treeSet.higher(15));
    }
}
```

## 7.4 Map インターフェース

### HashMap

```java
import java.util.*;

public class HashMapExample {
    public static void main(String[] args) {
        Map<String, Integer> scores = new HashMap<>();
        
        // キーと値のペアを追加
        scores.put("田中", 85);
        scores.put("佐藤", 92);
        scores.put("鈴木", 78);
        
        // 値の取得
        Integer tanakaScore = scores.get("田中");
        System.out.println("田中の点数: " + tanakaScore);
        
        // 存在しないキーの場合のデフォルト値
        Integer unknownScore = scores.getOrDefault("山田", 0);
        System.out.println("山田の点数: " + unknownScore);
        
        // キーの存在確認
        if (scores.containsKey("佐藤")) {
            System.out.println("佐藤のデータがあります");
        }
        
        // 全エントリの表示
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // キーのみ、値のみの取得
        Set<String> names = scores.keySet();
        Collection<Integer> scoreValues = scores.values();
    }
}
```

### TreeMap

```java
import java.util.*;

public class TreeMapExample {
    public static void main(String[] args) {
        Map<String, String> dictionary = new TreeMap<>();
        
        dictionary.put("cat", "猫");
        dictionary.put("dog", "犬");
        dictionary.put("bird", "鳥");
        dictionary.put("apple", "りんご");
        
        // キーがアルファベット順でソートされる
        for (String key : dictionary.keySet()) {
            System.out.println(key + " = " + dictionary.get(key));
        }
    }
}
```

## 7.5 Queue インターフェース

### PriorityQueue

```java
import java.util.*;

public class PriorityQueueExample {
    public static void main(String[] args) {
        // 優先度付きキュー（小さい値が高優先度）
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        
        pq.add(30);
        pq.add(10);
        pq.add(20);
        
        // 優先度順に取り出される
        while (!pq.isEmpty()) {
            System.out.println(pq.poll());  // 10, 20, 30
        }
        
        // カスタム比較器を使用
        PriorityQueue<String> stringPQ = new PriorityQueue<>(
            (a, b) -> b.compareTo(a)  // 降順
        );
        
        stringPQ.add("banana");
        stringPQ.add("apple");
        stringPQ.add("cherry");
        
        while (!stringPQ.isEmpty()) {
            System.out.println(stringPQ.poll());  // cherry, banana, apple
        }
    }
}
```

## 7.6 実践的な使用例

### 単語カウンター

```java
import java.util.*;

public class WordCounter {
    public static void main(String[] args) {
        String text = "Java is great Java is powerful Java is fun";
        String[] words = text.split(" ");
        
        Map<String, Integer> wordCount = new HashMap<>();
        
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        
        // 結果表示
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
```

### 学生管理システム

```java
import java.util.*;

class Student {
    private String name;
    private int id;
    private List<String> courses;
    
    public Student(String name, int id) {
        this.name = name;
        this.id = id;
        this.courses = new ArrayList<>();
    }
    
    public void addCourse(String course) {
        courses.add(course);
    }
    
    // getters
    public String getName() { return name; }
    public int getId() { return id; }
    public List<String> getCourses() { return new ArrayList<>(courses); }
    
    @Override
    public String toString() {
        return name + " (ID: " + id + ") - " + courses;
    }
}

public class StudentManager {
    private Map<Integer, Student> students = new HashMap<>();
    
    public void addStudent(Student student) {
        students.put(student.getId(), student);
    }
    
    public Student getStudent(int id) {
        return students.get(id);
    }
    
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }
    
    public Set<String> getAllCourses() {
        Set<String> allCourses = new HashSet<>();
        for (Student student : students.values()) {
            allCourses.addAll(student.getCourses());
        }
        return allCourses;
    }
    
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        
        Student s1 = new Student("田中太郎", 1001);
        s1.addCourse("Java");
        s1.addCourse("データベース");
        
        Student s2 = new Student("佐藤花子", 1002);
        s2.addCourse("Java");
        s2.addCourse("Web開発");
        
        manager.addStudent(s1);
        manager.addStudent(s2);
        
        System.out.println("全学生:");
        for (Student student : manager.getAllStudents()) {
            System.out.println(student);
        }
        
        System.out.println("\n全コース:");
        for (String course : manager.getAllCourses()) {
            System.out.println(course);
        }
    }
}
```

## 7.7 コレクションのユーティリティ

### Collections クラス

```java
import java.util.*;

public class CollectionsUtilExample {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9));
        
        // ソート
        Collections.sort(numbers);
        System.out.println("ソート後: " + numbers);
        
        // 逆順
        Collections.reverse(numbers);
        System.out.println("逆順: " + numbers);
        
        // シャッフル
        Collections.shuffle(numbers);
        System.out.println("シャッフル後: " + numbers);
        
        // 最大値・最小値
        System.out.println("最大値: " + Collections.max(numbers));
        System.out.println("最小値: " + Collections.min(numbers));
        
        // 不変リストの作成
        List<String> immutableList = Collections.unmodifiableList(
            Arrays.asList("a", "b", "c")
        );
    }
}
```

## 7.8 練習問題

1. 整数のリストから重複を除去し、降順でソートするプログラムを作成してください。

2. 文章を入力として受け取り、各文字の出現回数をカウントするプログラムを作成してください。

3. 商品管理システムを作成し、商品をカテゴリ別に分類する機能を実装してください。

## まとめ

この章では、Javaのコレクションフレームワークの主要なインターフェースと実装クラスを学習しました。List、Set、Map、Queueなどの特性を理解し、適切な場面で使い分けることで、効率的なプログラムを作成できるようになりました。