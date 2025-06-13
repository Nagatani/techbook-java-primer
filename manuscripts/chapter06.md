# 第6章 例外処理

この章では、Javaの例外処理メカニズムについて学習します。

## 6.1 例外の基礎

### try-catch文

```java
public class ExceptionExample {
    public static void main(String[] args) {
        try {
            int result = 10 / 0;  // ArithmeticException
        } catch (ArithmeticException e) {
            System.out.println("ゼロ除算エラー: " + e.getMessage());
        }
        
        try {
            String str = null;
            int length = str.length();  // NullPointerException
        } catch (NullPointerException e) {
            System.out.println("null参照エラー: " + e.getMessage());
        }
    }
}
```

### 複数の例外処理

```java
import java.io.*;

public class MultipleExceptions {
    public static void readFile(String filename) {
        try {
            FileReader file = new FileReader(filename);
            BufferedReader reader = new BufferedReader(file);
            String line = reader.readLine();
            System.out.println(line);
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("ファイルが見つかりません: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/Oエラー: " + e.getMessage());
        } finally {
            System.out.println("処理完了");
        }
    }
}
```

## 6.2 カスタム例外

```java
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

public class BankAccount {
    private double balance;
    
    public BankAccount(double balance) {
        this.balance = balance;
    }
    
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("残高不足: " + balance);
        }
        balance -= amount;
    }
}
```

## 6.3 try-with-resources

```java
import java.io.*;

public class ResourceManagement {
    public static void readFileWithResources(String filename) {
        try (FileReader file = new FileReader(filename);
             BufferedReader reader = new BufferedReader(file)) {
            
            String line = reader.readLine();
            System.out.println(line);
            
        } catch (IOException e) {
            System.out.println("エラー: " + e.getMessage());
        }
        // ファイルは自動的に閉じられる
    }
}
```

## まとめ

適切な例外処理により、堅牢なアプリケーションを作成できます。