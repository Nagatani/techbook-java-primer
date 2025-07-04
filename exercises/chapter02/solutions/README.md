# 第2章 解答例とポイント解説

## 📚 この章で学ぶこと

第2章では、Javaプログラミングの核心であるクラスとオブジェクトについて学習します。変数だけでなく、データと処理を組み合わせた構造化されたプログラミングの基礎を身につけます。

## 🎯 学習の目標

- **クラスの概念**: データ（フィールド）と処理（メソッド）をまとめる仕組みを理解する
- **オブジェクトの生成**: クラスからインスタンスを作成し、それぞれが独立したデータを持つことを理解する
- **メソッドの実装**: オブジェクトに対する操作を関数として定義する方法を学ぶ
- **オブジェクト指向の基礎**: 現実世界の概念をプログラムで表現する方法を習得する

## 📝 課題と解答例

### 課題1: Personクラスの作成

**学習ポイント**:
- クラスの基本的な構造（フィールド + メソッド）
- インスタンス変数とメソッドの定義方法
- オブジェクトの生成と操作

**設計パターン**:
```java
public class Person {
    // フィールド（データ）
    String name;
    int age;
    
    // メソッド（処理）
    void introduce() { /* 実装 */ }
    void birthday() { /* 実装 */ }
}
```

**重要な概念**:
- **フィールド**: オブジェクトが持つデータ
- **メソッド**: オブジェクトに対する操作
- **インスタンス**: クラスから作られた具体的なオブジェクト

### 課題2: Bookクラスの設計

**学習ポイント**:
- 複数のフィールドを持つクラス設計
- 計算処理を含むメソッドの実装
- オブジェクト間の比較処理

**高度な機能**:
- 税込価格の計算
- 価格比較メソッド
- データの妥当性チェック
- 書籍情報の表示

### 課題3: Circleクラスの実装

**学習ポイント**:
- double型を使った精密な数値計算
- Math.PIを使った数学的計算
- 幾何学的な概念のプログラム表現

**数学的処理**:
- 面積計算: π × r²
- 円周計算: 2 × π × r
- スケーリング処理

### 課題4: Carクラスの基本実装

**学習ポイント**:
- 状態を持つオブジェクトの設計
- 状態変更を伴うメソッド
- 現実世界の概念のモデル化

## 🏗️ オブジェクト指向の基本概念

### クラスとオブジェクトの関係

```java
// クラス = 設計図
public class Person {
    String name;
    int age;
}

// オブジェクト = 設計図から作った実体
Person person1 = new Person();  // 1つ目のインスタンス
Person person2 = new Person();  // 2つ目のインスタンス
```

### データとメソッドの組み合わせ

```java
public class Circle {
    double radius;              // データ
    
    double calculateArea() {    // そのデータを使う処理
        return Math.PI * radius * radius;
    }
}
```

## ⚠️ よくある間違いと対策

### 1. オブジェクトの独立性の誤解

```java
// ❌ 間違った理解
Person person1 = new Person();
Person person2 = person1;  // 同じオブジェクトを参照

// ✅ 正しい理解
Person person1 = new Person();
Person person2 = new Person();  // 別々のオブジェクトを作成
```

### 2. フィールドの初期化忘れ

```java
// ❌ 問題のあるコード
Person person = new Person();
person.introduce();  // nameがnullで表示される

// ✅ 適切なコード
Person person = new Person();
person.name = "田中太郎";  // 使用前に初期化
person.age = 20;
person.introduce();
```

### 3. nullチェックの忘れ

```java
// ❌ null安全でないコード
boolean isSameAuthor(Book other) {
    return this.author.equals(other.author);  // NullPointerException の可能性
}

// ✅ null安全なコード
boolean isSameAuthor(Book other) {
    if (other != null && this.author != null && other.author != null) {
        return this.author.equals(other.author);
    }
    return false;
}
```

### 4. 計算精度の問題

```java
// ❌ 精度の低い計算
double pi = 3.14;
double area = pi * radius * radius;

// ✅ 正確な計算
double area = Math.PI * radius * radius;
```

## 💡 設計のベストプラクティス

### クラス設計の原則

1. **単一責任の原則**: 一つのクラスは一つの概念を表現する
2. **データの整合性**: フィールドの値は常に有効な状態を保つ
3. **メソッドの命名**: 動詞で始める（introduce, calculate, display）
4. **判定メソッド**: is/hasで始める（isLarger, isSameAuthor）

### メソッドの種類と設計

```java
// 1. 計算メソッド（戻り値あり）
double calculateArea() {
    return Math.PI * radius * radius;
}

// 2. 表示メソッド（戻り値なし）
void displayInfo() {
    System.out.println("半径: " + radius);
}

// 3. 判定メソッド（boolean戻り値）
boolean isLarger(Circle other) {
    return this.calculateArea() > other.calculateArea();
}

// 4. 設定メソッド（妥当性チェック付き）
void setRadius(double newRadius) {
    if (newRadius > 0) {
        this.radius = newRadius;
    }
}
```

### エラーハンドリング

```java
// 入力値の妥当性チェック
void setAge(int newAge) {
    if (newAge >= 0 && newAge <= 150) {
        this.age = newAge;
    } else {
        System.out.println("年齢は0から150の範囲で入力してください");
    }
}
```

## 🔧 デバッグのコツ

### オブジェクトの状態確認

```java
// デバッグ用メソッドを追加
void debugInfo() {
    System.out.println("Debug: name=" + name + ", age=" + age);
}
```

### 段階的なテスト

1. **オブジェクト作成**: 正常にインスタンスが生成できるか
2. **フィールド設定**: 値の設定と取得が正しく動作するか
3. **メソッド呼び出し**: 期待した結果が得られるか
4. **エラーケース**: 不正な値での動作確認

## 🚀 発展的な内容

### オブジェクトの配列管理

```java
Person[] people = new Person[3];
people[0] = new Person();
people[0].name = "田中太郎";
people[0].age = 20;

// 全員の情報を表示
for (Person person : people) {
    if (person != null) {
        person.introduce();
    }
}
```

### 統計処理の実装

```java
// 平均年齢の計算
double calculateAverageAge(Person[] people) {
    int total = 0;
    int count = 0;
    
    for (Person person : people) {
        if (person != null) {
            total += person.age;
            count++;
        }
    }
    
    return count > 0 ? (double)total / count : 0.0;
}
```

## 🎯 次のステップ

第2章をマスターしたら、第3章「コンストラクタとthis」に進みましょう。

第3章では以下を学習します:
- コンストラクタによる初期化
- thisキーワードの使い方
- メソッドオーバーロード
- より洗練されたクラス設計

第2章で学んだクラスとオブジェクトの基礎概念は、これ以降のすべての章で使用される重要な土台となります。しっかりと理解を固めてから次に進みましょう。