// BankAccountV1Test.java
public class BankAccountV1Test {
    public static void main(String[] args) {
        BankAccountV1 account = new BankAccountV1("123456", 10000);
        
        System.out.println("初期残高: " + account.getBalance());
        
        account.deposit(5000);
        System.out.println("5000円入金後: " + account.getBalance());
        
        account.withdraw(3000);
        System.out.println("3000円出金後: " + account.getBalance());
        
        // 問題：負の金額でも処理される
        account.deposit(-1000);
        System.out.println("負の金額入金後: " + account.getBalance());
    }
}
