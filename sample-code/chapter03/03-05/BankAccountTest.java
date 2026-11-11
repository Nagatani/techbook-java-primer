// BankAccountTest.java
public class BankAccountTest {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        IO.println("初期残高: " + account.getBalance() + "円");
        
        account.deposit(10000);
        IO.println("10000円入金後: " + account.getBalance() + "円");
        
        account.deposit(-5000);  // 負の値は無視される
        IO.println("負の値入金後: " + account.getBalance() + "円");
    }
}
