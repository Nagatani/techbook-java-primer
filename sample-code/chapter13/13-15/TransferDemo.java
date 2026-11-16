import java.math.BigDecimal;

// 本文では省略しているmainメソッド（送金処理を試す）
public class TransferDemo {
    public static void main(String[] args) {
        InMemoryAccountRepository accounts = new InMemoryAccountRepository();
        accounts.add(new Account("A001", new BigDecimal("10000"), false));
        accounts.add(new Account("A002", new BigDecimal("5000"), false));
        TransferService service =
            new TransferService(accounts, new InMemoryTransactionRepository());

        tryTransfer(service, "A001", "A002", new BigDecimal("3000"));
        tryTransfer(service, "A001", "A002", new BigDecimal("50000"));
        tryTransfer(service, "A001", "A999", new BigDecimal("1000"));
    }

    private static void tryTransfer(TransferService service, String from,
                                    String to, BigDecimal amount) {
        try {
            TransferResult result = service.transfer(from, to, amount);
            IO.println("送金元の残高: " + result.fromBalance()
                + "、送金先の残高: " + result.toBalance());
        } catch (BusinessException e) {
            IO.println("[" + e.getErrorCode() + "] " + e.getMessage());
        }
        IO.println();
    }
}
