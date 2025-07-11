/**
 * リスト21-13
 * PaymentServiceTestクラス
 * 
 * 元ファイル: chapter21-unit-testing.md (418行目)
 */

public class PaymentServiceTest {
    
    @Nested
    @DisplayName("正常系のテスト")
    class SuccessfulPayments {
        @Test
        @DisplayName("クレジットカードでの支払いが成功する")
        void testCreditCardPayment() {
            // テスト実装
        }
    }
    
    @Nested
    @DisplayName("異常系のテスト")
    class FailedPayments {
        @Test
        @DisplayName("残高不足の場合は例外が発生する")
        void testInsufficientBalance() {
            // テスト実装
        }
    }
}