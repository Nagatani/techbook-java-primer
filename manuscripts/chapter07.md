# 第7章 コレクションフレームワーク

## はじめに：データ構造の進化とJavaコレクションフレームワークの革新

前章までで、オブジェクト指向プログラミングの基本概念を学習してきました。この章では、Javaプログラミングにおいて最も頻繁に使用され、かつ最も重要な機能の一つである「コレクションフレームワーク」について詳細に学習します。

コレクションフレームワークは、単なるデータ格納の仕組みではありません。これは、コンピュータサイエンスの長い歴史の中で蓄積されたデータ構造とアルゴリズムの知識を、実用的で使いやすい形でパッケージ化した、ソフトウェア工学の傑作の一つです。

### コンピュータサイエンスにおけるデータ構造の重要性

コンピュータプログラムの本質は「データの処理」です。どれほど複雑で高度なアプリケーションも、最終的にはデータの格納、検索、変更、削除といった基本的な操作の組み合わせで構成されています。そのため、データをどのように組織化し、管理するかという「データ構造」の選択は、プログラムの性能、保守性、拡張性を決定する最も重要な要素の一つです。

コンピュータサイエンスの初期から、研究者たちは様々なデータ構造を考案してきました。配列、連結リスト、スタック、キュー、ハッシュテーブル、二分探索木など、それぞれが特定の用途に最適化された特性を持っています。しかし、これらのデータ構造を一から実装することは、高度な専門知識と多大な時間を必要とします。

### プログラミング言語におけるデータ構造サポートの変遷

プログラミング言語の進化は、データ構造のサポート機能の充実と密接に関連しています。初期のプログラミング言語では、配列程度の基本的なデータ構造しか提供されていませんでした。プログラマーは、リストや木構造などの高度なデータ構造が必要な場合、ポインタや構造体を駆使して一から実装する必要がありました。

この状況は、以下のような深刻な問題を引き起こしていました：

**実装の複雑さ**：高度なデータ構造の実装には、メモリ管理、ポインタ操作、アルゴリズムの詳細な理解が必要で、多くのプログラマーにとって負担となっていました。

**バグの多発**：メモリリークやポインタエラーなど、低レベルの実装に起因するバグが頻発し、プログラムの信頼性が損なわれていました。

**車輪の再発明**：同じデータ構造を何度も実装することで、開発効率が著しく低下していました。

**性能の一貫性の欠如**：プログラマーの技量により、同じデータ構造でも性能に大きな差が生まれていました。

### ライブラリとフレームワークの概念

これらの問題を解決するため、1980年代頃から「ライブラリ」という概念が重要視されるようになりました。よく使用されるデータ構造やアルゴリズムを、再利用可能な形でパッケージ化し、プログラマーが簡単に利用できるようにする試みです。

しかし、単純なライブラリには限界もありました。異なるデータ構造間でインターフェイスが統一されていない、型安全性が確保されていない、相互運用性が低いなどの問題です。これらの課題を解決するため、1990年代に「フレームワーク」という、より統合的なアプローチが生まれました。

フレームワークは、単に機能を提供するだけでなく、一貫した設計思想とインターフェイスを持つ、包括的なソリューションです。プログラマーは、フレームワークが提供する統一されたインターフェイスを学習するだけで、多様なデータ構造を効率的に活用できるようになります。

### Javaコレクションフレームワークの革新性

Java 1.2（1998年）で導入されたコレクションフレームワークは、データ構造サポートにおける画期的な進歩を代表しています。このフレームワークの革新性は、以下の点にあります：

**統一されたインターフェイス設計**：List、Set、Mapという明確な概念でデータ構造を分類し、それぞれに統一されたインターフェイスを提供しました。これにより、プログラマーは具体的な実装の詳細を知らなくても、一貫した方法でデータを操作できます。

**実装の選択可能性**：同じインターフェイスに対して複数の実装を提供し、用途に応じて最適なものを選択できるようにしました。例えば、Listインターフェイスには、高速な要素アクセスに適したArrayListと、頻繁な挿入・削除に適したLinkedListが用意されています。

**型安全性の確保**：ジェネリクス（Java 1.5で導入）との組み合わせにより、コンパイル時の型チェックを実現し、実行時エラーを大幅に削減しました。

**アルゴリズムの統合**：Collections クラスを通じて、ソート、検索、シャッフルなどの汎用アルゴリズムを提供し、データ構造との緊密な連携を実現しました。

**イテレータパターンの実装**：すべてのコレクションに対して統一された反復処理の仕組みを提供し、for-each文やストリームAPIなどの言語機能との統合を可能にしました。

### 現代プログラミングにおけるコレクションの位置づけ

現代のソフトウェア開発において、コレクションフレームワークは空気のような存在になっています。Webアプリケーション、データ分析、機械学習、ゲーム開発など、あらゆる分野でコレクションが使用されており、その重要性はますます高まっています。

特に、ビッグデータ時代を迎えた現在、効率的なデータ処理は企業の競争力を左右する重要な要素となっています。Javaのコレクションフレームワークは、単純なデータ格納から、複雑なデータ分析まで、幅広いニーズに対応する基盤を提供しています。

また、関数型プログラミングの概念を取り入れたStream APIの導入により、宣言的なデータ処理が可能になり、より簡潔で理解しやすいコードの記述が実現されています。

### この章で学習する内容の意義

この章では、これらの歴史的背景と技術的意義を踏まえて、Javaコレクションフレームワークを体系的に学習していきます。単にAPIの使い方を覚えるのではなく、以下の点を重視して学習を進めます：

**適切なデータ構造の選択**：問題の性質を分析し、最適なコレクション実装を選択する判断力を養います。

**性能特性の理解**：各データ構造の時間計算量と空間計算量を理解し、スケーラブルなシステム設計の基礎を身につけます。

**実践的な使用パターン**：実際の開発現場でよく使用される効果的なコレクション活用パターンを習得します。

**モダンなJava機能との統合**：ラムダ式、ストリームAPI、関数型インターフェイスなどの現代的な機能との組み合わせ方を学習します。

コレクションフレームワークを深く理解することは、Javaプログラマーとしての基礎体力を大幅に向上させ、より高品質で効率的なソフトウェアの開発能力を身につけることにつながります。

## 7.1 コレクションフレームワークとは

### 配列の限界とコレクションの必要性

Javaプログラミングの初期に学ぶ配列は、複数のデータをまとめて管理するための基本的な手段です。しかし、配列には以下のような制限があります：

```c
// C言語の配列（固定サイズ）
int numbers[10];  // サイズ固定
int count = 0;

void addNumber(int num) {
    if (count < 10) {
        numbers[count++] = num;
    }
    // サイズを超えた場合の処理が困難
}
```

```java
// Javaの配列も同様の制限
int[] numbers = new int[10];
// 一度作成するとサイズを変更できない
// 要素の追加や削除に手間がかかる
```

これらの点を克服し、より柔軟かつ高機能なデータ管理を実現するのがコレクションフレームワークです：

```java
// Javaのコレクション（動的サイズ）
List<Integer> numbers = new ArrayList<>();

public void addNumber(int num) {
    numbers.add(num);  // サイズを気にする必要なし
    // 必要に応じて自動的にサイズが拡張される
}
```

### データ構造とコレクション

データを効率的に扱うための枠組みを「データ構造 (Data Structure)」と呼びます。Javaのコレクションフレームワークは、リスト、セット、マップといった代表的なデータ構造を、クラスやインターフェイスとして提供しています。

### インターフェイスとポリモーフィズムの役割

コレクションフレームワークの中心的な設計思想の1つに、インターフェイスに基づいたプログラミングがあります。主要なコレクションは、それぞれインターフェイスとして定義されています：

* `java.util.List`: 順序付けられた要素のコレクション。重複を許可します
* `java.util.Set`: 重複しない要素のコレクション。順序は保証されないか、特定の順序に従います
* `java.util.Map`: キーと値のペアを格納するコレクション。キーの重複は許可しません

```java
// Listインターフェイス型でArrayListのインスタンスを扱う
List<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");

// 同じListインターフェイス型でLinkedListのインスタンスも扱える
// names = new LinkedList<>(); // 必要に応じて実装を切り替え可能
```

このようにインターフェイス型で変数を宣言することで、具体的な実装クラスに依存しないコードを書くことができます。これをポリモーフィズム（多態性）と呼び、コードの柔軟性や拡張性を高めます。

### ジェネリクス（Generics）と型安全性

コレクションフレームワークを扱う上で欠かせないのがジェネリクスです。ジェネリクスは、クラスやインターフェイスが扱うデータ型を、インスタンス化する際に指定する仕組みです。

```java
// String型の要素のみを格納できるListを宣言
List<String> stringList = new ArrayList<>();
stringList.add("Java");
// stringList.add(123); // コンパイルエラー！ String型以外は追加できない

// Integer型の要素のみを格納できるSetを宣言
Set<Integer> numberSet = new HashSet<>();
numberSet.add(100);
// numberSet.add("hello"); // コンパイルエラー！ Integer型以外は追加できない
```

ジェネリクスを使用する主な利点は「型安全性」の向上です。コレクションに格納するデータの型をコンパイル時に指定することで、意図しない型のデータが混入するのを防ぎます。

#### ダイヤモンド演算子と型推論

Java SE 7以降では、右辺のジェネリクス型指定を省略できる「ダイヤモンド演算子 (`<>`)」が導入されました：

```java
// Java SE 7以降
List<String> nameList = new ArrayList<>(); // ダイヤモンド演算子で型指定を省略
Map<Integer, String> userMap = new HashMap<>();

// Java SE 6以前
// List<String> nameListOld = new ArrayList<String>();
// Map<Integer, String> userMapOld = new HashMap<Integer, String>();
```

さらに、Java SE 10からは`var`を用いたローカル変数の型推論も導入されました：

```java
// Java SE 10以降（varキーワード）
var inferredList = new ArrayList<String>(); // inferredListはArrayList<String>型と推論される
inferredList.add("Type Inference");
```

## 7.2 Listインターフェース

### Listの基本概念

`List`は順序付きのコレクションで、重複する要素を許可します。配列と似ていますが、サイズが動的に変更可能です。

### ArrayListの実践例：学生成績管理システム

`ArrayList`は、内部的に配列を使って実装されたリストです。ランダムアクセスが高速で、一般的に最もよく使用されます。以下のプログラムは、学生の成績管理システムという実用的な例を通じて、ArrayListの効果的な活用方法を学習するための材料です：

ファイル名： StudentGradeManager.java

```java
import java.util.*;
import java.util.stream.Collectors;

// 学生データを表現するクラス
class Student {
    private String studentId;
    private String name;
    private List<Integer> grades;
    private String department;
    
    public Student(String studentId, String name, String department) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.grades = new ArrayList<>(); // 成績リストをArrayListで初期化
    }
    
    // 成績の追加
    public void addGrade(int grade) {
        if (grade >= 0 && grade <= 100) {
            grades.add(grade);
            System.out.println(name + " に成績 " + grade + " を追加しました");
        } else {
            System.out.println("無効な成績です: " + grade + " (0-100の範囲で入力してください)");
        }
    }
    
    // 平均点の計算
    public double getAverageGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        return grades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
    
    // 最高点の取得
    public int getMaxGrade() {
        if (grades.isEmpty()) {
            return 0;
        }
        return Collections.max(grades);
    }
    
    // 最低点の取得
    public int getMinGrade() {
        if (grades.isEmpty()) {
            return 0;
        }
        return Collections.min(grades);
    }
    
    // 成績の総数
    public int getGradeCount() {
        return grades.size();
    }
    
    // 特定の成績の削除
    public boolean removeGrade(int grade) {
        boolean removed = grades.remove(Integer.valueOf(grade));
        if (removed) {
            System.out.println(name + " から成績 " + grade + " を削除しました");
        } else {
            System.out.println(name + " に成績 " + grade + " は見つかりませんでした");
        }
        return removed;
    }
    
    // 全成績の表示
    public void displayGrades() {
        System.out.println("\n=== " + name + " の成績詳細 ===");
        System.out.println("学籍番号: " + studentId);
        System.out.println("学部: " + department);
        System.out.println("成績一覧: " + grades);
        System.out.printf("平均点: %.2f\n", getAverageGrade());
        if (!grades.isEmpty()) {
            System.out.println("最高点: " + getMaxGrade());
            System.out.println("最低点: " + getMinGrade());
        }
        System.out.println("総受講科目数: " + getGradeCount());
    }
    
    // アクセサメソッド
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public List<Integer> getGrades() { return new ArrayList<>(grades); } // 防御的コピー
}

public class StudentGradeManager {
    private List<Student> students; // 学生リストをArrayListで管理
    
    public StudentGradeManager() {
        this.students = new ArrayList<>();
    }
    
    // 学生の登録
    public void addStudent(Student student) {
        students.add(student);
        System.out.println("学生 " + student.getName() + " を登録しました");
    }
    
    // 学籍番号による学生検索
    public Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
    
    // 名前による学生検索（部分一致）
    public List<Student> findStudentsByName(String nameQuery) {
        List<Student> results = new ArrayList<>();
        for (Student student : students) {
            if (student.getName().toLowerCase().contains(nameQuery.toLowerCase())) {
                results.add(student);
            }
        }
        return results;
    }
    
    // 学部による学生検索
    public List<Student> findStudentsByDepartment(String department) {
        return students.stream()
                .filter(student -> student.getDepartment().equals(department))
                .collect(Collectors.toList());
    }
    
    // 全学生の統計情報表示
    public void displayStatistics() {
        if (students.isEmpty()) {
            System.out.println("登録されている学生がいません");
            return;
        }
        
        System.out.println("\n=== 全学生統計情報 ===");
        System.out.println("総学生数: " + students.size());
        
        // 学部別学生数の集計
        Map<String, Long> departmentCounts = students.stream()
                .collect(Collectors.groupingBy(Student::getDepartment, Collectors.counting()));
        
        System.out.println("\n学部別学生数:");
        departmentCounts.forEach((dept, count) -> 
            System.out.println("  " + dept + ": " + count + "人"));
        
        // 平均点の高い順に上位5名を表示
        List<Student> topStudents = students.stream()
                .filter(student -> student.getGradeCount() > 0)
                .sorted((s1, s2) -> Double.compare(s2.getAverageGrade(), s1.getAverageGrade()))
                .limit(5)
                .collect(Collectors.toList());
        
        if (!topStudents.isEmpty()) {
            System.out.println("\n成績上位者:");
            for (int i = 0; i < topStudents.size(); i++) {
                Student student = topStudents.get(i);
                System.out.printf("  %d位: %s (%.2f点)\n", 
                    i + 1, student.getName(), student.getAverageGrade());
            }
        }
    }
    
    // 全学生リストの表示
    public void displayAllStudents() {
        System.out.println("\n=== 全学生一覧 ===");
        if (students.isEmpty()) {
            System.out.println("登録されている学生がいません");
            return;
        }
        
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            System.out.printf("%d. %s (%s) - %s学部 - 平均点: %.2f\n",
                i + 1, student.getName(), student.getStudentId(), 
                student.getDepartment(), student.getAverageGrade());
        }
    }
    
    // 成績データの一括入力
    public void bulkAddGrades(String studentId, int[] grades) {
        Student student = findStudentById(studentId);
        if (student != null) {
            System.out.println("\n" + student.getName() + " に一括で成績を追加中...");
            for (int grade : grades) {
                student.addGrade(grade);
            }
        } else {
            System.out.println("学籍番号 " + studentId + " の学生が見つかりません");
        }
    }
    
    // ArrayListの特徴的な操作のデモンストレーション
    public void demonstrateArrayListFeatures() {
        System.out.println("\n=== ArrayList の特徴的操作のデモ ===");
        
        // サイズ動的変更のデモ
        List<String> demo = new ArrayList<>();
        System.out.println("初期サイズ: " + demo.size());
        
        // 大量データの追加
        for (int i = 1; i <= 1000; i++) {
            demo.add("Element " + i);
        }
        System.out.println("1000要素追加後のサイズ: " + demo.size());
        
        // ランダムアクセスの高速性
        long startTime = System.nanoTime();
        String element500 = demo.get(499); // 500番目の要素取得
        long endTime = System.nanoTime();
        System.out.println("500番目の要素取得にかかった時間: " + (endTime - startTime) + " ナノ秒");
        System.out.println("取得した要素: " + element500);
        
        // 中間挿入の非効率性のデモ
        startTime = System.nanoTime();
        demo.add(500, "Inserted Element"); // 中間への挿入
        endTime = System.nanoTime();
        System.out.println("中間挿入にかかった時間: " + (endTime - startTime) + " ナノ秒");
        
        // 末尾追加の効率性
        startTime = System.nanoTime();
        demo.add("Last Element"); // 末尾への追加
        endTime = System.nanoTime();
        System.out.println("末尾追加にかかった時間: " + (endTime - startTime) + " ナノ秒");
    }
    
    public static void main(String[] args) {
        StudentGradeManager manager = new StudentGradeManager();
        
        System.out.println("=== 学生成績管理システム - ArrayList活用例 ===");
        
        // サンプル学生データの作成と登録
        Student student1 = new Student("S001", "田中太郎", "情報工学");
        Student student2 = new Student("S002", "佐藤花子", "経済学");
        Student student3 = new Student("S003", "鈴木次郎", "情報工学");
        Student student4 = new Student("S004", "高橋美咲", "文学");
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        manager.addStudent(student3);
        manager.addStudent(student4);
        
        // 成績データの一括追加
        manager.bulkAddGrades("S001", new int[]{85, 92, 78, 88, 95});
        manager.bulkAddGrades("S002", new int[]{90, 87, 92, 89, 91});
        manager.bulkAddGrades("S003", new int[]{75, 80, 85, 70, 88});
        manager.bulkAddGrades("S004", new int[]{95, 98, 92, 96, 94});
        
        // 各学生の詳細情報表示
        for (String studentId : Arrays.asList("S001", "S002", "S003", "S004")) {
            Student student = manager.findStudentById(studentId);
            if (student != null) {
                student.displayGrades();
            }
        }
        
        // 全学生一覧表示
        manager.displayAllStudents();
        
        // 統計情報表示
        manager.displayStatistics();
        
        // 検索機能のデモ
        System.out.println("\n=== 検索機能のデモ ===");
        List<Student> engineeringStudents = manager.findStudentsByDepartment("情報工学");
        System.out.println("情報工学部の学生: " + 
            engineeringStudents.stream().map(Student::getName).collect(Collectors.toList()));
        
        List<Student> nameSearchResults = manager.findStudentsByName("田中");
        System.out.println("名前に'田中'を含む学生: " + 
            nameSearchResults.stream().map(Student::getName).collect(Collectors.toList()));
        
        // ArrayList特徴のデモンストレーション
        manager.demonstrateArrayListFeatures();
    }
}
```

**このプログラムから学ぶ重要なArrayListの概念：**

1. **動的サイズ変更**：配列と異なり、要素の追加時に自動的にサイズが拡張されます。初期容量を超えても自動的に内部配列が再配置されます。

2. **ランダムアクセスの高速性**：インデックスによる直接アクセス（get(index)）が O(1) の時間計算量で実行されます。

3. **挿入・削除の特性**：末尾への追加は高速ですが、中間への挿入は要素のシフトが必要なため O(n) の時間がかかります。

4. **型安全性**：ジェネリクスにより、コンパイル時に型の整合性がチェックされ、実行時エラーを防げます。

5. **ストリームAPIとの統合**：Java 8以降のStream APIと組み合わせることで、関数型プログラミングスタイルでのデータ処理が可能です。

**実用的な応用場面：**

- **ユーザー管理システム**：登録ユーザーのリスト管理
- **商品カタログ**：ECサイトの商品情報管理
- **ログデータ処理**：時系列でのログエントリ管理
- **キャッシュシステム**：頻繁にアクセスされるデータの一時保存

**ArrayListの性能特性：**

- **メモリ効率**：内部配列による連続メモリ配置で、キャッシュ効率が良い
- **アクセス性能**：O(1) のランダムアクセス
- **追加性能**：末尾追加は通常 O(1)、容量拡張時は O(n)
- **挿入・削除性能**：中間操作は O(n)、末尾操作は O(1)
        
        // 要素の取得
        System.out.println("1番目の果物: " + fruits.get(1));
        
        // サイズの確認
        System.out.println("果物の数: " + fruits.size());
        
        // 要素の削除
        fruits.remove("バナナ");
        fruits.remove(0); // インデックスで削除
        
        // 全要素の表示
        System.out.println("全ての果物:");
        for (String fruit : fruits) {
            System.out.println("- " + fruit);
        }
        
        // 要素の存在確認
        if (fruits.contains("りんご")) {
            System.out.println("りんごが含まれています");
        }
        
        // リストのクリア
        fruits.clear();
        System.out.println("リストが空かどうか: " + fruits.isEmpty());
    }
}
```

### LinkedList

`LinkedList`は、リンクリスト構造で実装されたリストです。要素の挿入・削除が頻繁な場合に有利です。

```java
import java.util.*;

public class LinkedListExample {
    public static void main(String[] args) {
        LinkedList<Integer> numbers = new LinkedList<>();
        
        // 要素の追加
        numbers.add(10);
        numbers.add(20);
        numbers.add(30);
        
        // 先頭と末尾への追加（LinkedListの特徴）
        numbers.addFirst(5);
        numbers.addLast(40);
        
        System.out.println("リスト: " + numbers); // [5, 10, 20, 30, 40]
        
        // 先頭と末尾の要素を取得・削除
        System.out.println("先頭要素: " + numbers.getFirst());
        System.out.println("末尾要素: " + numbers.getLast());
        
        numbers.removeFirst();
        numbers.removeLast();
        
        System.out.println("削除後: " + numbers); // [10, 20, 30]
    }
}
```

### ArrayList vs LinkedList

| 操作 | ArrayList | LinkedList |
|------|-----------|------------|
| ランダムアクセス（get/set） | O(1) | O(n) |
| 末尾への追加（add） | O(1)* | O(1) |
| 先頭への挿入（add(0, element)） | O(n) | O(1) |
| 中間への挿入 | O(n) | O(n) |
| 削除 | O(n) | O(1)** |

*容量拡張が必要な場合はO(n)  
**削除対象のノードへの参照がある場合

### ListのソートとComparator

```java
import java.util.*;

public class ListSortExample {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(Arrays.asList("Charlie", "Alice", "Bob"));
        
        // 自然順序でソート
        Collections.sort(names);
        System.out.println("自然順序: " + names); // [Alice, Bob, Charlie]
        
        // 逆順でソート
        Collections.sort(names, Collections.reverseOrder());
        System.out.println("逆順: " + names); // [Charlie, Bob, Alice]
        
        // カスタムComparatorでソート（文字列長順）
        Collections.sort(names, Comparator.comparing(String::length));
        System.out.println("文字列長順: " + names);
        
        // Java 8のStreamを使用したソート
        List<String> sorted = names.stream()
                                  .sorted()
                                  .collect(Collectors.toList());
        System.out.println("Streamでソート: " + sorted);
    }
}
```

## 7.3 Setインターフェース：重複のない要素管理と集合演算の実現

### Setの基本概念と数学的背景

`Set`インターフェイスは、数学の集合論に基づいて設計されたコレクションです。「重複を許可しない要素の集まり」という集合の基本的な特性を、プログラミングの世界で実現します。Setは、重複排除、一意性の保証、集合演算（和集合、積集合、差集合）などの重要な機能を提供し、データの整合性と効率的な管理を可能にします。

### HashSetの実践例：オンライン学習プラットフォームの重複管理システム

以下の包括的な例では、オンライン学習プラットフォームにおけるユーザー管理、コース管理、学習履歴の重複排除を通じて、HashSetの実用的な活用方法と技術的特性を学習します：

```java
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * オンライン学習プラットフォームにおけるHashSet活用例
 * 重複のないデータ管理と集合演算の実践的デモンストレーション
 */

// ユーザー情報を表すクラス
class User {
    private String userId;
    private String username;
    private String email;
    private Set<String> enrolledCourses;
    private Set<String> completedCourses;
    private Set<String> interests;
    
    public User(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.enrolledCourses = new HashSet<>();
        this.completedCourses = new HashSet<>();
        this.interests = new HashSet<>();
    }
    
    // HashSetの重複排除機能を活用したコース登録
    public boolean enrollInCourse(String courseId) {
        if (completedCourses.contains(courseId)) {
            System.out.println("警告: " + username + " は既にコース " + courseId + " を完了しています");
            return false;
        }
        boolean added = enrolledCourses.add(courseId);
        if (!added) {
            System.out.println("情報: " + username + " は既にコース " + courseId + " に登録済みです");
        }
        return added;
    }
    
    public void completeCourse(String courseId) {
        if (enrolledCourses.remove(courseId)) {
            completedCourses.add(courseId);
            System.out.println(username + " がコース " + courseId + " を完了しました");
        }
    }
    
    public void addInterest(String interest) {
        interests.add(interest);
    }
    
    public void addInterests(String... interests) {
        this.interests.addAll(Arrays.asList(interests));
    }
    
    // ゲッターメソッド
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Set<String> getEnrolledCourses() { return new HashSet<>(enrolledCourses); }
    public Set<String> getCompletedCourses() { return new HashSet<>(completedCourses); }
    public Set<String> getInterests() { return new HashSet<>(interests); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return String.format("User{id='%s', username='%s', enrolled=%d, completed=%d}", 
            userId, username, enrolledCourses.size(), completedCourses.size());
    }
}

// コース情報を表すクラス
class Course {
    private String courseId;
    private String title;
    private String category;
    private Set<String> prerequisites;
    private Set<String> enrolledUsers;
    private int maxCapacity;
    
    public Course(String courseId, String title, String category, int maxCapacity) {
        this.courseId = courseId;
        this.title = title;
        this.category = category;
        this.maxCapacity = maxCapacity;
        this.prerequisites = new HashSet<>();
        this.enrolledUsers = new HashSet<>();
    }
    
    public void addPrerequisite(String prerequisiteCourseId) {
        prerequisites.add(prerequisiteCourseId);
    }
    
    public boolean enrollUser(String userId) {
        if (enrolledUsers.size() >= maxCapacity) {
            System.out.println("エラー: コース " + courseId + " は定員に達しています");
            return false;
        }
        return enrolledUsers.add(userId);
    }
    
    public boolean removeUser(String userId) {
        return enrolledUsers.remove(userId);
    }
    
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public Set<String> getPrerequisites() { return new HashSet<>(prerequisites); }
    public Set<String> getEnrolledUsers() { return new HashSet<>(enrolledUsers); }
    public int getCurrentEnrollment() { return enrolledUsers.size(); }
    public int getMaxCapacity() { return maxCapacity; }
    
    @Override
    public String toString() {
        return String.format("Course{id='%s', title='%s', enrolled=%d/%d}", 
            courseId, title, enrolledUsers.size(), maxCapacity);
    }
}

public class OnlineLearningPlatform {
    private Set<User> users;
    private Set<Course> courses;
    private Set<String> availableCategories;
    private Map<String, Set<String>> categoryToCourses;
    
    public OnlineLearningPlatform() {
        this.users = new HashSet<>();
        this.courses = new HashSet<>();
        this.availableCategories = new HashSet<>();
        this.categoryToCourses = new HashMap<>();
    }
    
    // ユーザー管理（HashSetによる重複排除）
    public boolean registerUser(User user) {
        boolean added = users.add(user);
        if (added) {
            System.out.println("新規ユーザー登録: " + user.getUsername());
        } else {
            System.out.println("警告: ユーザーID " + user.getUserId() + " は既に登録済みです");
        }
        return added;
    }
    
    // コース管理
    public void addCourse(Course course) {
        courses.add(course);
        availableCategories.add(course.getCategory());
        categoryToCourses.computeIfAbsent(course.getCategory(), k -> new HashSet<>())
                        .add(course.getCourseId());
        System.out.println("新規コース追加: " + course.getTitle());
    }
    
    // 集合演算を活用した高度な検索・分析機能
    public Set<String> findCommonInterests(String userId1, String userId2) {
        User user1 = findUserById(userId1);
        User user2 = findUserById(userId2);
        
        if (user1 == null || user2 == null) {
            return new HashSet<>();
        }
        
        Set<String> commonInterests = new HashSet<>(user1.getInterests());
        commonInterests.retainAll(user2.getInterests()); // 積集合
        return commonInterests;
    }
    
    public Set<String> findUniqueInterests(String userId1, String userId2) {
        User user1 = findUserById(userId1);
        User user2 = findUserById(userId2);
        
        if (user1 == null || user2 == null) {
            return new HashSet<>();
        }
        
        Set<String> allInterests = new HashSet<>(user1.getInterests());
        allInterests.addAll(user2.getInterests()); // 和集合
        
        Set<String> commonInterests = findCommonInterests(userId1, userId2);
        allInterests.removeAll(commonInterests); // 差集合
        return allInterests;
    }
    
    public Set<String> recommendCourses(String userId) {
        User user = findUserById(userId);
        if (user == null) return new HashSet<>();
        
        Set<String> recommendations = new HashSet<>();
        Set<String> userInterests = user.getInterests();
        Set<String> enrolledCourses = user.getEnrolledCourses();
        Set<String> completedCourses = user.getCompletedCourses();
        
        // 興味に基づくコース推薦
        for (String interest : userInterests) {
            Set<String> categoryCoruses = categoryToCourses.getOrDefault(interest, new HashSet<>());
            recommendations.addAll(categoryCoruses);
        }
        
        // 既に登録済み・完了済みのコースを除外
        recommendations.removeAll(enrolledCourses);
        recommendations.removeAll(completedCourses);
        
        return recommendations;
    }
    
    // ユーザー検索（等価性によるHashSet検索）
    public User findUserById(String userId) {
        return users.stream()
                   .filter(user -> user.getUserId().equals(userId))
                   .findFirst()
                   .orElse(null);
    }
    
    // 統計情報生成
    public void displayPlatformStatistics() {
        System.out.println("\n=== プラットフォーム統計情報 ===");
        System.out.println("総ユーザー数: " + users.size());
        System.out.println("総コース数: " + courses.size());
        System.out.println("利用可能カテゴリ数: " + availableCategories.size());
        
        // 人気カテゴリの分析
        Map<String, Long> enrollmentByCategory = courses.stream()
            .collect(Collectors.groupingBy(
                Course::getCategory,
                Collectors.summingLong(Course::getCurrentEnrollment)
            ));
        
        System.out.println("\nカテゴリ別登録者数:");
        enrollmentByCategory.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> System.out.println("  " + entry.getKey() + ": " + entry.getValue() + "人"));
    }
    
    // HashSetの性能特性デモンストレーション
    public void demonstrateHashSetPerformance() {
        System.out.println("\n=== HashSet性能特性デモンストレーション ===");
        
        Set<String> largeSet = new HashSet<>();
        long startTime, endTime;
        
        // 大量データの追加性能テスト
        startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            largeSet.add("Element" + i);
        }
        endTime = System.nanoTime();
        System.out.println("100,000要素の追加時間: " + (endTime - startTime) / 1_000_000 + " ミリ秒");
        
        // 検索性能テスト（O(1)の期待性能）
        startTime = System.nanoTime();
        boolean found = largeSet.contains("Element50000");
        endTime = System.nanoTime();
        System.out.println("要素検索時間: " + (endTime - startTime) + " ナノ秒 (見つかった: " + found + ")");
        
        // 重複追加の検証
        int initialSize = largeSet.size();
        largeSet.add("Element50000"); // 既存要素の重複追加
        System.out.println("重複追加後のサイズ変化: " + initialSize + " → " + largeSet.size());
        
        // 削除性能テスト
        startTime = System.nanoTime();
        largeSet.remove("Element50000");
        endTime = System.nanoTime();
        System.out.println("要素削除時間: " + (endTime - startTime) + " ナノ秒");
    }
    
    public static void main(String[] args) {
        OnlineLearningPlatform platform = new OnlineLearningPlatform();
        
        System.out.println("=== オンライン学習プラットフォーム - HashSet活用例 ===");
        
        // ユーザー登録
        User user1 = new User("U001", "田中太郎", "tanaka@example.com");
        User user2 = new User("U002", "佐藤花子", "sato@example.com");
        User user3 = new User("U003", "鈴木次郎", "suzuki@example.com");
        
        platform.registerUser(user1);
        platform.registerUser(user2);
        platform.registerUser(user3);
        
        // 興味分野の追加（重複自動排除）
        user1.addInterests("Java", "Python", "データサイエンス", "機械学習");
        user2.addInterests("Python", "Web開発", "データサイエンス", "UI/UX");
        user3.addInterests("Java", "Web開発", "モバイル開発", "機械学習");
        
        // コース作成
        Course javaBasics = new Course("C001", "Java基礎プログラミング", "Java", 30);
        Course pythonData = new Course("C002", "Pythonデータ分析", "Python", 25);
        Course webDev = new Course("C003", "Web開発入門", "Web開発", 20);
        Course mlIntro = new Course("C004", "機械学習入門", "機械学習", 15);
        
        platform.addCourse(javaBasics);
        platform.addCourse(pythonData);
        platform.addCourse(webDev);
        platform.addCourse(mlIntro);
        
        // コース登録（重複防止の確認）
        user1.enrollInCourse("C001");
        user1.enrollInCourse("C002");
        user1.enrollInCourse("C001"); // 重複登録の試行
        
        user2.enrollInCourse("C002");
        user2.enrollInCourse("C003");
        
        user3.enrollInCourse("C001");
        user3.enrollInCourse("C004");
        
        // 集合演算による分析
        System.out.println("\n=== 集合演算による分析 ===");
        Set<String> commonInterests = platform.findCommonInterests("U001", "U002");
        System.out.println("田中さんと佐藤さんの共通興味: " + commonInterests);
        
        Set<String> uniqueInterests = platform.findUniqueInterests("U001", "U003");
        System.out.println("田中さんと鈴木さんの固有興味: " + uniqueInterests);
        
        // コース推薦システム
        System.out.println("\n=== コース推薦システム ===");
        Set<String> recommendations = platform.recommendCourses("U001");
        System.out.println("田中さんへの推薦コース: " + recommendations);
        
        // 統計情報の表示
        platform.displayPlatformStatistics();
        
        // 性能特性のデモンストレーション
        platform.demonstrateHashSetPerformance();
    }
}
```

**このプログラムから学ぶ重要なHashSetの概念：**

1. **重複排除の自動化**：HashSetは要素の重複を自動的に排除し、データの一意性を保証します。ユーザーの興味分野やコース登録において、重複チェックのロジックを簡素化できます。

2. **高速な検索性能**：ハッシュテーブルによる実装により、要素の検索・追加・削除が平均的にO(1)の時間計算量で実行されます。

3. **集合演算の活用**：addAll()（和集合）、retainAll()（積集合）、removeAll()（差集合）により、数学的な集合演算をプログラムで直接実現できます。

4. **等価性とハッシュコード**：equals()とhashCode()メソッドの適切な実装により、カスタムオブジェクトでも正確な重複判定が行われます。

5. **メモリ効率と性能**：重複データの保存を防ぐことで、メモリ使用量を最適化し、システム全体の性能を向上させます。

### LinkedHashSetの実践例：ログ処理システムの順序保持

LinkedHashSetは、HashSetの高速性能と挿入順序の保持を両立させた実装です。以下の例では、システムログ処理における順序保持の重要性を示します：

```java
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ログ処理システムにおけるLinkedHashSet活用例
 * 重複排除と挿入順序保持の実践的デモンストレーション
 */

class LogEntry {
    private String level;
    private String message;
    private LocalDateTime timestamp;
    private String source;
    
    public LogEntry(String level, String message, String source) {
        this.level = level;
        this.message = message;
        this.source = source;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getLevel() { return level; }
    public String getMessage() { return message; }
    public String getSource() { return source; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LogEntry logEntry = (LogEntry) obj;
        return Objects.equals(level, logEntry.level) &&
               Objects.equals(message, logEntry.message) &&
               Objects.equals(source, logEntry.source);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(level, message, source);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", 
            level, 
            timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
            message, 
            source);
    }
}

public class OrderedLogProcessor {
    private Set<String> uniqueErrorMessages;      // 重複排除のみ
    private Set<String> orderedUniqueMessages;   // 順序保持 + 重複排除
    private Set<String> processedSources;        // 処理済みソース（順序重要）
    
    public OrderedLogProcessor() {
        this.uniqueErrorMessages = new HashSet<>();
        this.orderedUniqueMessages = new LinkedHashSet<>();
        this.processedSources = new LinkedHashSet<>();
    }
    
    public void processLog(LogEntry entry) {
        // 全てのメッセージを順序保持で記録
        orderedUniqueMessages.add(entry.getMessage());
        processedSources.add(entry.getSource());
        
        // エラーレベルのメッセージのみを別途記録
        if ("ERROR".equals(entry.getLevel())) {
            uniqueErrorMessages.add(entry.getMessage());
        }
        
        System.out.println("処理中: " + entry);
    }
    
    public void displayOrderedReport() {
        System.out.println("\n=== 処理順序を保持したユニークメッセージ ===");
        int index = 1;
        for (String message : orderedUniqueMessages) {
            System.out.println(index++ + ". " + message);
        }
        
        System.out.println("\n=== 処理順序を保持したソース一覧 ===");
        index = 1;
        for (String source : processedSources) {
            System.out.println(index++ + ". " + source);
        }
        
        System.out.println("\n=== ユニークエラーメッセージ（順序無し） ===");
        uniqueErrorMessages.forEach(msg -> System.out.println("- " + msg));
    }
    
    public void demonstrateOrderPreservation() {
        System.out.println("\n=== LinkedHashSet vs HashSet 順序比較 ===");
        
        Set<String> linkedHashSet = new LinkedHashSet<>();
        Set<String> hashSet = new HashSet<>();
        
        String[] testData = {"Delta", "Alpha", "Charlie", "Bravo", "Alpha", "Echo"};
        
        for (String item : testData) {
            linkedHashSet.add(item);
            hashSet.add(item);
        }
        
        System.out.println("挿入順序: " + Arrays.toString(testData));
        System.out.println("LinkedHashSet: " + linkedHashSet); // 挿入順序保持
        System.out.println("HashSet: " + hashSet); // 順序はランダム
    }
    
    public static void main(String[] args) {
        OrderedLogProcessor processor = new OrderedLogProcessor();
        
        // 様々なログエントリを処理（重複あり）
        processor.processLog(new LogEntry("INFO", "システム開始", "MainService"));
        processor.processLog(new LogEntry("ERROR", "データベース接続失敗", "DatabaseService"));
        processor.processLog(new LogEntry("WARN", "メモリ使用量警告", "MemoryMonitor"));
        processor.processLog(new LogEntry("INFO", "ユーザーログイン", "AuthService"));
        processor.processLog(new LogEntry("ERROR", "データベース接続失敗", "DatabaseService")); // 重複
        processor.processLog(new LogEntry("INFO", "システム開始", "MainService")); // 重複
        processor.processLog(new LogEntry("ERROR", "認証失敗", "AuthService"));
        processor.processLog(new LogEntry("INFO", "バックアップ完了", "BackupService"));
        
        processor.displayOrderedReport();
        processor.demonstrateOrderPreservation();
    }
}
```

### TreeSetの実践例：競技スコア管理システムの自動ソート

TreeSetは、要素を自動的にソートして格納する高度なSet実装です。以下の例では、競技スコア管理システムを通じて、TreeSetの強力なソート機能を活用します：

```java
import java.util.*;

/**
 * 競技スコア管理システムにおけるTreeSet活用例
 * 自動ソート機能と範囲検索の実践的デモンストレーション
 */

class CompetitorScore implements Comparable<CompetitorScore> {
    private String name;
    private int score;
    private String category;
    
    public CompetitorScore(String name, int score, String category) {
        this.name = name;
        this.score = score;
        this.category = category;
    }
    
    @Override
    public int compareTo(CompetitorScore other) {
        // スコアの降順（高い順）でソート、同スコアなら名前で昇順
        int scoreComparison = Integer.compare(other.score, this.score);
        return scoreComparison != 0 ? scoreComparison : this.name.compareTo(other.name);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CompetitorScore that = (CompetitorScore) obj;
        return score == that.score && 
               Objects.equals(name, that.name) && 
               Objects.equals(category, that.category);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, score, category);
    }
    
    public String getName() { return name; }
    public int getScore() { return score; }
    public String getCategory() { return category; }
    
    @Override
    public String toString() {
        return String.format("%-12s: %3d点 (%s)", name, score, category);
    }
}

public class CompetitionScoreManager {
    private TreeSet<CompetitorScore> allScores;
    private TreeSet<Integer> uniqueScores;
    private Map<String, TreeSet<CompetitorScore>> categoryScores;
    
    public CompetitionScoreManager() {
        this.allScores = new TreeSet<>();
        this.uniqueScores = new TreeSet<>(Collections.reverseOrder());
        this.categoryScores = new HashMap<>();
    }
    
    public void addScore(CompetitorScore score) {
        allScores.add(score);
        uniqueScores.add(score.getScore());
        
        categoryScores.computeIfAbsent(score.getCategory(), k -> new TreeSet<>())
                     .add(score);
        
        System.out.println("スコア追加: " + score);
    }
    
    public void displayRankings() {
        System.out.println("\n=== 総合ランキング（自動ソート済み） ===");
        int rank = 1;
        for (CompetitorScore score : allScores) {
            System.out.println(String.format("%2d位: %s", rank++, score));
        }
    }
    
    public void displayCategoryRankings() {
        System.out.println("\n=== カテゴリ別ランキング ===");
        for (Map.Entry<String, TreeSet<CompetitorScore>> entry : categoryScores.entrySet()) {
            System.out.println("\n【" + entry.getKey() + "】");
            int rank = 1;
            for (CompetitorScore score : entry.getValue()) {
                System.out.println(String.format("  %d位: %s", rank++, score));
            }
        }
    }
    
    public void displayScoreAnalysis() {
        System.out.println("\n=== スコア分析 ===");
        
        if (!allScores.isEmpty()) {
            CompetitorScore highest = allScores.first();  // 最高スコア
            CompetitorScore lowest = allScores.last();    // 最低スコア
            
            System.out.println("最高スコア: " + highest);
            System.out.println("最低スコア: " + lowest);
            System.out.println("参加者数: " + allScores.size());
            System.out.println("ユニークスコア数: " + uniqueScores.size());
        }
        
        // スコア分布の表示
        System.out.println("\nスコア分布（降順）:");
        uniqueScores.forEach(score -> {
            long count = allScores.stream()
                                  .mapToInt(CompetitorScore::getScore)
                                  .filter(s -> s == score)
                                  .count();
            System.out.println(String.format("  %3d点: %d人", score, count));
        });
    }
    
    public void displayScoreRangeQueries() {
        System.out.println("\n=== スコア範囲検索 ===");
        
        if (uniqueScores.isEmpty()) return;
        
        // 上位25%のスコア範囲
        int totalScores = uniqueScores.size();
        int top25PercentIndex = Math.max(0, totalScores / 4);
        Integer[] scoreArray = uniqueScores.toArray(new Integer[0]);
        
        if (totalScores > 0) {
            Integer top25PercentThreshold = scoreArray[top25PercentIndex];
            
            System.out.println("上位25%のスコア（" + top25PercentThreshold + "点以上）:");
            allScores.stream()
                    .filter(score -> score.getScore() >= top25PercentThreshold)
                    .forEach(score -> System.out.println("  " + score));
        }
        
        // 中央値付近のスコア範囲
        if (totalScores >= 3) {
            Integer medianScore = scoreArray[totalScores / 2];
            Integer lowerBound = scoreArray[Math.min(totalScores - 1, totalScores / 2 + 1)];
            Integer upperBound = scoreArray[Math.max(0, totalScores / 2 - 1)];
            
            System.out.println("\n中央値付近のスコア（" + lowerBound + "-" + upperBound + "点）:");
            allScores.stream()
                    .filter(score -> score.getScore() >= lowerBound && score.getScore() <= upperBound)
                    .forEach(score -> System.out.println("  " + score));
        }
    }
    
    public void demonstrateNavigationMethods() {
        System.out.println("\n=== TreeSet ナビゲーションメソッド デモ ===");
        
        if (uniqueScores.isEmpty()) return;
        
        System.out.println("全スコア: " + uniqueScores);
        System.out.println("最高スコア: " + uniqueScores.first());
        System.out.println("最低スコア: " + uniqueScores.last());
        
        Integer targetScore = 85;
        System.out.println("\n基準スコア: " + targetScore + "点");
        System.out.println("以上の最小スコア: " + uniqueScores.ceiling(targetScore));
        System.out.println("より大きい最小スコア: " + uniqueScores.higher(targetScore));
        System.out.println("以下の最大スコア: " + uniqueScores.floor(targetScore));
        System.out.println("より小さい最大スコア: " + uniqueScores.lower(targetScore));
    }
    
    public static void main(String[] args) {
        CompetitionScoreManager manager = new CompetitionScoreManager();
        
        System.out.println("=== 競技スコア管理システム - TreeSet活用例 ===");
        
        // 競技者のスコア追加
        manager.addScore(new CompetitorScore("田中太郎", 95, "一般"));
        manager.addScore(new CompetitorScore("佐藤花子", 87, "学生"));
        manager.addScore(new CompetitorScore("鈴木次郎", 92, "一般"));
        manager.addScore(new CompetitorScore("高橋美咲", 89, "学生"));
        manager.addScore(new CompetitorScore("山田健太", 95, "学生")); // 同スコア
        manager.addScore(new CompetitorScore("中村由美", 78, "一般"));
        manager.addScore(new CompetitorScore("小林拓也", 91, "学生"));
        manager.addScore(new CompetitorScore("森田詩織", 83, "一般"));
        
        // 各種表示・分析機能
        manager.displayRankings();
        manager.displayCategoryRankings();
        manager.displayScoreAnalysis();
        manager.displayScoreRangeQueries();
        manager.demonstrateNavigationMethods();
    }
}
```

**Set実装の特性比較：**

| 実装 | 順序保持 | ソート | 検索性能 | 主な用途 |
|------|----------|--------|----------|----------|
| HashSet | ❌ | ❌ | O(1) | 高速な重複排除 |
| LinkedHashSet | ✅ (挿入順) | ❌ | O(1) | 順序重要な重複排除 |
| TreeSet | ✅ (ソート順) | ✅ | O(log n) | 自動ソート + 範囲検索 |

**実用的な応用場面：**

- **HashSet**: キャッシュキー管理、一意制約チェック、高速集合演算
- **LinkedHashSet**: ログ処理順序保持、設定項目順序管理、UIコンポーネント順序
- **TreeSet**: ランキングシステム、範囲検索、自動ソート要求

## 7.4 Mapインターフェース：キー・値ペアによる高速データ管理

### Mapの基本概念と実用性

`Map`インターフェイスは、「辞書」や「連想配列」とも呼ばれ、一意のキーと値のペアを管理するコレクションです。現実世界では、辞書で単語（キー）から意味（値）を調べるように、プログラムではIDから顧客情報を取得したり、商品名から価格を検索したりする場面で威力を発揮します。Mapは、O(1)〜O(log n)の高速検索性能により、大規模データの効率的な管理を可能にします。

### HashMapの実践例：Eコマースサイトの在庫・価格管理システム

以下の包括的な例では、Eコマースサイトの商品管理システムを通じて、HashMapの実用的な活用方法と高性能な特性を学習します：

```java
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Eコマースサイトの在庫・価格管理システム
 * HashMapの高性能検索とデータ管理の実践的デモンストレーション
 */

// 商品情報クラス
class Product {
    private String productId;
    private String name;
    private String category;
    private BigDecimal price;
    private int stockQuantity;
    private LocalDateTime lastUpdated;
    
    public Product(String productId, String name, String category, BigDecimal price, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // ゲッター・セッター
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public boolean isInStock() {
        return stockQuantity > 0;
    }
    
    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', price=%s, stock=%d}", 
            productId, name, price, stockQuantity);
    }
}

// 購入履歴クラス
class PurchaseHistory {
    private String customerId;
    private Map<String, Integer> purchasedProducts; // productId -> quantity
    private BigDecimal totalSpent;
    
    public PurchaseHistory(String customerId) {
        this.customerId = customerId;
        this.purchasedProducts = new HashMap<>();
        this.totalSpent = BigDecimal.ZERO;
    }
    
    public void addPurchase(String productId, int quantity, BigDecimal unitPrice) {
        purchasedProducts.merge(productId, quantity, Integer::sum);
        totalSpent = totalSpent.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }
    
    public String getCustomerId() { return customerId; }
    public Map<String, Integer> getPurchasedProducts() { return new HashMap<>(purchasedProducts); }
    public BigDecimal getTotalSpent() { return totalSpent; }
    
    @Override
    public String toString() {
        return String.format("PurchaseHistory{customer='%s', products=%d, total=%s}", 
            customerId, purchasedProducts.size(), totalSpent);
    }
}

public class EcommerceInventorySystem {
    private Map<String, Product> productCatalog;           // productId -> Product
    private Map<String, BigDecimal> priceIndex;            // productId -> price (高速価格検索用)
    private Map<String, Set<String>> categoryProducts;     // category -> Set<productId>
    private Map<String, PurchaseHistory> customerHistory;  // customerId -> PurchaseHistory
    private Map<String, Integer> searchCount;              // productId -> search count (人気度追跡)
    
    public EcommerceInventorySystem() {
        this.productCatalog = new HashMap<>();
        this.priceIndex = new HashMap<>();
        this.categoryProducts = new HashMap<>();
        this.customerHistory = new HashMap<>();
        this.searchCount = new HashMap<>();
    }
    
    // 商品登録（複数のMapを同期して更新）
    public void registerProduct(Product product) {
        String productId = product.getProductId();
        
        productCatalog.put(productId, product);
        priceIndex.put(productId, product.getPrice());
        
        categoryProducts.computeIfAbsent(product.getCategory(), k -> new HashSet<>())
                       .add(productId);
        
        searchCount.put(productId, 0);
        
        System.out.println("商品登録: " + product.getName() + " (ID: " + productId + ")");
    }
    
    // 高速商品検索（O(1)）
    public Product findProduct(String productId) {
        searchCount.merge(productId, 1, Integer::sum); // 検索回数をカウント
        return productCatalog.get(productId);
    }
    
    // 価格一括更新（HashMapの高速更新特性を活用）
    public void updatePrices(Map<String, BigDecimal> priceUpdates) {
        System.out.println("\n=== 価格一括更新 ===");
        
        for (Map.Entry<String, BigDecimal> update : priceUpdates.entrySet()) {
            String productId = update.getKey();
            BigDecimal newPrice = update.getValue();
            
            Product product = productCatalog.get(productId);
            if (product != null) {
                BigDecimal oldPrice = product.getPrice();
                product.setPrice(newPrice);
                priceIndex.put(productId, newPrice);
                
                System.out.printf("価格更新: %s %s -> %s%n", 
                    product.getName(), oldPrice, newPrice);
            }
        }
    }
    
    // カテゴリ別商品検索
    public List<Product> findProductsByCategory(String category) {
        Set<String> productIds = categoryProducts.getOrDefault(category, new HashSet<>());
        return productIds.stream()
                        .map(productCatalog::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }
    
    // 購入処理（在庫管理とMap更新）
    public boolean purchaseProduct(String customerId, String productId, int quantity) {
        Product product = productCatalog.get(productId);
        if (product == null) {
            System.out.println("エラー: 商品が見つかりません (ID: " + productId + ")");
            return false;
        }
        
        if (product.getStockQuantity() < quantity) {
            System.out.println("エラー: 在庫不足 " + product.getName() + 
                             " (在庫: " + product.getStockQuantity() + ", 要求: " + quantity + ")");
            return false;
        }
        
        // 在庫更新
        product.setStockQuantity(product.getStockQuantity() - quantity);
        
        // 購入履歴更新
        customerHistory.computeIfAbsent(customerId, PurchaseHistory::new)
                      .addPurchase(productId, quantity, product.getPrice());
        
        System.out.printf("購入完了: %s が %s を %d個購入 (残り在庫: %d)%n", 
            customerId, product.getName(), quantity, product.getStockQuantity());
        
        return true;
    }
    
    // 売上分析（複数Mapの統合分析）
    public void displaySalesAnalysis() {
        System.out.println("\n=== 売上分析 ===");
        
        // 総売上計算
        BigDecimal totalRevenue = customerHistory.values().stream()
            .map(PurchaseHistory::getTotalSpent)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("総売上: " + totalRevenue);
        System.out.println("総顧客数: " + customerHistory.size());
        
        // 人気商品ランキング（検索回数 + 売上数量）
        Map<String, Integer> salesVolume = new HashMap<>();
        for (PurchaseHistory history : customerHistory.values()) {
            for (Map.Entry<String, Integer> purchase : history.getPurchasedProducts().entrySet()) {
                salesVolume.merge(purchase.getKey(), purchase.getValue(), Integer::sum);
            }
        }
        
        System.out.println("\n人気商品ランキング（販売数量 + 検索回数）:");
        productCatalog.keySet().stream()
            .sorted((a, b) -> {
                int salesA = salesVolume.getOrDefault(a, 0);
                int salesB = salesVolume.getOrDefault(b, 0);
                int searchA = searchCount.getOrDefault(a, 0);
                int searchB = searchCount.getOrDefault(b, 0);
                return Integer.compare(salesB + searchB, salesA + searchA);
            })
            .limit(5)
            .forEach(productId -> {
                Product product = productCatalog.get(productId);
                int sales = salesVolume.getOrDefault(productId, 0);
                int searches = searchCount.getOrDefault(productId, 0);
                System.out.printf("  %s: 販売%d個, 検索%d回%n", 
                    product.getName(), sales, searches);
            });
    }
    
    // HashMapの性能特性デモンストレーション
    public void demonstratePerformance() {
        System.out.println("\n=== HashMap性能特性デモンストレーション ===");
        
        Map<String, String> performanceTestMap = new HashMap<>();
        long startTime, endTime;
        
        // 大量データの挿入性能
        startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            performanceTestMap.put("key" + i, "value" + i);
        }
        endTime = System.nanoTime();
        System.out.println("100,000件の挿入時間: " + (endTime - startTime) / 1_000_000 + " ミリ秒");
        
        // ランダム検索性能（O(1)の期待性能）
        Random random = new Random();
        startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            String value = performanceTestMap.get("key" + random.nextInt(100000));
        }
        endTime = System.nanoTime();
        System.out.println("10,000回の検索時間: " + (endTime - startTime) / 1_000_000 + " ミリ秒");
        
        // キー存在チェック性能
        startTime = System.nanoTime();
        boolean exists = performanceTestMap.containsKey("key50000");
        endTime = System.nanoTime();
        System.out.println("キー存在チェック時間: " + (endTime - startTime) + " ナノ秒 (存在: " + exists + ")");
    }
    
    public static void main(String[] args) {
        EcommerceInventorySystem system = new EcommerceInventorySystem();
        
        System.out.println("=== Eコマース在庫・価格管理システム - HashMap活用例 ===");
        
        // 商品登録
        system.registerProduct(new Product("P001", "ワイヤレスイヤホン", "電子機器", 
            new BigDecimal("12800"), 50));
        system.registerProduct(new Product("P002", "ビジネスバッグ", "バッグ", 
            new BigDecimal("8500"), 25));
        system.registerProduct(new Product("P003", "プログラミング書籍", "書籍", 
            new BigDecimal("3200"), 100));
        system.registerProduct(new Product("P004", "ワイヤレスマウス", "電子機器", 
            new BigDecimal("2800"), 75));
        system.registerProduct(new Product("P005", "ノートパソコン", "電子機器", 
            new BigDecimal("89800"), 10));
        
        // 商品検索デモ
        System.out.println("\n=== 商品検索デモ ===");
        Product laptop = system.findProduct("P005");
        if (laptop != null) {
            System.out.println("検索結果: " + laptop);
        }
        
        // カテゴリ検索
        System.out.println("\n=== カテゴリ別検索 ===");
        List<Product> electronics = system.findProductsByCategory("電子機器");
        electronics.forEach(product -> System.out.println("  " + product));
        
        // 購入処理デモ
        System.out.println("\n=== 購入処理デモ ===");
        system.purchaseProduct("CUST001", "P001", 2);
        system.purchaseProduct("CUST002", "P003", 1);
        system.purchaseProduct("CUST001", "P004", 1);
        system.purchaseProduct("CUST003", "P005", 1);
        system.purchaseProduct("CUST002", "P001", 3);
        
        // 価格一括更新
        Map<String, BigDecimal> priceUpdates = new HashMap<>();
        priceUpdates.put("P001", new BigDecimal("11800")); // 値下げ
        priceUpdates.put("P005", new BigDecimal("92800")); // 値上げ
        system.updatePrices(priceUpdates);
        
        // 売上分析
        system.displaySalesAnalysis();
        
        // 性能デモンストレーション
        system.demonstratePerformance();
    }
}
```

**このプログラムから学ぶ重要なHashMapの概念：**

1. **O(1)の高速検索**：ハッシュテーブルによる実装により、キーによる値の検索が平均的に定数時間で実行されます。大規模なデータセットでも高速アクセスが可能です。

2. **複数Mapの連携管理**：商品カタログ、価格インデックス、カテゴリ分類など、複数のMapを組み合わせることで、異なる視点からの高速データアクセスを実現できます。

3. **動的なデータ更新**：put()、remove()、merge()、computeIfAbsent()などのメソッドにより、実行時にデータを効率的に更新・管理できます。

4. **null値の取り扱い**：get()がnullを返す場合とgetOrDefault()による安全な値取得により、堅牢なアプリケーションを構築できます。

5. **メモリ効率と拡張性**：自動的な容量拡張により、データ量の増減に柔軟に対応し、メモリ使用量を最適化します。

### LinkedHashMap

`LinkedHashMap`は、挿入順序または最後にアクセスした順序を保持するマップです。

```java
import java.util.*;

public class LinkedHashMapExample {
    public static void main(String[] args) {
        // 挿入順序を保持するLinkedHashMap
        Map<String, String> insertionOrderMap = new LinkedHashMap<>();
        insertionOrderMap.put("first", "1番目");
        insertionOrderMap.put("second", "2番目");
        insertionOrderMap.put("third", "3番目");
        
        System.out.println("挿入順序を保持: " + insertionOrderMap);
        
        // アクセス順序を保持するLinkedHashMap（LRUキャッシュの実装に使用）
        Map<String, String> accessOrderMap = new LinkedHashMap<>(16, 0.75f, true);
        accessOrderMap.put("A", "値A");
        accessOrderMap.put("B", "値B");
        accessOrderMap.put("C", "値C");
        
        // Aにアクセス
        accessOrderMap.get("A");
        System.out.println("Aアクセス後: " + accessOrderMap); // Aが最後に移動
    }
}
```

### TreeMap

`TreeMap`は、キーをソートして格納するマップです。

```java
import java.util.*;

public class TreeMapExample {
    public static void main(String[] args) {
        TreeMap<String, Integer> sortedMap = new TreeMap<>();
        sortedMap.put("Charlie", 78);
        sortedMap.put("Alice", 90);
        sortedMap.put("Bob", 85);
        
        System.out.println("キーでソート済み: " + sortedMap);
        // 結果: {Alice=90, Bob=85, Charlie=78}
        
        // TreeMap特有のメソッド
        System.out.println("最初のキー: " + sortedMap.firstKey());
        System.out.println("最後のキー: " + sortedMap.lastKey());
        
        // 範囲指定
        System.out.println("AからBの範囲: " + sortedMap.subMap("A", "C"));
    }
}
```

## 7.5 コレクションの選択指針

### パフォーマンス特性の比較

| データ構造 | 検索 | 挿入 | 削除 | 特徴 |
|-----------|------|------|------|------|
| ArrayList | O(1)* | O(1)** | O(n) | ランダムアクセス高速 |
| LinkedList | O(n) | O(1) | O(1)*** | 挿入・削除高速 |
| HashSet | O(1) | O(1) | O(1) | 重複なし、順序なし |
| LinkedHashSet | O(1) | O(1) | O(1) | 重複なし、挿入順序保持 |
| TreeSet | O(log n) | O(log n) | O(log n) | 重複なし、ソート済み |
| HashMap | O(1) | O(1) | O(1) | キー-値ペア、順序なし |
| LinkedHashMap | O(1) | O(1) | O(1) | キー-値ペア、順序保持 |
| TreeMap | O(log n) | O(log n) | O(log n) | キー-値ペア、キーでソート |

*インデックスアクセスの場合  
**末尾挿入の場合  
***削除対象への参照がある場合

### 使い分けの指針

#### Listの選択
- **ArrayList**: 一般的な用途、ランダムアクセスが必要な場合
- **LinkedList**: 頻繁な挿入・削除が必要な場合

#### Setの選択
- **HashSet**: 重複排除が主目的、順序不要
- **LinkedHashSet**: 重複排除かつ挿入順序保持が必要
- **TreeSet**: 重複排除かつソート済み状態が必要

#### Mapの選択
- **HashMap**: 一般的なキー-値ペア、順序不要
- **LinkedHashMap**: キー-値ペアかつ順序保持が必要
- **TreeMap**: キー-値ペアかつキーでソート済み状態が必要

## 7.6 コレクションの高度な操作

### Collections ユーティリティクラス

`Collections`クラスには、コレクションを操作するための便利なstaticメソッドが多数用意されています。

```java
import java.util.*;

public class CollectionsUtilityExample {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));
        
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
        
        // 二分探索（ソート済みリストが必要）
        Collections.sort(numbers);
        int index = Collections.binarySearch(numbers, 5);
        System.out.println("5の位置: " + index);
        
        // 要素の置換
        Collections.fill(numbers, 0);
        System.out.println("全て0に置換: " + numbers);
        
        // 不変コレクションの作成
        List<String> immutableList = Collections.unmodifiableList(
            Arrays.asList("読み専用", "変更不可", "安全"));
        // immutableList.add("追加"); // UnsupportedOperationException
        
        // 空のコレクション
        List<String> emptyList = Collections.emptyList();
        Set<String> emptySet = Collections.emptySet();
        Map<String, String> emptyMap = Collections.emptyMap();
    }
}
```

### 配列とコレクションの相互変換

```java
import java.util.*;

public class ArrayCollectionConversion {
    public static void main(String[] args) {
        // 配列からListへ
        String[] array = {"Apple", "Banana", "Cherry"};
        List<String> listFromArray = Arrays.asList(array);
        System.out.println("配列からList: " + listFromArray);
        
        // 注意：Arrays.asListで作成されたListは固定サイズ
        // listFromArray.add("Date"); // UnsupportedOperationException
        
        // 変更可能なListを作成
        List<String> mutableList = new ArrayList<>(Arrays.asList(array));
        mutableList.add("Date");
        System.out.println("変更可能なList: " + mutableList);
        
        // ListからListへ（Java 8以降）
        List<String> listFromStream = Arrays.stream(array)
                                          .collect(Collectors.toList());
        
        // Listから配列へ
        String[] arrayFromList = mutableList.toArray(new String[0]);
        System.out.println("Listから配列: " + Arrays.toString(arrayFromList));
        
        // Java 11以降の便利なメソッド
        List<String> listFromOf = List.of("A", "B", "C"); // 不変リスト
        Set<String> setFromOf = Set.of("X", "Y", "Z"); // 不変セット
        Map<String, Integer> mapFromOf = Map.of("one", 1, "two", 2); // 不変マップ
    }
}
```

## 7.7 Stream APIとの連携

### 基本的なStream操作

```java
import java.util.*;
import java.util.stream.*;

public class StreamWithCollections {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");
        
        // 長さが5文字以上の単語をフィルタリング
        List<String> longWords = words.stream()
                                     .filter(word -> word.length() >= 5)
                                     .collect(Collectors.toList());
        System.out.println("5文字以上の単語: " + longWords);
        
        // 大文字に変換
        List<String> upperWords = words.stream()
                                      .map(String::toUpperCase)
                                      .collect(Collectors.toList());
        System.out.println("大文字変換: " + upperWords);
        
        // 文字数でソート
        List<String> sortedByLength = words.stream()
                                          .sorted(Comparator.comparing(String::length))
                                          .collect(Collectors.toList());
        System.out.println("文字数順: " + sortedByLength);
        
        // 文字数でグループ化
        Map<Integer, List<String>> groupedByLength = words.stream()
                .collect(Collectors.groupingBy(String::length));
        System.out.println("文字数でグループ化: " + groupedByLength);
        
        // 統計情報
        IntSummaryStatistics stats = words.stream()
                .mapToInt(String::length)
                .summaryStatistics();
        System.out.println("文字数統計: " + stats);
    }
}
```

### 並列処理

```java
import java.util.*;
import java.util.stream.*;

public class ParallelStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 1_000_000)
                                        .boxed()
                                        .collect(Collectors.toList());
        
        // 順次処理
        long start = System.currentTimeMillis();
        long sum1 = numbers.stream()
                          .mapToLong(Integer::longValue)
                          .sum();
        long time1 = System.currentTimeMillis() - start;
        System.out.println("順次処理: " + sum1 + " (時間: " + time1 + "ms)");
        
        // 並列処理
        start = System.currentTimeMillis();
        long sum2 = numbers.parallelStream()
                          .mapToLong(Integer::longValue)
                          .sum();
        long time2 = System.currentTimeMillis() - start;
        System.out.println("並列処理: " + sum2 + " (時間: " + time2 + "ms)");
    }
}
```

## 7.8 実践的なコレクション活用例

### 単語カウンター

```java
import java.util.*;
import java.util.stream.*;

public class WordCounter {
    public static void main(String[] args) {
        String text = "Java is a popular programming language. " +
                     "Java is object-oriented. Java is platform-independent.";
        
        // 単語の出現回数をカウント
        Map<String, Long> wordCount = Arrays.stream(text.toLowerCase().split("\\s+"))
                .map(word -> word.replaceAll("[^a-z]", "")) // 記号を除去
                .filter(word -> !word.isEmpty())
                .collect(Collectors.groupingBy(
                    word -> word,
                    Collectors.counting()
                ));
        
        System.out.println("単語の出現回数:");
        wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> 
                    System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
```

### 学生成績管理システム

```java
import java.util.*;
import java.util.stream.*;

class Student {
    private String name;
    private String subject;
    private int score;
    
    public Student(String name, String subject, int score) {
        this.name = name;
        this.subject = subject;
        this.score = score;
    }
    
    // getter メソッド
    public String getName() { return name; }
    public String getSubject() { return subject; }
    public int getScore() { return score; }
    
    @Override
    public String toString() {
        return String.format("%s(%s: %d点)", name, subject, score);
    }
}

public class StudentGradeManager {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", "数学", 85),
            new Student("Bob", "数学", 92),
            new Student("Alice", "英語", 78),
            new Student("Charlie", "数学", 88),
            new Student("Bob", "英語", 85),
            new Student("Charlie", "英語", 90)
        );
        
        // 学生別の平均点
        Map<String, Double> averageByStudent = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getName,
                    Collectors.averagingInt(Student::getScore)
                ));
        
        System.out.println("学生別平均点:");
        averageByStudent.forEach((name, avg) -> 
            System.out.printf("%s: %.1f点%n", name, avg));
        
        // 科目別の平均点
        Map<String, Double> averageBySubject = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getSubject,
                    Collectors.averagingInt(Student::getScore)
                ));
        
        System.out.println("\n科目別平均点:");
        averageBySubject.forEach((subject, avg) -> 
            System.out.printf("%s: %.1f点%n", subject, avg));
        
        // 90点以上の成績
        List<Student> topScores = students.stream()
                .filter(student -> student.getScore() >= 90)
                .sorted(Comparator.comparing(Student::getScore).reversed())
                .collect(Collectors.toList());
        
        System.out.println("\n90点以上の成績:");
        topScores.forEach(System.out::println);
    }
}
```

## 7.9 匿名クラスとラムダ式

### 匿名クラスからラムダ式への進化

Java 8より前では、関数型の処理を記述するために匿名クラスを使用していました。ラムダ式の導入により、より簡潔で読みやすいコードを書けるようになりました。

```java
import java.util.*;
import java.util.function.*;

public class AnonymousToLambda {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        
        // 従来の匿名クラス
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        System.out.println("匿名クラスでソート: " + names);
        
        // ラムダ式（簡潔版）
        names = Arrays.asList("Charlie", "Alice", "Bob");
        Collections.sort(names, (s1, s2) -> s1.compareTo(s2));
        System.out.println("ラムダ式でソート: " + names);
        
        // メソッド参照（さらに簡潔）
        names = Arrays.asList("Charlie", "Alice", "Bob");
        Collections.sort(names, String::compareTo);
        System.out.println("メソッド参照でソート: " + names);
    }
}
```

### 関数型インターフェイスの活用

```java
import java.util.*;
import java.util.function.*;

public class FunctionalInterfaceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Predicate<T>: T -> boolean
        Predicate<Integer> isEven = n -> n % 2 == 0;
        List<Integer> evenNumbers = numbers.stream()
                                          .filter(isEven)
                                          .collect(Collectors.toList());
        System.out.println("偶数: " + evenNumbers);
        
        // Function<T, R>: T -> R
        Function<Integer, String> numberToString = n -> "数値: " + n;
        List<String> stringNumbers = numbers.stream()
                                           .map(numberToString)
                                           .collect(Collectors.toList());
        System.out.println("文字列変換: " + stringNumbers.subList(0, 3));
        
        // Consumer<T>: T -> void
        Consumer<String> printer = s -> System.out.println("出力: " + s);
        Arrays.asList("Hello", "World").forEach(printer);
        
        // Supplier<T>: () -> T
        Supplier<Double> randomSupplier = () -> Math.random();
        System.out.println("ランダム値: " + randomSupplier.get());
        
        // BinaryOperator<T>: (T, T) -> T
        BinaryOperator<Integer> multiply = (a, b) -> a * b;
        Optional<Integer> product = numbers.stream()
                                          .reduce(multiply);
        System.out.println("総積: " + product.orElse(0));
    }
}
```

### メソッド参照の種類

```java
import java.util.*;
import java.util.function.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // 1. 静的メソッド参照
        words.forEach(System.out::println);
        
        // 2. インスタンスメソッド参照（特定のオブジェクト）
        String prefix = "Fruit: ";
        Function<String, String> addPrefix = prefix::concat;
        words.stream()
             .map(addPrefix)
             .forEach(System.out::println);
        
        // 3. インスタンスメソッド参照（任意のオブジェクト）
        words.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);
        
        // 4. コンストラクタ参照
        Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> newList = listSupplier.get();
        
        Function<String, StringBuilder> sbBuilder = StringBuilder::new;
        StringBuilder sb = sbBuilder.apply("Hello");
        System.out.println("StringBuilder: " + sb);
    }
}
```

## 7.10 高度なStream API

### 複雑なデータ処理

```java
import java.util.*;
import java.util.stream.*;

class Product {
    private String name;
    private String category;
    private double price;
    
    public Product(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }
    
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    
    @Override
    public String toString() {
        return String.format("%s(%s: ¥%.0f)", name, category, price);
    }
}

public class AdvancedStreamExample {
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
            new Product("ノートPC", "電子機器", 80000),
            new Product("マウス", "電子機器", 2000),
            new Product("本", "書籍", 1500),
            new Product("コーヒー", "飲料", 500),
            new Product("スマートフォン", "電子機器", 120000),
            new Product("雑誌", "書籍", 800)
        );
        
        // カテゴリ別の商品数と平均価格
        Map<String, Map<String, Object>> categoryStats = products.stream()
                .collect(Collectors.groupingBy(
                    Product::getCategory,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> Map.of(
                            "count", list.size(),
                            "avgPrice", list.stream().mapToDouble(Product::getPrice).average().orElse(0.0),
                            "products", list
                        )
                    )
                ));
        
        System.out.println("カテゴリ別統計:");
        categoryStats.forEach((category, stats) -> {
            System.out.printf("%s: %d商品, 平均価格¥%.0f%n", 
                category, stats.get("count"), stats.get("avgPrice"));
        });
        
        // 価格帯別グループ化
        Map<String, List<Product>> priceRanges = products.stream()
                .collect(Collectors.groupingBy(product -> {
                    double price = product.getPrice();
                    if (price < 1000) return "安価";
                    else if (price < 10000) return "中価格";
                    else return "高価格";
                }));
        
        System.out.println("\n価格帯別商品:");
        priceRanges.forEach((range, productList) -> {
            System.out.println(range + ": " + productList.size() + "商品");
            productList.forEach(p -> System.out.println("  " + p));
        });
        
        // 最も高価な商品をカテゴリ別に取得
        Map<String, Optional<Product>> mostExpensiveByCategory = products.stream()
                .collect(Collectors.groupingBy(
                    Product::getCategory,
                    Collectors.maxBy(Comparator.comparing(Product::getPrice))
                ));
        
        System.out.println("\nカテゴリ別最高価格商品:");
        mostExpensiveByCategory.forEach((category, product) -> 
            product.ifPresent(p -> System.out.println(category + ": " + p)));
    }
}
```

### 並列ストリームの活用

```java
import java.util.*;
import java.util.stream.*;

public class ParallelProcessingExample {
    public static void main(String[] args) {
        // 大きなデータセットでの処理比較
        List<Integer> largeList = IntStream.rangeClosed(1, 10_000_000)
                                          .boxed()
                                          .collect(Collectors.toList());
        
        // 順次処理での素数カウント
        long start = System.currentTimeMillis();
        long primeCount1 = largeList.stream()
                                   .filter(ParallelProcessingExample::isPrime)
                                   .count();
        long sequentialTime = System.currentTimeMillis() - start;
        
        // 並列処理での素数カウント
        start = System.currentTimeMillis();
        long primeCount2 = largeList.parallelStream()
                                   .filter(ParallelProcessingExample::isPrime)
                                   .count();
        long parallelTime = System.currentTimeMillis() - start;
        
        System.out.printf("素数の数: %d%n", primeCount1);
        System.out.printf("順次処理時間: %dms%n", sequentialTime);
        System.out.printf("並列処理時間: %dms%n", parallelTime);
        System.out.printf("高速化率: %.2fx%n", (double)sequentialTime / parallelTime);
    }
    
    private static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
```

## まとめ

この章では、Javaのコレクションフレームワークについて包括的に学習しました。以下の重要なポイントを覚えておきましょう：

### 主要なポイント

1. **適切なコレクションの選択**: List、Set、Mapの特性を理解し、用途に応じて選択する
2. **ジェネリクスの活用**: 型安全性を確保し、キャストエラーを防ぐ
3. **インターフェイスベースのプログラミング**: 具体的な実装に依存しない柔軟なコード
4. **パフォーマンス特性の理解**: データサイズや操作頻度に応じた最適な選択
5. **Stream APIとの連携**: 関数型プログラミングによる簡潔で読みやすいコード
6. **ラムダ式とメソッド参照**: 匿名クラスからの進化による簡潔な記述

### 実践での活用

- **ArrayList**: 一般的なリスト操作
- **HashMap**: キー-値ペアの管理
- **HashSet**: 重複排除
- **Stream API**: データの変換・フィルタリング・集計
- **ラムダ式**: 簡潔な関数型処理

コレクションフレームワークは、効率的で保守性の高いJavaプログラムを作成するための基盤となる重要な技術です。適切に活用することで、より品質の高いアプリケーションを開発できるようになります。