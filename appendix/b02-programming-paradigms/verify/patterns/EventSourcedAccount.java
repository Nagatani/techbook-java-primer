import java.math.BigDecimal;
import java.util.List;

public class EventSourcedAccount {
    // イベントの定義
    sealed interface AccountEvent
            permits AccountCreated, MoneyDeposited, MoneyWithdrawn {}

    record AccountCreated(String accountId, String owner)
            implements AccountEvent {}
    record MoneyDeposited(String accountId, Money amount)
            implements AccountEvent {}
    record MoneyWithdrawn(String accountId, Money amount)
            implements AccountEvent {}
    
    // イベントを順に適用して、現在の状態を求める
    public static class AccountProjection {
        public AccountState project(List<AccountEvent> events) {
            AccountState state = AccountState.empty();
            for (AccountEvent event : events) {
                state = applyEvent(state, event);
            }
            return state;
        }
        
        private AccountState applyEvent(AccountState state,
                AccountEvent event) {
            return switch (event) {
                case AccountCreated(var id, var owner) -> 
                    new AccountState(id, owner, Money.ZERO);
                    
                case MoneyDeposited(var id, var amount) -> 
                    state.withBalance(state.balance().add(amount));
                    
                case MoneyWithdrawn(var id, var amount) -> 
                    state.withBalance(state.balance().subtract(amount));
            };
        }
    }

    public static void main(String[] args) {
        List<AccountEvent> events = List.of(
                new AccountCreated("AC001", "田中"),
                new MoneyDeposited("AC001", new Money(new BigDecimal("10000"))),
                new MoneyWithdrawn("AC001", new Money(new BigDecimal("3000"))),
                new MoneyDeposited("AC001", new Money(new BigDecimal("500"))));
        AccountProjection projection = new AccountProjection();
        // 途中までのイベントを適用すれば、その時点の状態もわかる
        IO.println("2件目まで: " + projection.project(events.subList(0, 2)));
        IO.println("すべて: " + projection.project(events));
    }
}
