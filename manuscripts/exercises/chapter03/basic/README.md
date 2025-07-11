# 第3章 基礎課題

## 概要
第3章では、オブジェクト指向の基本的な考え方とその実装方法を学習しました。これらの基礎課題では、カプセル化、クラス設計、オブジェクトの相互作用など、オブジェクト指向プログラミングの核心的な概念を実践します。

## 学習目標
- オブジェクト指向の基本原則を実装できる
- 適切なカプセル化を行えるクラスを設計できる
- オブジェクト間の関係性を理解し実装できる
- 実世界の概念をクラスとしてモデル化できる

## 課題一覧

### 課題3-1: メソッド作成練習
**ファイル名**: `MethodPractice.java`

#### 要求仕様
1. 以下のメソッドを持つクラスを作成：
   - `isPrime(int n)`: 素数判定
   - `factorial(int n)`: 階乗計算
   - `fibonacci(int n)`: フィボナッチ数列のn番目
   - `gcd(int a, int b)`: 最大公約数
   - `lcm(int a, int b)`: 最小公倍数
2. 各メソッドは適切なアクセス修飾子を使用
3. メソッドの引数チェックを実装（負数など）
4. mainメソッドで各メソッドをテスト

#### 実装のヒント
- 再帰を使った実装も検討
- 効率的なアルゴリズムを選択
- エラーケースを考慮

#### 実装例の骨組み
```java
public class MethodPractice {
    // 素数判定
    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        // 実装を完成させる
    }
    
    // 階乗計算
    public static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("負数の階乗は定義されません");
        }
        // 実装を完成させる
    }
    
    // 他のメソッドも実装
    
    public static void main(String[] args) {
        // 各メソッドのテスト
        System.out.println("10は素数？: " + isPrime(10));
        System.out.println("5の階乗: " + factorial(5));
        // 他のテストも実行
    }
}
```

#### 評価ポイント
- アルゴリズムの正確性
- エラーハンドリング
- コードの効率性

#### 発展学習
- メモ化を使った最適化
- より大きな数値への対応
- 単体テストの作成

---

### 課題3-2: テストの点数管理クラス
**ファイル名**: `TestScore.java`、`TestScoreManager.java`

#### 要求仕様
1. `TestScore`クラス：
   - 科目名、点数、受験日を管理
   - 点数は0-100の範囲でバリデーション
   - 成績評価メソッド（A/B/C/D/F）
2. `TestScoreManager`クラス：
   - 複数のテスト結果を管理
   - 科目別の平均点計算
   - 全体の平均点計算
   - 最高点・最低点の取得
   - 成績一覧の表示
3. カプセル化を適切に実装

#### 実装のヒント
- privateフィールドとpublicメソッド
- 入力値の検証
- 意味のあるメソッド名

#### 実装例の骨組み
```java
// TestScore.java
public class TestScore {
    private String subject;
    private int score;
    private String testDate;
    
    public TestScore(String subject, int score, String testDate) {
        // バリデーションを含むコンストラクタ
    }
    
    public String getGrade() {
        // A: 90-100, B: 80-89, C: 70-79, D: 60-69, F: 0-59
    }
    
    // getter/setterメソッド
}

// TestScoreManager.java
public class TestScoreManager {
    private TestScore[] scores;
    private int scoreCount;
    
    public TestScoreManager(int capacity) {
        // 初期化
    }
    
    public void addScore(TestScore score) {
        // スコアの追加
    }
    
    public double calculateAverage() {
        // 全体平均の計算
    }
    
    // 他のメソッドも実装
}
```

#### 評価ポイント
- カプセル化の適切な実装
- データの整合性維持
- 有用な分析機能

#### 発展学習
- 統計的な分析機能（分散、標準偏差）
- グラフ表示機能
- データの永続化

---

### 課題3-3: 車の燃料消費シミュレータ
**ファイル名**: `Car.java`、`FuelSimulator.java`

#### 要求仕様
1. `Car`クラス：
   - 車種名、燃料タンク容量、現在の燃料量、燃費
   - 給油メソッド（タンク容量を超えない）
   - 走行メソッド（燃料消費を計算）
   - 走行可能距離の計算
   - 燃料残量警告（20%以下）
2. `FuelSimulator`クラス：
   - 複数の車を管理
   - 走行シミュレーション
   - 燃料消費レポート
   - 最も燃費の良い車の選出
3. 現実的な制約を実装

#### 実装のヒント
- 燃料は0以下にならない
- 走行距離は正の値のみ
- 燃費は km/L で管理

#### 実装例の骨組み
```java
// Car.java
public class Car {
    private String model;
    private double tankCapacity;
    private double currentFuel;
    private double fuelEfficiency; // km/L
    
    public Car(String model, double tankCapacity, double fuelEfficiency) {
        // 初期化
    }
    
    public boolean drive(double distance) {
        double requiredFuel = distance / fuelEfficiency;
        if (currentFuel >= requiredFuel) {
            currentFuel -= requiredFuel;
            return true;
        }
        return false;
    }
    
    public void refuel(double amount) {
        // 給油処理（タンク容量を超えない）
    }
    
    public double getRange() {
        // 走行可能距離を返す
    }
    
    // 他のメソッドも実装
}

// FuelSimulator.java
public class FuelSimulator {
    private Car[] cars;
    private int carCount;
    
    public void runSimulation() {
        // シミュレーション実行
    }
    
    public void generateReport() {
        // レポート生成
    }
}
```

#### 評価ポイント
- 現実的な動作のモデリング
- エラーケースの適切な処理
- 分かりやすいレポート出力

#### 発展学習
- 高速道路/市街地での燃費の違い
- メンテナンスによる燃費変化
- CO2排出量の計算

---

### 課題3-4: 銀行口座管理システム
**ファイル名**: `BankAccount.java`、`Transaction.java`、`BankingSystem.java`

#### 要求仕様
1. `BankAccount`クラス：
   - 口座番号、名義人、残高
   - 入金・出金・送金メソッド
   - 最低残高の維持（1000円）
   - 取引履歴の記録
2. `Transaction`クラス：
   - 取引日時、種類、金額、残高
   - 取引の詳細表示
3. `BankingSystem`クラス：
   - 複数の口座管理
   - 口座間送金
   - 月次レポート生成
   - 利息計算（年利0.1%）

#### 実装のヒント
- 残高は負にならない
- 取引は時系列で記録
- スレッドセーフは考慮しない

#### 評価ポイント
- 金融システムとしての信頼性
- トランザクションの完全性
- 実用的な機能の実装

#### 発展学習
- 定期預金口座の実装
- 為替対応
- セキュリティ機能

## 提出方法
1. 各課題のJavaファイルを作成
2. JavaDocコメントで各メソッドを文書化
3. テストケースの実行結果を含める
4. ソースコード（.javaファイル）のみを提出

## 注意事項
- オブジェクト指向の原則を意識
- 適切なアクセス修飾子の使用
- 意味のある変数名・メソッド名
- エラー処理を忘れずに実装