# 付録B.14: 仮想メソッドテーブル（vtable）と動的ディスパッチ

## 概要

本付録では、Javaにおけるポリモーフィズムの実装メカニズムである仮想メソッドテーブル（vtable）と動的ディスパッチについて詳細に解説します。JVMがどのようにメソッド呼び出しを解決し、最適化するかを理解することで、パフォーマンスを意識したコード設計が可能になります。

**対象読者**: オブジェクト指向の基本を理解し、内部実装に興味がある開発者  
**前提知識**: 第5章「継承とポリモーフィズム」の内容、基本的なJVMの知識  
**関連章**: 第5章、第1章（JVMアーキテクチャ）

## なぜvtableの理解が重要なのか

### 実際のパフォーマンス問題

**問題1: 過度なポリモーフィズムによる性能劣化**
```java
// 性能劣化の例：メガモーフィック呼び出し
public class ProcessorFactory {
    public List<DataProcessor> createProcessors() {
        return List.of(
            new XmlProcessor(),     new JsonProcessor(),
            new CsvProcessor(),     new BinaryProcessor(),
            new DatabaseProcessor(), new MessageProcessor(),
            new FileProcessor(),    new StreamProcessor()
            // ... 20種類以上の実装
        );
    }
}

public void processData(List<DataProcessor> processors, Data data) {
    for (DataProcessor processor : processors) {
        processor.process(data); // 多型呼び出し → vtable経由で性能低下
    }
}
```
**問題**: 20種類以上の実装があるとJVMのインライン化が困難
**影響**: 期待する性能の30-50%に低下

**問題2: 不適切なfinal修飾子の使用**
```java
// パフォーマンス機会を逃す例
public class ConfigurationManager {
    // finalを付けていないため、JVMがインライン化できない
    public String getValue(String key) {
        return properties.getProperty(key);
    }
    
    // 実際には継承される予定がないメソッド
    public boolean isEnabled(String feature) {
        return "true".equals(getValue(feature));
    }
}
```
**問題**: 継承の予定がないのにfinalを付けないため最適化されない
**影響**: 呼び出し回数が多い場合に数十%の性能差

### ビジネスへの実際の影響

**実際の障害事例:**
- **某ゲーム会社**: AI処理でポリモーフィック呼び出しが多用され、フレームレート50%低下
- **金融システム**: 取引処理でメソッド呼び出しオーバーヘッドにより処理能力30%低下
- **ECサイト**: 商品価格計算でvtable検索コストが積み重なり応答時間2倍に

**設計レベルでの影響:**
- **レスポンス性能**: ホットパスでの動的ディスパッチコストが蓄積
- **スループット**: メソッド呼び出し頻度が高いシステムで処理能力低下
- **リソース使用量**: CPU使用率増加によりインフラコスト増大

**最適化による効果:**
- **final修飾子活用**: ホットメソッドで20-40%高速化
- **型の安定性確保**: モノモーフィック呼び出しで3-5倍高速化
- **インライン化促進**: 小さなメソッドで10-15倍高速化

**実際の改善事例:**
- **オンラインゲーム**: AI計算ロジックの最適化でフレームレート60fps維持達成
- **決済システム**: トランザクション処理の最適化で処理能力2倍向上
- **データ分析基盤**: 集計処理の最適化で実行時間70%短縮

---

## メソッド呼び出しの種類

### 静的ディスパッチと動的ディスパッチ

```java
public class DispatchExample {
    // 静的メソッド：コンパイル時に解決
    public static void staticMethod() {
        System.out.println("Static method");
    }
    
    // privateメソッド：コンパイル時に解決
    private void privateMethod() {
        System.out.println("Private method");
    }
    
    // finalメソッド：コンパイル時に解決
    public final void finalMethod() {
        System.out.println("Final method");
    }
    
    // 仮想メソッド：実行時に解決（動的ディスパッチ）
    public void virtualMethod() {
        System.out.println("Virtual method");
    }
}

// バイトコードレベルの違い
/*
静的ディスパッチ:
  invokestatic    // staticメソッド
  invokespecial   // private、コンストラクタ、super呼び出し
  
動的ディスパッチ:
  invokevirtual   // 通常のインスタンスメソッド
  invokeinterface // インターフェイスメソッド
  invokedynamic   // ラムダ式、メソッド参照
*/
```

---

## 仮想メソッドテーブルの構造

### クラス階層とvtable

```java
// サンプルクラス階層
class Animal {
    public void move() { System.out.println("Animal moves"); }
    public void eat() { System.out.println("Animal eats"); }
    public void sleep() { System.out.println("Animal sleeps"); }
}

class Dog extends Animal {
    @Override
    public void move() { System.out.println("Dog runs"); }
    public void bark() { System.out.println("Dog barks"); }
}

class Cat extends Animal {
    @Override
    public void move() { System.out.println("Cat walks"); }
    @Override
    public void eat() { System.out.println("Cat eats fish"); }
    public void meow() { System.out.println("Cat meows"); }
}
```

### vtableの概念図

```
Animal vtable:
+-------+------------------+
| Index | Method           |
+-------+------------------+
| 0     | Object.toString  |
| 1     | Object.equals    |
| 2     | Object.hashCode  |
| ...   | ...              |
| 10    | Animal.move      |
| 11    | Animal.eat       |
| 12    | Animal.sleep     |
+-------+------------------+

Dog vtable:
+-------+------------------+
| Index | Method           |
+-------+------------------+
| 0     | Object.toString  |
| 1     | Object.equals    |
| 2     | Object.hashCode  |
| ...   | ...              |
| 10    | Dog.move         | <- オーバーライド
| 11    | Animal.eat       | <- 継承
| 12    | Animal.sleep     | <- 継承
| 13    | Dog.bark         | <- 新規メソッド
+-------+------------------+

Cat vtable:
+-------+------------------+
| Index | Method           |
+-------+------------------+
| 0     | Object.toString  |
| 1     | Object.equals    |
| 2     | Object.hashCode  |
| ...   | ...              |
| 10    | Cat.move         | <- オーバーライド
| 11    | Cat.eat          | <- オーバーライド
| 12    | Animal.sleep     | <- 継承
| 13    | Cat.meow         | <- 新規メソッド
+-------+------------------+
```

### メソッド呼び出しの解決

```java
public class VTableResolution {
    public static void main(String[] args) {
        Animal animal = new Dog();  // 実際のオブジェクトはDog
        
        // 動的ディスパッチの流れ：
        // 1. animalの実際の型（Dog）を取得
        // 2. Dogクラスのvtableをロード
        // 3. moveメソッドのインデックス（10）でvtableを参照
        // 4. Dog.moveメソッドを呼び出し
        animal.move();  // "Dog runs"が出力される
    }
}

// 擬似的な実装イメージ
class JVMMethodDispatch {
    void invokeVirtual(Object obj, int methodIndex) {
        // 1. オブジェクトのクラスを取得
        Class<?> actualClass = obj.getClass();
        
        // 2. クラスのvtableを取得
        MethodTable vtable = getVTable(actualClass);
        
        // 3. メソッドを取得
        Method method = vtable.getMethod(methodIndex);
        
        // 4. メソッドを実行
        method.invoke(obj);
    }
}
```

---

## インライン化と最適化

### メソッドインライン化

```java
public class InliningExample {
    private int value;
    
    // 小さなメソッドはインライン化の候補
    public int getValue() {
        return value;
    }
    
    public void process() {
        // JITコンパイラによる最適化前
        int x = getValue();
        int y = getValue();
        int result = x + y;
        
        // JITコンパイラによる最適化後（インライン化）
        // int x = this.value;
        // int y = this.value;
        // int result = x + y;
    }
}

// インライン化を阻害する要因
public class InliningInhibitors {
    // 大きすぎるメソッド（デフォルトは35バイトコード）
    public int largeMethod() {
        // 多くの処理...
        return 0;
    }
    
    // 同期メソッド（条件によってはインライン化可能）
    public synchronized int syncMethod() {
        return value;
    }
    
    // 仮想メソッド呼び出し（型が確定できない場合）
    public void polymorphicCall(Animal animal) {
        animal.move();  // 実際の型が不明
    }
}
```

### モノモーフィック、バイモーフィック、メガモーフィック呼び出し

```java
public class CallSiteOptimization {
    // モノモーフィック：1つの型のみ
    public void monomorphic() {
        Dog dog = new Dog();
        for (int i = 0; i < 10000; i++) {
            dog.move();  // 常にDog.move()
        }
    }
    
    // バイモーフィック：2つの型
    public void bimorphic(boolean flag) {
        Animal animal = flag ? new Dog() : new Cat();
        for (int i = 0; i < 10000; i++) {
            animal.move();  // Dog.move()またはCat.move()
        }
    }
    
    // メガモーフィック：多くの型
    public void megamorphic(List<Animal> animals) {
        // Dog, Cat, Bird, Fish, ...
        for (Animal animal : animals) {
            animal.move();  // 多くの異なる実装
        }
    }
}

// JVMの最適化戦略
/*
モノモーフィック:
  - 直接呼び出しにインライン化
  - 最高のパフォーマンス

バイモーフィック:
  - 条件分岐による最適化
  - 良好なパフォーマンス

メガモーフィック:
  - vtable経由の呼び出し
  - 最適化が困難
*/
```

### インライン化の確認

```java
// JVMフラグでインライン化を確認
// -XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining

public class InliningVerification {
    private static final int ITERATIONS = 100_000;
    
    static class Calculator {
        private int value;
        
        // ホットメソッド（頻繁に呼ばれる）
        public int add(int x) {
            return value + x;
        }
        
        // コールドメソッド（めったに呼ばれない）
        public int complexCalculation(int x) {
            return (int) Math.sqrt(value * x);
        }
    }
    
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        
        // ウォームアップ
        for (int i = 0; i < ITERATIONS; i++) {
            calc.add(i);  // インライン化される
        }
        
        // 実測定
        long start = System.nanoTime();
        int sum = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            sum += calc.add(i);
        }
        long end = System.nanoTime();
        
        System.out.println("Time: " + (end - start) / 1_000_000 + " ms");
    }
}
```

---

## インターフェイスメソッドテーブル（itable）

### インターフェイスの実装

```java
interface Flyable {
    void fly();
    void land();
}

interface Swimmable {
    void swim();
    void dive();
}

class Duck implements Flyable, Swimmable {
    public void fly() { System.out.println("Duck flies"); }
    public void land() { System.out.println("Duck lands"); }
    public void swim() { System.out.println("Duck swims"); }
    public void dive() { System.out.println("Duck dives"); }
}
```

### itableの構造

```
Duck itable:
+------------+--------+------------------+
| Interface  | Offset | Method           |
+------------+--------+------------------+
| Flyable    | 0      | Duck.fly         |
| Flyable    | 1      | Duck.land        |
| Swimmable  | 0      | Duck.swim        |
| Swimmable  | 1      | Duck.dive        |
+------------+--------+------------------+
```

### インターフェイスメソッドの解決

```java
public class InterfaceDispatch {
    public static void processFlyable(Flyable flyable) {
        // invokeinterface命令
        // 1. オブジェクトのクラスを取得
        // 2. itableでFlyableエントリを検索
        // 3. メソッドオフセットで実装を特定
        // 4. メソッドを呼び出し
        flyable.fly();
    }
    
    public static void main(String[] args) {
        Duck duck = new Duck();
        processFlyable(duck);  // インターフェイス経由の呼び出し
    }
}
```

---

## パフォーマンス測定と最適化

### vtable vs 直接呼び出しのベンチマーク

```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class DispatchBenchmark {
    
    static class Base {
        public int compute(int x) {
            return x * 2;
        }
    }
    
    static final class FinalClass extends Base {
        @Override
        public int compute(int x) {
            return x * 3;
        }
    }
    
    static class NonFinalClass extends Base {
        @Override
        public int compute(int x) {
            return x * 3;
        }
    }
    
    private Base baseRef = new NonFinalClass();
    private FinalClass finalRef = new FinalClass();
    private NonFinalClass nonFinalRef = new NonFinalClass();
    
    @Benchmark
    public int virtualCall() {
        return baseRef.compute(42);  // 仮想メソッド呼び出し
    }
    
    @Benchmark
    public int directCall() {
        return nonFinalRef.compute(42);  // 型が確定
    }
    
    @Benchmark
    public int finalCall() {
        return finalRef.compute(42);  // finalクラス
    }
}
```

### 最適化のガイドライン

```java
// 1. 小さなメソッドを作る（インライン化しやすい）
public class OptimizedClass {
    private int x, y;
    
    // 良い例：小さなgetter
    public int getX() { return x; }
    
    // 悪い例：大きなメソッド
    public int complexGetter() {
        // 多くの処理...
        return x;
    }
}

// 2. finalを適切に使用
public final class ImmutablePoint {
    private final int x, y;
    
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // finalクラスのメソッドは暗黙的にfinal
    public int getX() { return x; }
}

// 3. ホットパスでの型の安定性
public class TypeStability {
    // 型が安定している処理
    public void processDogsOnly(List<Dog> dogs) {
        for (Dog dog : dogs) {
            dog.bark();  // モノモーフィック
        }
    }
    
    // 型が不安定な処理
    public void processAllAnimals(List<Animal> animals) {
        for (Animal animal : animals) {
            animal.move();  // ポリモーフィック
        }
    }
}
```

---

## 実装の詳細

### メソッドエントリポイント

```java
// JVMのメソッドエントリポイント
public class MethodEntryPoints {
    /*
    各メソッドは複数のエントリポイントを持つ：
    
    1. Interpreter entry point
       - インタープリタから呼ばれる
       - 引数のスタック→レジスタ変換
    
    2. Compiled entry point
       - コンパイル済みコードから呼ばれる
       - レジスタベースの呼び出し規約
    
    3. Verified entry point
       - 型チェック済みの高速パス
    
    4. Unverified entry point
       - 型チェックが必要なパス
    */
}
```

### クラスローディングとvtable構築

```java
// vtable構築の擬似コード
class VTableConstruction {
    void buildVTable(Class<?> clazz) {
        // 1. 親クラスのvtableをコピー
        VTable parentVTable = clazz.getSuperclass().getVTable();
        VTable vtable = parentVTable.copy();
        
        // 2. オーバーライドされたメソッドを更新
        for (Method method : clazz.getDeclaredMethods()) {
            if (isOverride(method)) {
                int index = findMethodIndex(parentVTable, method);
                vtable.setMethod(index, method);
            } else if (isNewVirtualMethod(method)) {
                vtable.addMethod(method);
            }
        }
        
        // 3. vtableをクラスに設定
        clazz.setVTable(vtable);
    }
}
```

---

## まとめ

仮想メソッドテーブルと動的ディスパッチの理解により：

1. **パフォーマンスの予測**: メソッド呼び出しのコストを理解
2. **最適化の機会**: インライン化を促進する設計
3. **適切な設計選択**: final、private、staticの使い分け
4. **JVMの挙動理解**: 最適化の仕組みと限界

これらの知識は、高性能なJavaアプリケーションの開発において重要です。ただし、可読性と保守性を犠牲にしてまで最適化を追求すべきではなく、実際のパフォーマンス要件に基づいて判断することが重要です。

## 実践的なサンプルコード

本付録で解説した仮想メソッドテーブルとJVM最適化の実装例は、以下のディレクトリで確認できます：

**[→ 仮想メソッドテーブルのデモ実装](/appendix/virtual-method-table/)**

このディレクトリには以下が含まれています：

- **VirtualMethodTableDemo.java**: vtableの動作原理を可視化するデモ実装
- **MethodInliningDemo.java**: JITコンパイラによるメソッドインライン化の分析
- **パフォーマンス測定**: 動的ディスパッチのオーバーヘッドを実測
- **最適化のガイドライン**: finalやprivateメソッドの使用による最適化効果

すべてのコードは実行可能で、JVMの内部動作を理解するための教育的な実装となっています。