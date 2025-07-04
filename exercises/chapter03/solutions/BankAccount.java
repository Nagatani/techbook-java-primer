/**
 * 第3章 演習問題3: BankAccountクラスの解答例
 * 
 * 【学習ポイント】
 * - コンストラクタチェーンの実装
 * - thisキーワードの活用
 * - 銀行口座の基本的な操作
 * - 状態変更メソッドの設計
 * 
 * 【よくある間違いと対策】
 * 1. コンストラクタチェーンでthis()を最初に書かない
 * 2. thisキーワードを使わずに変数の区別ができない
 * 3. 残高の妥当性チェックを忘れる
 * 4. 取引メソッドの戻り値を適切に処理しない
 */
public class BankAccount {
    // インスタンス変数
    private String accountNumber;
    private double balance;
    private String ownerName;
    
    // 【基本解答】口座番号のみのコンストラクタ
    public BankAccount(String accountNumber) {
        this(accountNumber, 0.0, "未設定");
    }
    
    // 【基本解答】口座番号と名前のコンストラクタ
    public BankAccount(String accountNumber, String ownerName) {
        this(accountNumber, 0.0, ownerName);
    }
    
    // 【基本解答】全項目のコンストラクタ
    public BankAccount(String accountNumber, double balance, String ownerName) {
        // thisキーワードで引数と変数を区別
        this.accountNumber = accountNumber;
        this.balance = Math.max(0.0, balance);  // 負の残高は0に補正
        this.ownerName = ownerName;
    }
    
    // 【応用解答】デフォルトコンストラクタ
    public BankAccount() {
        this("000000", 0.0, "未設定");
    }
    
    // 【応用解答】コピーコンストラクタ
    public BankAccount(BankAccount other) {
        this(other.accountNumber, other.balance, other.ownerName);
    }
    
    // 【基本解答】入金メソッド（基本）
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println(amount + "円を入金しました。");
        } else {
            System.out.println("入金額は正の値である必要があります。");
        }
    }
    
    // 【応用解答】入金メソッド（メッセージ付き）
    public void deposit(double amount, String message) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println(amount + "円を入金しました。（" + message + "）");
        } else {
            System.out.println("入金額は正の値である必要があります。");
        }
    }
    
    // 【基本解答】出金メソッド（基本）
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("出金額は正の値である必要があります。");
            return false;
        }
        
        if (this.balance >= amount) {
            this.balance -= amount;
            System.out.println(amount + "円を出金しました。");
            return true;
        } else {
            System.out.println("残高不足です。（残高: " + this.balance + "円）");
            return false;
        }
    }
    
    // 【応用解答】出金メソッド（手数料付き）
    public boolean withdraw(double amount, double fee) {
        double totalAmount = amount + fee;
        
        if (amount <= 0 || fee < 0) {
            System.out.println("出金額は正の値、手数料は0以上である必要があります。");
            return false;
        }
        
        if (this.balance >= totalAmount) {
            this.balance -= totalAmount;
            System.out.println(amount + "円を出金しました。（手数料: " + fee + "円）");
            return true;
        } else {
            System.out.println("残高不足です。（必要額: " + totalAmount + "円, 残高: " + this.balance + "円）");
            return false;
        }
    }
    
    // 【発展解答】振込メソッド
    public boolean transfer(BankAccount toAccount, double amount) {
        if (this.withdraw(amount)) {
            toAccount.deposit(amount, "振込");
            System.out.println("口座" + toAccount.getAccountNumber() + "に" + amount + "円を振り込みました。");
            return true;
        }
        return false;
    }
    
    // 【発展解答】振込メソッド（手数料付き）
    public boolean transfer(BankAccount toAccount, double amount, double transferFee) {
        if (this.withdraw(amount, transferFee)) {
            toAccount.deposit(amount, "振込");
            System.out.println("口座" + toAccount.getAccountNumber() + "に" + amount + "円を振り込みました。（手数料: " + transferFee + "円）");
            return true;
        }
        return false;
    }
    
    // 口座情報表示メソッド
    public void displayAccountInfo() {
        System.out.println("口座番号: " + accountNumber + ", 残高: " + balance + "円, 名義: " + ownerName);
    }
    
    // 【発展解答】詳細な口座情報表示
    public void displayDetailedAccountInfo() {
        System.out.println("=== 口座情報 ===");
        System.out.println("口座番号: " + accountNumber);
        System.out.println("名義人: " + ownerName);
        System.out.printf("残高: %.2f円%n", balance);
        System.out.println("口座状態: " + (balance > 0 ? "正常" : "残高不足"));
        System.out.println("===============");
    }
    
    // getter/setterメソッド
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public double getBalance() {
        return balance;
    }
    
    // 【注意】残高の直接設定は通常は提供しない
    // 必要に応じて特別な認証を伴う管理者用メソッドとして実装
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    // 【発展解答】残高の状態判定メソッド
    public boolean hasBalance() {
        return balance > 0;
    }
    
    public boolean canWithdraw(double amount) {
        return balance >= amount && amount > 0;
    }
    
    public boolean isOverdrawn() {
        return balance < 0;
    }
    
    // 【発展解答】利息計算メソッド
    public void addInterest(double rate) {
        if (rate > 0 && balance > 0) {
            double interest = balance * rate / 100;
            this.balance += interest;
            System.out.printf("利息 %.2f円が追加されました。（利率: %.2f%%）%n", interest, rate);
        }
    }
    
    // 【発展解答】口座の比較メソッド
    public boolean hasSameOwner(BankAccount other) {
        return this.ownerName.equals(other.ownerName);
    }
    
    public boolean hasMoreBalance(BankAccount other) {
        return this.balance > other.balance;
    }
    
    // 【発展解答】toString()メソッドのオーバーライド
    @Override
    public String toString() {
        return String.format("BankAccount{accountNumber='%s', balance=%.2f, ownerName='%s'}", 
                           accountNumber, balance, ownerName);
    }
    
    // 【発展解答】equals()メソッドのオーバーライド
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BankAccount account = (BankAccount) obj;
        return accountNumber.equals(account.accountNumber);  // 口座番号で判定
    }
    
    // 【発展解答】hashCode()メソッドのオーバーライド
    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
    
    // 【発展解答】静的メソッド - 複数口座の合計残高
    public static double getTotalBalance(BankAccount[] accounts) {
        double total = 0.0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
    }
    
    // 【発展解答】静的メソッド - 最も残高の多い口座を返す
    public static BankAccount getRichestAccount(BankAccount[] accounts) {
        if (accounts.length == 0) return null;
        
        BankAccount richest = accounts[0];
        for (BankAccount account : accounts) {
            if (account.getBalance() > richest.getBalance()) {
                richest = account;
            }
        }
        return richest;
    }
    
    // 【発展解答】静的メソッド - 口座番号の妥当性チェック
    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && 
               accountNumber.length() >= 6 && 
               accountNumber.matches("\\d+");  // 数字のみ
    }
}