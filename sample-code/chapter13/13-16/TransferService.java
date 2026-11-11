import java.math.BigDecimal;

public class TransferService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    public TransferService(AccountRepository accountRepository,
                           TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    
    public TransferResult transfer(String fromAccountId, 
                                 String toAccountId, 
                                 BigDecimal amount) 
            throws BusinessException {
        
        IO.println("送金処理開始: " + fromAccountId + " -> " + toAccountId + ", 金額: " + amount);
        
        try {
            // 入力検証
            validateTransferRequest(fromAccountId, toAccountId, amount);
            
            // アカウント取得
            Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));
            
            Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));
            
            // ビジネスルールチェック
            checkBusinessRules(fromAccount, amount);
            
            // 送金実行
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            
            // トランザクション記録
            Transaction transaction = new Transaction(
                fromAccountId, toAccountId, amount, TransactionType.TRANSFER);
            transactionRepository.save(transaction);
            
            // 結果返却
            TransferResult result = new TransferResult(
                transaction.getId(), 
                fromAccount.getBalance(), 
                toAccount.getBalance());
            
            IO.println("送金処理完了: " + result);
            return result;
            
        } catch (BusinessException e) {
            // ビジネス例外は呼び出し元に伝播
            System.err.println("送金処理でビジネスエラー: " + e.getMessage());
            throw e;
            
        } catch (Exception e) {
            // 予期せぬエラー（DataAccessExceptionなども含む）
            e.printStackTrace();
            // 非検査例外に包み、原因の例外を渡して投げ直す
            throw new RuntimeException("送金処理中に予期せぬエラーが発生しました", e);
        }
    }
    
    private void validateTransferRequest(String fromAccountId, 
                                       String toAccountId, 
                                       BigDecimal amount) 
            throws ValidationException {
        
        if (fromAccountId == null || fromAccountId.isEmpty()) {
            throw new ValidationException("送金元アカウントIDが指定されていません");
        }
        
        if (toAccountId == null || toAccountId.isEmpty()) {
            throw new ValidationException("送金先アカウントIDが指定されていません");
        }
        
        if (fromAccountId.equals(toAccountId)) {
            throw new ValidationException("同一アカウント間での送金はできません");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("送金額は0より大きい値を指定してください");
        }
    }
    
    private void checkBusinessRules(Account account, BigDecimal amount) 
            throws BusinessException {
        
        if (account.isFrozen()) {
            throw new AccountFrozenException(account.getId());
        }
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                account.getBalance().doubleValue(), 
                amount.doubleValue());
        }
        
        BigDecimal dailyLimit = account.getDailyTransferLimit();
        BigDecimal todayTotal = transactionRepository
            .getTodayTotalAmount(account.getId());
        
        if (todayTotal.add(amount).compareTo(dailyLimit) > 0) {
            throw new DailyLimitExceededException(
                dailyLimit.doubleValue(), 
                todayTotal.doubleValue(), 
                amount.doubleValue());
        }
    }
}
