# 第14章 基礎課題：例外処理

## 概要
本章で学んだ例外処理の基本的な使い方を練習します。try-catch-finally構文、チェック例外と非チェック例外、基本的なリソース管理を身につけましょう。

## 課題一覧

### 課題1: 基本的な例外処理
`BasicExceptionHandling.java`を作成し、以下を実装してください：

1. **数値変換処理**
   ```java
   public static int safeParseInt(String input) {
       // NumberFormatExceptionを適切に処理
       // エラー時は0を返す
   }
   
   public static Integer safeParseInteger(String input) {
       // NumberFormatExceptionを適切に処理
       // エラー時はnullを返す
   }
   ```

2. **配列アクセス処理**
   ```java
   public static String safeGetElement(String[] array, int index) {
       // ArrayIndexOutOfBoundsExceptionを適切に処理
       // エラー時は"NOT_FOUND"を返す
   }
   ```

3. **除算処理**
   ```java
   public static double safeDivide(double numerator, double denominator) {
       // ゼロ除算を適切に処理
       // エラー時はDouble.NaNを返す
   }
   ```

### 課題2: ファイル処理と例外
`FileProcessor.java`を作成し、以下を実装してください：

1. **ファイル読み込み（従来の方法）**
   ```java
   public static String readFile(String filename) {
       BufferedReader reader = null;
       try {
           reader = new BufferedReader(new FileReader(filename));
           StringBuilder content = new StringBuilder();
           String line;
           while ((line = reader.readLine()) != null) {
               content.append(line).append("\n");
           }
           return content.toString();
       } catch (FileNotFoundException e) {
           // 適切なエラー処理
       } catch (IOException e) {
           // 適切なエラー処理
       } finally {
           // リソースのクリーンアップ
       }
   }
   ```

2. **ファイル書き込み（従来の方法）**
   ```java
   public static boolean writeFile(String filename, String content) {
       PrintWriter writer = null;
       try {
           writer = new PrintWriter(new FileWriter(filename));
           writer.println(content);
           return true;
       } catch (IOException e) {
           // 適切なエラー処理
           return false;
       } finally {
           // リソースのクリーンアップ
       }
   }
   ```

### 課題3: カスタム例外の作成
`BankAccountException.java`を作成し、以下を実装してください：

1. **カスタム例外クラス**
   ```java
   public class InsufficientFundsException extends Exception {
       private final double balance;
       private final double attemptedAmount;
       
       public InsufficientFundsException(double balance, double attemptedAmount) {
           super(String.format("残高不足: 残高 %.2f 円, 引き出し額 %.2f 円", 
                   balance, attemptedAmount));
           this.balance = balance;
           this.attemptedAmount = attemptedAmount;
       }
       
       // getterメソッド
   }
   
   public class InvalidAmountException extends Exception {
       public InvalidAmountException(String message) {
           super(message);
       }
   }
   ```

2. **銀行口座クラス**
   ```java
   public class BankAccount {
       private double balance;
       private final String accountNumber;
       
       public void deposit(double amount) throws InvalidAmountException {
           if (amount <= 0) {
               throw new InvalidAmountException("入金額は正の数である必要があります");
           }
           balance += amount;
       }
       
       public void withdraw(double amount) 
               throws InvalidAmountException, InsufficientFundsException {
           // 実装してください
       }
       
       public void transfer(BankAccount target, double amount) 
               throws InvalidAmountException, InsufficientFundsException {
           // 実装してください
       }
   }
   ```

### 課題4: 例外の連鎖
`ExceptionChaining.java`を作成し、以下を実装してください：

1. **設定ファイル読み込み**
   ```java
   public class ConfigurationException extends Exception {
       public ConfigurationException(String message) {
           super(message);
       }
       
       public ConfigurationException(String message, Throwable cause) {
           super(message, cause);
       }
   }
   
   public class ConfigLoader {
       public Properties loadConfig(String filename) throws ConfigurationException {
           try {
               Properties props = new Properties();
               props.load(new FileInputStream(filename));
               return props;
           } catch (FileNotFoundException e) {
               throw new ConfigurationException(
                   "設定ファイルが見つかりません: " + filename, e);
           } catch (IOException e) {
               throw new ConfigurationException(
                   "設定ファイルの読み込みエラー: " + filename, e);
           }
       }
   }
   ```

## 実装のヒント

### try-catch-finallyの基本構造
```java
try {
    // 例外が発生する可能性のあるコード
} catch (SpecificException e) {
    // 特定の例外の処理
} catch (Exception e) {
    // その他の例外の処理
} finally {
    // 必ず実行される処理（リソースのクリーンアップなど）
}
```

### 複数の例外をキャッチ
```java
try {
    // 処理
} catch (IOException | SQLException e) {
    // IOExceptionまたはSQLExceptionの処理
    logger.error("処理エラー: " + e.getMessage());
}
```

### 例外の再スロー
```java
try {
    // 何らかの処理
} catch (Exception e) {
    logger.error("エラーが発生しました", e);
    throw e; // 例外を再スロー
}
```

## 提出前チェックリスト
- [ ] すべての例外が適切に処理されている
- [ ] finallyブロックでリソースが確実にクローズされている
- [ ] カスタム例外が適切に設計されている
- [ ] エラーメッセージが分かりやすい

## 評価基準
- try-catch-finally構文を正しく使用できているか
- チェック例外と非チェック例外を理解しているか
- リソース管理が適切に行われているか
- カスタム例外が適切に設計・使用されているか