/**
 * 第3章 演習問題3: BankAccountTestクラスの解答例
 * 
 * 【テストの目的】
 * - コンストラクタチェーンの動作確認
 * - 入金・出金メソッドの動作確認
 * - エラーケースの処理確認
 * - 実際の銀行口座操作のシミュレーション
 * 
 * 【デバッグのコツ】
 * 1. 残高の変化を追跡する
 * 2. 戻り値（boolean）を必ず確認する
 * 3. エラーメッセージの内容を確認する
 * 4. 境界値（0円、負の値）をテストする
 */
public class BankAccountTest {
    public static void main(String[] args) {
        System.out.println("=== 第3章 演習問題3: BankAccount クラスのテスト ===");
        
        // 【基本テスト】コンストラクタのテスト
        testConstructors();
        
        // 【基本テスト】入金・出金メソッドのテスト
        testDepositWithdraw();
        
        // 【応用テスト】オーバーロードメソッドのテスト
        testOverloadedMethods();
        
        // 【発展テスト】振込機能のテスト
        testTransferMethods();
        
        // 【実践テスト】実際の使用例
        testPracticalUsage();
        
        // 【エラーテスト】エラーケースの処理
        testErrorCases();
        
        System.out.println("\n=== テスト完了 ===");
    }
    
    /**
     * コンストラクタのテスト
     */
    private static void testConstructors() {
        System.out.println("\n--- コンストラクタのテスト ---");
        
        // 口座番号のみのコンストラクタ
        BankAccount account1 = new BankAccount("12345");
        System.out.print("口座1: ");
        account1.displayAccountInfo();
        
        // 口座番号と名前のコンストラクタ
        BankAccount account2 = new BankAccount("67890", "田中太郎");
        System.out.print("口座2: ");
        account2.displayAccountInfo();
        
        // 全項目のコンストラクタ
        BankAccount account3 = new BankAccount("11111", 50000.0, "佐藤花子");
        System.out.print("口座3: ");
        account3.displayAccountInfo();
        
        // デフォルトコンストラクタ
        BankAccount account4 = new BankAccount();
        System.out.print("口座4: ");
        account4.displayAccountInfo();
        
        // コピーコンストラクタ
        BankAccount account5 = new BankAccount(account3);
        System.out.print("口座5（コピー）: ");
        account5.displayAccountInfo();
        
        // コンストラクタチェーンの確認
        System.out.println("\nコンストラクタチェーンが正常に動作しています。");
    }
    
    /**
     * 入金・出金メソッドのテスト
     */
    private static void testDepositWithdraw() {
        System.out.println("\n--- 入金・出金メソッドのテスト ---");
        
        BankAccount account = new BankAccount("12345", "テスト太郎");
        System.out.println("初期状態:");
        account.displayAccountInfo();
        
        // 入金テスト
        System.out.println("\n--- 入金テスト ---");
        account.deposit(10000.0);
        account.displayAccountInfo();
        
        account.deposit(5000.0);
        account.displayAccountInfo();
        
        // 出金テスト
        System.out.println("\n--- 出金テスト ---");
        boolean result1 = account.withdraw(3000.0);
        System.out.println("出金結果: " + result1);
        account.displayAccountInfo();
        
        boolean result2 = account.withdraw(20000.0);  // 残高不足
        System.out.println("出金結果: " + result2);
        account.displayAccountInfo();
        
        // 正常な出金
        boolean result3 = account.withdraw(5000.0);
        System.out.println("出金結果: " + result3);
        account.displayAccountInfo();
    }
    
    /**
     * オーバーロードメソッドのテスト
     */
    private static void testOverloadedMethods() {
        System.out.println("\n--- オーバーロードメソッドのテスト ---");
        
        BankAccount account = new BankAccount("54321", 20000.0, "オーバーロード太郎");
        System.out.println("初期状態:");
        account.displayAccountInfo();
        
        // 入金メソッドのオーバーロード
        System.out.println("\n--- 入金メソッドのオーバーロード ---");
        account.deposit(3000.0);                    // 基本の入金
        account.deposit(2000.0, "給与");            // メッセージ付き入金
        account.deposit(1000.0, "ボーナス");        // メッセージ付き入金
        
        account.displayAccountInfo();
        
        // 出金メソッドのオーバーロード
        System.out.println("\n--- 出金メソッドのオーバーロード ---");
        account.withdraw(5000.0);                   // 基本の出金
        account.withdraw(3000.0, 108.0);            // 手数料付き出金
        
        account.displayAccountInfo();
        
        // 手数料付き出金（残高不足）
        System.out.println("\n--- 手数料付き出金（残高不足） ---");
        account.withdraw(15000.0, 216.0);
        account.displayAccountInfo();
    }
    
    /**
     * 振込機能のテスト
     */
    private static void testTransferMethods() {
        System.out.println("\n--- 振込機能のテスト ---");
        
        BankAccount sender = new BankAccount("111111", 30000.0, "送金者");
        BankAccount receiver = new BankAccount("222222", 10000.0, "受取人");
        
        System.out.println("振込前:");
        sender.displayAccountInfo();
        receiver.displayAccountInfo();
        
        // 基本の振込
        System.out.println("\n--- 基本の振込 ---");
        boolean result1 = sender.transfer(receiver, 5000.0);
        System.out.println("振込結果: " + result1);
        
        System.out.println("振込後:");
        sender.displayAccountInfo();
        receiver.displayAccountInfo();
        
        // 手数料付き振込
        System.out.println("\n--- 手数料付き振込 ---");
        boolean result2 = sender.transfer(receiver, 3000.0, 216.0);
        System.out.println("振込結果: " + result2);
        
        System.out.println("手数料付き振込後:");
        sender.displayAccountInfo();
        receiver.displayAccountInfo();
        
        // 残高不足での振込
        System.out.println("\n--- 残高不足での振込 ---");
        boolean result3 = sender.transfer(receiver, 50000.0);
        System.out.println("振込結果: " + result3);
    }
    
    /**
     * 実践的な使用例のテスト
     */
    private static void testPracticalUsage() {
        System.out.println("\n--- 実践的な使用例のテスト ---");
        
        System.out.println("=== 銀行口座管理システム ===");
        
        // 複数の口座を作成
        BankAccount[] accounts = {
            new BankAccount("001001", 100000.0, "田中太郎"),
            new BankAccount("001002", 50000.0, "佐藤花子"),
            new BankAccount("001003", 200000.0, "鈴木一郎"),
            new BankAccount("001004", 75000.0, "高橋美咲")
        };
        
        System.out.println("=== 全口座の初期状態 ===");
        for (int i = 0; i < accounts.length; i++) {
            System.out.print("口座" + (i + 1) + ": ");
            accounts[i].displayDetailedAccountInfo();
        }
        
        // 利息の追加
        System.out.println("\n=== 利息の追加（年利1.5%） ===");
        for (BankAccount account : accounts) {
            account.addInterest(1.5);
        }
        
        // 合計残高の計算
        double totalBalance = BankAccount.getTotalBalance(accounts);
        System.out.printf("全口座の合計残高: %.2f円%n", totalBalance);
        
        // 最も残高の多い口座
        BankAccount richest = BankAccount.getRichestAccount(accounts);
        System.out.println("最も残高の多い口座: " + richest.toString());
        
        // 実際の取引シミュレーション
        System.out.println("\n=== 取引シミュレーション ===");
        
        // 田中さんから佐藤さんへの振込
        System.out.println("田中さんから佐藤さんへ30,000円振込:");
        accounts[0].transfer(accounts[1], 30000.0, 324.0);
        
        // 鈴木さんから高橋さんへの振込
        System.out.println("\n鈴木さんから高橋さんへ15,000円振込:");
        accounts[2].transfer(accounts[3], 15000.0);
        
        // 最終残高の確認
        System.out.println("\n=== 最終残高 ===");
        for (int i = 0; i < accounts.length; i++) {
            System.out.print("口座" + (i + 1) + ": ");
            accounts[i].displayAccountInfo();
        }
    }
    
    /**
     * エラーケースのテスト
     */
    private static void testErrorCases() {
        System.out.println("\n--- エラーケースのテスト ---");
        
        BankAccount account = new BankAccount("999999", 1000.0, "エラーテスト");
        
        // 負の値での入金
        System.out.println("--- 負の値での入金 ---");
        account.deposit(-500.0);
        account.displayAccountInfo();
        
        // ゼロでの入金
        System.out.println("\n--- ゼロでの入金 ---");
        account.deposit(0.0);
        account.displayAccountInfo();
        
        // 負の値での出金
        System.out.println("\n--- 負の値での出金 ---");
        account.withdraw(-200.0);
        account.displayAccountInfo();
        
        // 残高不足での出金
        System.out.println("\n--- 残高不足での出金 ---");
        account.withdraw(2000.0);
        account.displayAccountInfo();
        
        // 負の手数料での出金
        System.out.println("\n--- 負の手数料での出金 ---");
        account.withdraw(500.0, -100.0);
        account.displayAccountInfo();
        
        // 境界値テスト
        System.out.println("\n--- 境界値テスト ---");
        BankAccount boundaryAccount = new BankAccount("000000", 0.0, "境界値");
        System.out.println("残高0円の口座:");
        boundaryAccount.displayAccountInfo();
        
        System.out.println("残高0円での出金:");
        boundaryAccount.withdraw(1.0);
        
        System.out.println("残高0円での入金:");
        boundaryAccount.deposit(1.0);
        boundaryAccount.displayAccountInfo();
    }
    
    /**
     * 判定メソッドのテスト
     */
    private static void testJudgmentMethods() {
        System.out.println("\n--- 判定メソッドのテスト ---");
        
        BankAccount account1 = new BankAccount("111111", 50000.0, "判定太郎");
        BankAccount account2 = new BankAccount("222222", 30000.0, "判定花子");
        BankAccount account3 = new BankAccount("333333", 0.0, "判定太郎");
        
        // 残高の有無判定
        System.out.println("account1 残高有無: " + account1.hasBalance());
        System.out.println("account3 残高有無: " + account3.hasBalance());
        
        // 出金可能性判定
        System.out.println("account1 10,000円出金可能: " + account1.canWithdraw(10000.0));
        System.out.println("account1 60,000円出金可能: " + account1.canWithdraw(60000.0));
        
        // 同じ名義人かどうか
        System.out.println("account1とaccount3 同じ名義人: " + account1.hasSameOwner(account3));
        System.out.println("account1とaccount2 同じ名義人: " + account1.hasSameOwner(account2));
        
        // 残高の比較
        System.out.println("account1 > account2 (残高): " + account1.hasMoreBalance(account2));
        System.out.println("account2 > account1 (残高): " + account2.hasMoreBalance(account1));
    }
    
    /**
     * 静的メソッドのテスト
     */
    private static void testStaticMethods() {
        System.out.println("\n--- 静的メソッドのテスト ---");
        
        // 口座番号の妥当性チェック
        System.out.println("口座番号妥当性チェック:");
        System.out.println("'123456' -> " + BankAccount.isValidAccountNumber("123456"));
        System.out.println("'12345' -> " + BankAccount.isValidAccountNumber("12345"));
        System.out.println("'abc123' -> " + BankAccount.isValidAccountNumber("abc123"));
        System.out.println("null -> " + BankAccount.isValidAccountNumber(null));
    }
}