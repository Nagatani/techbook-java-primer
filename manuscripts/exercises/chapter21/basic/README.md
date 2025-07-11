# 第21章 基礎課題: ユニットテストの基本

## 課題1: 電卓クラスのテスト作成

### 背景
単体テストの基本を理解するため、シンプルな電卓クラスに対するテストを作成します。

### 要件
以下の`Calculator`クラスに対して、JUnitを使用したテストクラスを作成してください。

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
    
    public double divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return (double) a / b;
    }
}
```

### 実装すべきテスト
1. 各メソッドの正常系テスト
2. ゼロ除算の例外テスト
3. 負の数を含む計算のテスト

### ヒント
- `@Test`アノテーションを使用
- `assertEquals()`で結果を検証
- `assertThrows()`で例外を検証

---

## 課題2: 文字列ユーティリティクラスのテスト

### 背景
より実践的なテストとして、文字列処理ユーティリティクラスのテストを作成します。

### 要件
以下の仕様を満たす`StringUtils`クラスとそのテストを作成してください。

#### StringUtilsクラスの仕様
- `reverse(String str)`: 文字列を逆順にする（nullの場合はnullを返す）
- `isPalindrome(String str)`: 回文かどうかを判定（大文字小文字を区別しない）
- `countVowels(String str)`: 母音（a,e,i,o,u）の数を数える

### 実装すべきテスト
1. 通常の文字列に対するテスト
2. null/空文字列のテスト
3. 境界値のテスト（1文字、特殊文字など）

### 評価基準
- AAAパターン（Arrange-Act-Assert）の適用
- テストメソッド名の分かりやすさ
- 網羅的なテストケース

---

## 課題3: テストカバレッジの測定

### 背景
作成したテストがコードをどの程度カバーしているかを確認します。

### 要件
1. 課題1と課題2で作成したテストのカバレッジを測定
2. カバレッジが80%以上になるようテストを追加
3. カバレッジレポートのスクリーンショットを提出

### 使用ツール
- IntelliJ IDEAの内蔵カバレッジツール
- または、JaCoCoプラグイン

### ヒント
- 分岐（if文）をすべて通るようにテストケースを設計
- 例外処理のパスも忘れずにテスト

---

## 課題4: @BeforeEachと@AfterEachの活用

### 背景
テストの前処理・後処理を効率的に行う方法を学びます。

### 要件
以下の`BankAccount`クラスに対するテストを作成してください。

```java
public class BankAccount {
    private double balance;
    private List<String> transactionHistory;
    
    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        transactionHistory.add("Account opened with: " + initialBalance);
    }
    
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
        transactionHistory.add("Deposited: " + amount);
    }
    
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance -= amount;
        transactionHistory.add("Withdrew: " + amount);
    }
    
    public double getBalance() {
        return balance;
    }
    
    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
}
```

### 実装すべき内容
1. `@BeforeEach`でテスト用のBankAccountインスタンスを初期化
2. 各種操作のテスト（入金、出金、履歴確認）
3. `@AfterEach`でテスト後のクリーンアップ（必要に応じて）

### 評価ポイント
- セットアップコードの重複を避ける
- 各テストが独立して実行可能
- テストの可読性

## 提出方法
1. 各課題のソースコードを`src/`フォルダに格納
2. テストコードを`test/`フォルダに格納
3. 実行結果のスクリーンショットを`screenshots/`フォルダに格納
4. 簡単な説明を`SUBMISSION.md`に記載

## 参考資料
- JUnit 5公式ドキュメント: https://junit.org/junit5/docs/current/user-guide/
- 第21章の本文を参照