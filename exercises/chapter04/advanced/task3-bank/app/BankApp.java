package app;

import bank.Account;
import bank.Bank;

public class BankApp {
    public static void main(String[] args) {
        Bank bank = new Bank("みどり銀行");

        IO.println("=== 口座の開設 ===");
        Account yamada = bank.open("A001", "山田太郎", 100000);
        Account sato = bank.open("A002", "佐藤花子", 50000);
        bank.open("A003", "鈴木一郎");
        bank.open("A001", "田中次郎", 1000);

        IO.println();
        IO.println("=== 振込 ===");
        bank.transfer(yamada, sato, 30000);
        bank.transfer(sato, yamada, 100000);
        bank.transfer("A002", "A003", 79800);
        bank.transfer("A003", "A009", 500);
        bank.transfer(yamada, yamada, 1000);
        // yamada.withdraw(1000); や new Transaction(...) はパッケージが違うのでコンパイルエラーになる

        IO.println();
        IO.println("=== 取引の履歴 ===");
        yamada.showHistory();
        sato.showHistory();

        IO.println();
        IO.println("=== 監査 ===");
        bank.audit();
    }
}
