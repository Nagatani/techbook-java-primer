# 第13章 基礎課題：列挙型(Enums)

## 概要
本章で学んだ列挙型（Enum）の基本的な使い方を練習します。型安全な定数の定義、Enumメソッドの活用、基本的なEnumパターンを身につけましょう。

## 課題一覧

### 課題1: 基本的なEnumの定義と使用
`BasicEnums.java`を作成し、以下のEnumを定義してください：

1. **曜日のEnum**
   ```java
   public enum DayOfWeek {
       MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
   }
   ```
   以下のメソッドを実装：
   - 平日かどうかを判定する`isWeekday()`メソッド
   - 曜日の日本語名を返す`getJapaneseName()`メソッド

2. **信号機の色のEnum**
   ```java
   public enum TrafficLight {
       RED, YELLOW, GREEN
   }
   ```
   以下を実装：
   - 次の色を返す`next()`メソッド
   - 車が進んでよいかを判定する`canGo()`メソッド

3. **サイズのEnum**
   ```java
   public enum Size {
       SMALL, MEDIUM, LARGE, EXTRA_LARGE
   }
   ```
   以下を実装：
   - 略称を返す`getAbbreviation()`メソッド（S, M, L, XL）
   - 日本でのサイズ表記を返す`getJapaneseSize()`メソッド

### 課題2: フィールドとコンストラクタを持つEnum
`EnumWithFields.java`を作成し、以下を実装してください：

1. **惑星のEnum**
   ```java
   public enum Planet {
       MERCURY(3.303e+23, 2.4397e6),
       VENUS(4.869e+24, 6.0518e6),
       EARTH(5.976e+24, 6.37814e6),
       // 他の惑星も定義
       
       private final double mass;   // kg
       private final double radius; // meters
       
       // コンストラクタとメソッドを実装
   }
   ```
   実装すべきメソッド：
   - 表面重力を計算する`surfaceGravity()`
   - 物体の重さを計算する`surfaceWeight(double mass)`

2. **HTTPステータスコードのEnum**
   ```java
   public enum HttpStatus {
       OK(200, "OK"),
       NOT_FOUND(404, "Not Found"),
       INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
       // 他のステータスコードも定義
       
       private final int code;
       private final String message;
       
       // コンストラクタとメソッドを実装
   }
   ```
   実装すべきメソッド：
   - ステータスコードからEnumを取得する`fromCode(int code)`
   - クライアントエラーかを判定する`isClientError()`
   - サーバーエラーかを判定する`isServerError()`

### 課題3: Enumの標準メソッドの活用
`EnumMethods.java`を作成し、以下を実装してください：

1. **values()メソッドの活用**
   - すべての曜日を表示する
   - すべてのHTTPステータスコードをリストアップする

2. **valueOf()メソッドの活用**
   - 文字列から曜日を取得する（エラーハンドリング含む）
   - ユーザー入力からEnumを取得する

3. **ordinal()とname()メソッド**
   - 曜日の順序を使った処理
   - Enumの名前を使った処理

### 課題4: Switch文とEnumの組み合わせ
`EnumSwitch.java`を作成し、以下を実装してください：

1. **交通信号の制御**
   ```java
   public static String getAction(TrafficLight light) {
       return switch (light) {
           case RED -> "Stop";
           case YELLOW -> "Caution";
           case GREEN -> "Go";
       };
   }
   ```

2. **料金計算システム**
   ```java
   public enum CustomerType {
       REGULAR, MEMBER, VIP
   }
   
   public static double calculateDiscount(CustomerType type, double amount) {
       // typeに応じた割引率を適用
   }
   ```

## 実装のヒント

### Enumの基本構造
```java
public enum Color {
    RED("#FF0000"),
    GREEN("#00FF00"),
    BLUE("#0000FF");
    
    private final String hexCode;
    
    Color(String hexCode) {
        this.hexCode = hexCode;
    }
    
    public String getHexCode() {
        return hexCode;
    }
}
```

### Enumの便利なメソッド
```java
// すべての値を取得
for (DayOfWeek day : DayOfWeek.values()) {
    System.out.println(day);
}

// 文字列からEnumを取得
DayOfWeek day = DayOfWeek.valueOf("MONDAY");

// 安全な変換メソッド
public static Optional<DayOfWeek> fromString(String name) {
    try {
        return Optional.of(DayOfWeek.valueOf(name));
    } catch (IllegalArgumentException e) {
        return Optional.empty();
    }
}
```

## 提出前チェックリスト
- [ ] すべてのEnumが適切に定義されている
- [ ] フィールドとコンストラクタが正しく実装されている
- [ ] メソッドが期待通りに動作する
- [ ] Javadocコメントが記述されている

## 評価基準
- Enumの基本的な構文を理解しているか
- フィールドとメソッドを適切に追加できているか
- Enumの標準メソッドを活用できているか
- 型安全性を活かした設計ができているか