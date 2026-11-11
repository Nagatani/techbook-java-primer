import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// 本文では省略している補助の型（送金処理を試せる最小限の実装）

interface AccountRepository {
    Optional<Account> findById(String id);
}

interface TransactionRepository {
    void save(Transaction transaction);
    BigDecimal getTodayTotalAmount(String accountId);
}

class Account {
    private final String id;
    private BigDecimal balance;
    private final boolean frozen;

    Account(String id, BigDecimal balance, boolean frozen) {
        this.id = id;
        this.balance = balance;
        this.frozen = frozen;
    }

    String getId() { return id; }
    BigDecimal getBalance() { return balance; }
    boolean isFrozen() { return frozen; }
    BigDecimal getDailyTransferLimit() { return new BigDecimal("1000000"); }
    void withdraw(BigDecimal amount) { balance = balance.subtract(amount); }
    void deposit(BigDecimal amount) { balance = balance.add(amount); }
}

enum TransactionType { TRANSFER }

class Transaction {
    private static int count = 0;
    private final String id = "T%04d".formatted(++count);
    Transaction(String from, String to, BigDecimal amount, TransactionType type) {}
    String getId() { return id; }
}

record TransferResult(String transactionId, BigDecimal fromBalance, BigDecimal toBalance) {}

class ValidationException extends BusinessException {
    ValidationException(String message) { super("VAL-001", message); }
}

class AccountNotFoundException extends BusinessException {
    AccountNotFoundException(String accountId) { super("ACC-404", "アカウントが見つかりません: " + accountId); }
}

class AccountFrozenException extends BusinessException {
    AccountFrozenException(String accountId) { super("ACC-002", "アカウントが凍結されています: " + accountId); }
}

class DailyLimitExceededException extends BusinessException {
    DailyLimitExceededException(double limit, double todayTotal, double amount) {
        super("ACC-003", "1日の送金限度額を超えています");
    }
}

class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accounts = new HashMap<>();
    void add(Account account) { accounts.put(account.getId(), account); }
    public Optional<Account> findById(String id) { return Optional.ofNullable(accounts.get(id)); }
}

class InMemoryTransactionRepository implements TransactionRepository {
    public void save(Transaction transaction) {}
    public BigDecimal getTodayTotalAmount(String accountId) { return BigDecimal.ZERO; }
}
