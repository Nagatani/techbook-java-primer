---
title: Recordの利用
---

> オブジェクト指向プログラミングおよび演習1 第09回
> 
> Java16からの新機能を試そう


JavaのRecordはJava 16から正式に導入された機能で、データを格納するだけのクラスを簡単に宣言するための機能です。

## 機能と特性

Recordは、Javaの特殊なクラス宣言です。以下の機能、特徴を持ちます。

- 一連の不変なデータを保持するためのシンプルなデータキャリアです。
    - 内部的にはfinalなクラスとして宣言され、フィールドもすべてfinalとなります。
- 明示的に書かなくても、`equals()`、`hashCode()`、`toString()`メソッドを自動で内部生成します。これらは配置したフィールドに基づいて作成されます。
- すべてのフィールドを引数とするコンストラクタも自動生成されます。このコンストラクタは、引数の順序がフィールドの宣言順に一致します。

## メリット

- ボイラープレートコード（冗長なコード）の削減:
    - フィールド、コンストラクタ、および必要なメソッド（equals、hashCode、toString）が自動的に生成されます。
- 不変性（イミュータブル）:
    - レコードは不変オブジェクト（一度作ったら変更されない）を作成するための良い方法であり、それはスレッドセーフ（マルチスレッドでも大丈夫）であり、副作用なしに共有できます。

## デメリット

- 変更可能なデータを扱う必要がある場合や、より高度なオブジェクト指向機能（たとえば、多態性や継承）が必要な場合には、レコードは適していません。
- レコードクラスは継承できません。finalクラスであり、他のクラスから継承できないという制限があります。

## サンプルコード

以下に、JavaのRecordを使用したサンプルコードを示します。

Record: `Employee.java`

```java
// レコードクラスの定義
public record Employee(
  String name,
  int age,
  String department
) {
    
}
```

動作確認クラス: `Test.java`

```java
public class Test {
  public static void main(String[] args) {
    Employee emp = new Employee("Alice", 25, "IT");
    System.out.println(emp.name());
    System.out.println(emp.age());
    System.out.println(emp.department());

    // toString()が自動的に生成されているはずなので確認します
    System.out.println(emp);


    // Recordのフィールド値は変更できません。以下のコードはコンパイルエラーとなります。
    // emp.name = "Bob"; 

    // 新しい値を持つ新しいRecordを作成することは可能です。
    Employee emp2 = new Employee("Bob", 30, "Sales");

    System.out.println(emp2.name());  // Bob
    System.out.println(emp2.age());   // 30
    System.out.println(emp2.department()); // Sales
    System.out.println(emp2);
  }
}
```

### EmployeeにRecordを使わない場合

完全に同じものは作れないので、参考程度に下記コードをみてください。

```java
public final class Employee extends java.lang.Record {

    private final String name;

    private final int age;

    private final String department;

    public Employee(String name, int age, String department) {
        this.name = name;
        this.age = age;
        this.department = department;
    }
    
    public String name() {
        return this.name;
    }
    
    public int age() {
        return this.age;
    }
    
    public String department() {
        return this.department;
    }
    @Override
    public int hashCode() {
        /* 省略 */
    }

    @Override
    public boolean equals(Object other) {
        /* 省略 */
    }

    @Override
    public String toString() {
        return "Employee[name=" + this.name + ", age=" + this.age + ", department=" + this.department + "]";
    }
}
```

### Recordにメソッドを追加する

内部的にはクラスなので、メソッド、コンストラクタの追加は可能です。
しかし、追加したフィールドは、finalで宣言されているため値を変更するメソッドを書くことはできません。

ほかにもいくつか制約がありますが、ここでは割愛いたします。

## Recordを使ったCSVファイルの取り込み

JavaのRecordを使用してCSVファイルからデータを読み込み、それを処理する簡単な例を以下に示します。
この例では、JavaのRecordとJDKのFile API、そしてjava.util.Scannerを使用しています。

使用するデータは以下のデータです。適当なファイル名（data.csvなど）で保存し、IntelliJ IDEAのプロジェクトフォルダ直下に配置してください。

```bash
Alice,25,Tokyo
Bob,30,Osaka
Charlie,35,Nagoya
Dave,40,Sapporo
Eve,45,Fukuoka
Frank,50,Sendai
Grace,55,Hiroshima
Hank,60,Kumamoto
Ivy,65,Okinawa
Jack,70,Hokkaido
```

まず、CSVファイルの各行が持つべきデータを表現するためのRecordを定義します。
ここでは、例として、名前、年齢、居住都市を持つPersonというレコードを使用します。

```java
public record Person(String name, int age, String city) {}
```

次に、CSVファイルからデータを読み込み、そのデータをPersonレコードのリストとして処理するメインメソッドを作成します。

```java
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvReader {

    public static void main(String[] args) {
        Path filePath = Paths.get("data.csv");  // 上記csvデータをdata.csvというファイル名で保存されているとする
        List<Person> persons = new ArrayList<>();

        try (Scanner scanner = new Scanner(filePath)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // 1行のデータを,で分割
                String[] fields = line.split(",");

                // それぞれのデータを変数に退避
                String name = fields[0];
                int age = Integer.parseInt(fields[1]);
                String city = fields[2];

                // データを作成し追加
                persons.add(new Person(name, age, city));
            }
        } catch (FileNotFoundException e) {
            System.err.println("ファイルが見つかりません。");
            System.exit(0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // データの処理をします。ここでは単に全てのPersonを表示するだけ
        persons.forEach(person -> {
            System.out.println(person);
        });
        //persons.forEach(System.out::println); //これでも良い
    }
}
```

このコードはCSVファイルを行ごとに読み込み、各行をカンマで分割してPersonオブジェクトを作成します。
その後、すべてのPersonオブジェクトを表示します。

## Recordまとめ

Record以前は、データを1件保持するクラスとして、各列に該当するフィールドを作成し、getメソッドやtoStringメソッドなどを手作業で実装していました。

Recordは、この冗長な手順を「各列に該当するフィールド」を用意するのみで用意できます。
これだけでも十分ではありますが、Recordで自動生成されるクラスのインスタンスは不変オブジェクトとして生成されるので、一度newしたデータは変更されません。
引数で渡した場合でも勝手にデータが書き変わらないので、マルチスレッドプログラミングなどでも安心して使えます。
