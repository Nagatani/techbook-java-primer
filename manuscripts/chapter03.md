# 第3章 クラスとオブジェクトの詳細

この章では、クラスとオブジェクトのより詳細な概念と実践的な使用方法を学習します。

## 3.1 クラス設計の原則

### 単一責任の原則

```java
// 悪い例：複数の責任を持つクラス
public class EmployeeBad {
    private String name;
    private double salary;
    
    public void calculatePay() { /* 給与計算 */ }
    public void saveToDatabase() { /* データベース保存 */ }
    public void sendEmail() { /* メール送信 */ }
}

// 良い例：単一責任を持つクラス
public class Employee {
    private String name;
    private double salary;
    
    // ゲッター・セッター
    public String getName() { return name; }
    public double getSalary() { return salary; }
}

public class PayrollCalculator {
    public double calculatePay(Employee employee) {
        // 給与計算ロジック
        return employee.getSalary();
    }
}

public class EmployeeRepository {
    public void save(Employee employee) {
        // データベース保存ロジック
    }
}
```

## 3.2 オブジェクトの関係性

### 関連（Association）

```java
public class Student {
    private String name;
    private Course[] courses;
    
    public Student(String name) {
        this.name = name;
        this.courses = new Course[10];
    }
    
    public void enrollCourse(Course course) {
        // コース登録ロジック
    }
}

public class Course {
    private String courseName;
    private String instructor;
    
    public Course(String courseName, String instructor) {
        this.courseName = courseName;
        this.instructor = instructor;
    }
}
```

### 集約（Aggregation）

```java
public class Department {
    private String name;
    private List<Employee> employees;
    
    public Department(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }
    
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }
    
    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }
}
```

### 合成（Composition）

```java
public class House {
    private Room[] rooms;
    
    public House(int roomCount) {
        this.rooms = new Room[roomCount];
        // Houseが作成されるときにRoomも作成される
        for (int i = 0; i < roomCount; i++) {
            rooms[i] = new Room("Room " + (i + 1));
        }
    }
    
    private class Room {  // 内部クラス
        private String name;
        
        public Room(String name) {
            this.name = name;
        }
    }
}
```

## 3.3 メソッドの詳細

### メソッドオーバーロード

```java
public class Calculator {
    // 同じ名前のメソッドを異なるパラメータで定義
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {
        return a + b;
    }
    
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    public String add(String a, String b) {
        return a + b;
    }
}
```

### 可変長引数

```java
public class MathUtils {
    public static int sum(int... numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }
    
    public static void main(String[] args) {
        System.out.println(sum(1, 2, 3));           // 6
        System.out.println(sum(1, 2, 3, 4, 5));    // 15
        System.out.println(sum());                  // 0
    }
}
```

## 3.4 内部クラス

### 非静的内部クラス

```java
public class OuterClass {
    private String outerField = "外部クラスのフィールド";
    
    public class InnerClass {
        public void display() {
            // 外部クラスのフィールドにアクセス可能
            System.out.println(outerField);
        }
    }
    
    public void createInner() {
        InnerClass inner = new InnerClass();
        inner.display();
    }
}
```

### 静的内部クラス

```java
public class OuterClass {
    private static String staticField = "静的フィールド";
    private String instanceField = "インスタンスフィールド";
    
    public static class StaticInnerClass {
        public void display() {
            System.out.println(staticField);  // 静的フィールドのみアクセス可能
            // System.out.println(instanceField);  // エラー！
        }
    }
}
```

### 匿名クラス

```java
public class AnonymousExample {
    public static void main(String[] args) {
        // インターフェースの匿名実装
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("匿名クラスで実行");
            }
        };
        
        task.run();
    }
}
```

## 3.5 オブジェクトの比較

### equals()メソッドのオーバーライド

```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Person person = (Person) obj;
        return age == person.age && 
               Objects.equals(name, person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}
```

## 3.6 不変オブジェクト（Immutable Object）

```java
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;
    
    public ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        // 防御的コピーを作成
        this.hobbies = new ArrayList<>(hobbies);
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public List<String> getHobbies() {
        // 防御的コピーを返す
        return new ArrayList<>(hobbies);
    }
}
```

## 3.7 デザインパターンの基礎

### Singletonパターン

```java
public class Logger {
    private static Logger instance;
    
    private Logger() {
        // プライベートコンストラクタ
    }
    
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}
```

### Builderパターン

```java
public class Computer {
    private String cpu;
    private String memory;
    private String storage;
    private String graphicsCard;
    
    private Computer(Builder builder) {
        this.cpu = builder.cpu;
        this.memory = builder.memory;
        this.storage = builder.storage;
        this.graphicsCard = builder.graphicsCard;
    }
    
    public static class Builder {
        private String cpu;
        private String memory;
        private String storage;
        private String graphicsCard;
        
        public Builder setCpu(String cpu) {
            this.cpu = cpu;
            return this;
        }
        
        public Builder setMemory(String memory) {
            this.memory = memory;
            return this;
        }
        
        public Builder setStorage(String storage) {
            this.storage = storage;
            return this;
        }
        
        public Builder setGraphicsCard(String graphicsCard) {
            this.graphicsCard = graphicsCard;
            return this;
        }
        
        public Computer build() {
            return new Computer(this);
        }
    }
}

// 使用例
Computer computer = new Computer.Builder()
    .setCpu("Intel i7")
    .setMemory("16GB")
    .setStorage("1TB SSD")
    .setGraphicsCard("RTX 3080")
    .build();
```

## 3.8 練習問題

1. `BankAccount`クラスを設計し、equals()とhashCode()メソッドを適切に実装してください。

2. Builderパターンを使用して`Pizza`クラスを作成してください。

3. 不変な`Date`クラスを作成し、日付の比較機能を実装してください。

## まとめ

この章では、クラスとオブジェクトのより詳細な概念を学習しました。オブジェクト間の関係性、メソッドの詳細、内部クラス、デザインパターンなど、実践的なJavaプログラミングに必要な知識を身につけることができました。