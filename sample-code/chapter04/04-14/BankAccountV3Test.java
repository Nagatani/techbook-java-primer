public class BankAccountV3Test {
    public static void main(String[] args) {
        BankAccountV3 account = new BankAccountV3("123456", 10000);
        
        IO.println("口座番号: " + account.getAccountNumber());
        IO.println("初期残高: " + account.getBalance());
        
        account.deposit(5000);
        IO.println("\n5000円入金後の残高: " + account.getBalance());
        
        // 出金成功
        if (account.withdraw(3000)) {
            IO.println("3000円出金成功後の残高: " + account.getBalance());
        }
        
        // 出金失敗
        if (!account.withdraw(20000)) {
            IO.println("20000円出金失敗（残高不足）");
        }
        
        IO.println("\n取引履歴:");
        for (String transaction : account.getTransactionHistory()) {
            IO.println("  " + transaction);
        }
    }
}
