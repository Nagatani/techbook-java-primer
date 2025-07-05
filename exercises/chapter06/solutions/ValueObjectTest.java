import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 値オブジェクトのテストクラス
 * 
 * 【テストの観点】
 * 1. 値オブジェクトの不変性
 * 2. バリデーションの動作
 * 3. equals/hashCodeの動作
 * 4. 境界値テスト
 * 5. 異常値テスト
 */
public class ValueObjectTest {
    
    @Nested
    @DisplayName("Moneyクラスのテスト")
    class MoneyTest {
        
        @Test
        @DisplayName("正常な金額でインスタンス作成")
        void testValidMoney() {
            Money money = new Money(1000, "JPY");
            assertEquals(1000, money.getAmount());
            assertEquals("JPY", money.getCurrency());
        }
        
        @Test
        @DisplayName("デフォルト通貨でのインスタンス作成")
        void testDefaultCurrency() {
            Money money = new Money(1000);
            assertEquals(1000, money.getAmount());
            assertEquals("JPY", money.getCurrency());
        }
        
        @Test
        @DisplayName("負の金額でエラー")
        void testNegativeAmount() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Money(-1000, "JPY");
            });
        }
        
        @Test
        @DisplayName("null通貨でエラー")
        void testNullCurrency() {
            assertThrows(NullPointerException.class, () -> {
                new Money(1000, null);
            });
        }
        
        @Test
        @DisplayName("金額の加算")
        void testAdd() {
            Money money1 = new Money(1000, "JPY");
            Money money2 = new Money(2000, "JPY");
            Money result = money1.add(money2);
            
            assertEquals(3000, result.getAmount());
            assertEquals("JPY", result.getCurrency());
            
            // 元のインスタンスは変更されない
            assertEquals(1000, money1.getAmount());
            assertEquals(2000, money2.getAmount());
        }
        
        @Test
        @DisplayName("異なる通貨の加算でエラー")
        void testAddDifferentCurrency() {
            Money jpyMoney = new Money(1000, "JPY");
            Money usdMoney = new Money(10, "USD");
            
            assertThrows(IllegalArgumentException.class, () -> {
                jpyMoney.add(usdMoney);
            });
        }
        
        @Test
        @DisplayName("金額の減算")
        void testSubtract() {
            Money money1 = new Money(3000, "JPY");
            Money money2 = new Money(1000, "JPY");
            Money result = money1.subtract(money2);
            
            assertEquals(2000, result.getAmount());
            assertEquals("JPY", result.getCurrency());
        }
        
        @Test
        @DisplayName("金額の乗算")
        void testMultiply() {
            Money money = new Money(1000, "JPY");
            Money result = money.multiply(1.5);
            
            assertEquals(1500, result.getAmount());
            assertEquals("JPY", result.getCurrency());
        }
        
        @Test
        @DisplayName("金額の比較")
        void testIsGreaterThan() {
            Money money1 = new Money(2000, "JPY");
            Money money2 = new Money(1000, "JPY");
            
            assertTrue(money1.isGreaterThan(money2));
            assertFalse(money2.isGreaterThan(money1));
        }
        
        @Test
        @DisplayName("equalsとhashCodeのテスト")
        void testEqualsAndHashCode() {
            Money money1 = new Money(1000, "JPY");
            Money money2 = new Money(1000, "JPY");
            Money money3 = new Money(2000, "JPY");
            
            assertEquals(money1, money2);
            assertEquals(money1.hashCode(), money2.hashCode());
            assertNotEquals(money1, money3);
        }
    }
    
    @Nested
    @DisplayName("Emailクラスのテスト")
    class EmailTest {
        
        @Test
        @DisplayName("有効なメールアドレス")
        void testValidEmail() {
            Email email = new Email("test@example.com");
            assertEquals("test@example.com", email.getAddress());
            assertEquals("test", email.getLocalPart());
            assertEquals("example.com", email.getDomain());
        }
        
        @Test
        @DisplayName("無効なメールアドレス")
        void testInvalidEmail() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Email("invalid-email");
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                new Email("test@");
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                new Email("@example.com");
            });
        }
        
        @Test
        @DisplayName("nullメールアドレス")
        void testNullEmail() {
            assertThrows(NullPointerException.class, () -> {
                new Email(null);
            });
        }
        
        @Test
        @DisplayName("複雑なメールアドレス")
        void testComplexEmail() {
            Email email = new Email("user.name+tag@example.co.jp");
            assertEquals("user.name+tag", email.getLocalPart());
            assertEquals("example.co.jp", email.getDomain());
        }
    }
    
    @Nested
    @DisplayName("PhoneNumberクラスのテスト")
    class PhoneNumberTest {
        
        @Test
        @DisplayName("有効な電話番号")
        void testValidPhoneNumber() {
            PhoneNumber phone = new PhoneNumber("03-1234-5678");
            assertEquals("03-1234-5678", phone.getNumber());
            assertEquals("03123456789", phone.getNumberWithoutHyphens());
        }
        
        @Test
        @DisplayName("無効な電話番号")
        void testInvalidPhoneNumber() {
            assertThrows(IllegalArgumentException.class, () -> {
                new PhoneNumber("123-45-6789");
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                new PhoneNumber("03-1234-567");
            });
        }
        
        @Test
        @DisplayName("様々な形式の電話番号")
        void testVariousPhoneFormats() {
            assertDoesNotThrow(() -> new PhoneNumber("03-1234-5678"));
            assertDoesNotThrow(() -> new PhoneNumber("090-1234-5678"));
            assertDoesNotThrow(() -> new PhoneNumber("0120-123-456"));
        }
    }
    
    @Nested
    @DisplayName("PostalCodeクラスのテスト")
    class PostalCodeTest {
        
        @Test
        @DisplayName("有効な郵便番号")
        void testValidPostalCode() {
            PostalCode postal = new PostalCode("123-4567");
            assertEquals("123-4567", postal.getCode());
            assertEquals("1234567", postal.getCodeWithoutHyphen());
            assertEquals("123", postal.getFirstThreeDigits());
            assertEquals("4567", postal.getLastFourDigits());
        }
        
        @Test
        @DisplayName("無効な郵便番号")
        void testInvalidPostalCode() {
            assertThrows(IllegalArgumentException.class, () -> {
                new PostalCode("12-4567");
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                new PostalCode("123-456");
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                new PostalCode("1234567");
            });
        }
    }
    
    @Nested
    @DisplayName("Ageクラスのテスト")
    class AgeTest {
        
        @Test
        @DisplayName("有効な年齢")
        void testValidAge() {
            Age age = new Age(25);
            assertEquals(25, age.getValue());
            assertTrue(age.isAdult());
            assertFalse(age.isMinor());
            assertFalse(age.isSenior());
        }
        
        @Test
        @DisplayName("境界値テスト")
        void testAgeBoundaries() {
            Age minor = new Age(19);
            assertTrue(minor.isMinor());
            assertFalse(minor.isAdult());
            
            Age adult = new Age(20);
            assertFalse(adult.isMinor());
            assertTrue(adult.isAdult());
            assertFalse(adult.isSenior());
            
            Age senior = new Age(65);
            assertTrue(senior.isAdult());
            assertTrue(senior.isSenior());
        }
        
        @Test
        @DisplayName("無効な年齢")
        void testInvalidAge() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Age(-1);
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                new Age(151);
            });
        }
        
        @Test
        @DisplayName("年齢の増加")
        void testAgeIncrement() {
            Age age = new Age(25);
            Age nextAge = age.increment();
            
            assertEquals(26, nextAge.getValue());
            assertEquals(25, age.getValue());  // 元のインスタンスは変更されない
        }
        
        @Test
        @DisplayName("境界値の年齢")
        void testBoundaryAges() {
            assertDoesNotThrow(() -> new Age(0));
            assertDoesNotThrow(() -> new Age(150));
        }
    }
    
    @Nested
    @DisplayName("Addressクラスのテスト")
    class AddressTest {
        
        @Test
        @DisplayName("有効な住所")
        void testValidAddress() {
            PostalCode postalCode = new PostalCode("123-4567");
            Address address = new Address(postalCode, "東京都", "渋谷区", "1-1-1");
            
            assertEquals(postalCode, address.getPostalCode());
            assertEquals("東京都", address.getPrefecture());
            assertEquals("渋谷区", address.getCity());
            assertEquals("1-1-1", address.getStreetAddress());
            assertEquals("〒123-4567 東京都渋谷区1-1-1", address.getFullAddress());
        }
        
        @Test
        @DisplayName("null値でエラー")
        void testNullValues() {
            PostalCode postalCode = new PostalCode("123-4567");
            
            assertThrows(NullPointerException.class, () -> {
                new Address(null, "東京都", "渋谷区", "1-1-1");
            });
            
            assertThrows(NullPointerException.class, () -> {
                new Address(postalCode, null, "渋谷区", "1-1-1");
            });
            
            assertThrows(NullPointerException.class, () -> {
                new Address(postalCode, "東京都", null, "1-1-1");
            });
            
            assertThrows(NullPointerException.class, () -> {
                new Address(postalCode, "東京都", "渋谷区", null);
            });
        }
    }
    
    @Nested
    @DisplayName("Customerクラスのテスト")
    class CustomerTest {
        
        @Test
        @DisplayName("顧客情報の作成")
        void testCustomerCreation() {
            Age age = new Age(25);
            Email email = new Email("customer@example.com");
            PhoneNumber phone = new PhoneNumber("03-1234-5678");
            PostalCode postalCode = new PostalCode("123-4567");
            Address address = new Address(postalCode, "東京都", "渋谷区", "1-1-1");
            
            ValueObject.Customer customer = new ValueObject.Customer(
                "田中太郎", age, email, phone, address);
            
            assertEquals("田中太郎", customer.getName());
            assertEquals(age, customer.getAge());
            assertEquals(email, customer.getEmail());
            assertEquals(phone, customer.getPhoneNumber());
            assertEquals(address, customer.getAddress());
            
            assertTrue(customer.canPurchaseAlcohol());
            assertFalse(customer.isSeniorDiscount());
        }
        
        @Test
        @DisplayName("未成年顧客のテスト")
        void testMinorCustomer() {
            Age age = new Age(19);
            Email email = new Email("minor@example.com");
            PhoneNumber phone = new PhoneNumber("03-1234-5678");
            PostalCode postalCode = new PostalCode("123-4567");
            Address address = new Address(postalCode, "東京都", "渋谷区", "1-1-1");
            
            ValueObject.Customer customer = new ValueObject.Customer(
                "未成年太郎", age, email, phone, address);
            
            assertFalse(customer.canPurchaseAlcohol());
            assertFalse(customer.isSeniorDiscount());
        }
        
        @Test
        @DisplayName("シニア顧客のテスト")
        void testSeniorCustomer() {
            Age age = new Age(70);
            Email email = new Email("senior@example.com");
            PhoneNumber phone = new PhoneNumber("03-1234-5678");
            PostalCode postalCode = new PostalCode("123-4567");
            Address address = new Address(postalCode, "東京都", "渋谷区", "1-1-1");
            
            ValueObject.Customer customer = new ValueObject.Customer(
                "シニア太郎", age, email, phone, address);
            
            assertTrue(customer.canPurchaseAlcohol());
            assertTrue(customer.isSeniorDiscount());
        }
    }
    
    @Nested
    @DisplayName("Orderクラスのテスト")
    class OrderTest {
        
        private ValueObject.Customer createTestCustomer(int age) {
            Age customerAge = new Age(age);
            Email email = new Email("customer@example.com");
            PhoneNumber phone = new PhoneNumber("03-1234-5678");
            PostalCode postalCode = new PostalCode("123-4567");
            Address address = new Address(postalCode, "東京都", "渋谷区", "1-1-1");
            
            return new ValueObject.Customer("テスト太郎", customerAge, email, phone, address);
        }
        
        @Test
        @DisplayName("通常の注文")
        void testNormalOrder() {
            ValueObject.Customer customer = createTestCustomer(30);
            Money totalAmount = new Money(1000);
            
            ValueObject.Order order = new ValueObject.Order("ORD-001", customer, totalAmount);
            
            assertEquals("ORD-001", order.getOrderId());
            assertEquals(customer, order.getCustomer());
            assertEquals(totalAmount, order.getTotalAmount());
            
            // 税額計算（10%）
            Money expectedTax = new Money(100);
            assertEquals(expectedTax, order.getTaxAmount());
            
            // 小計計算
            Money expectedSubtotal = new Money(900);
            assertEquals(expectedSubtotal, order.getSubtotalAmount());
            
            // 最終金額（割引なし）
            assertEquals(totalAmount, order.getFinalAmount());
        }
        
        @Test
        @DisplayName("シニア割引適用の注文")
        void testSeniorDiscountOrder() {
            ValueObject.Customer seniorCustomer = createTestCustomer(70);
            Money totalAmount = new Money(1000);
            
            ValueObject.Order order = new ValueObject.Order("ORD-002", seniorCustomer, totalAmount);
            
            // シニア割引適用（10%割引）
            Money expectedFinalAmount = new Money(900);
            assertEquals(expectedFinalAmount, order.getFinalAmount());
        }
        
        @Test
        @DisplayName("null値でエラー")
        void testOrderWithNullValues() {
            ValueObject.Customer customer = createTestCustomer(30);
            Money totalAmount = new Money(1000);
            
            assertThrows(NullPointerException.class, () -> {
                new ValueObject.Order(null, customer, totalAmount);
            });
            
            assertThrows(NullPointerException.class, () -> {
                new ValueObject.Order("ORD-001", null, totalAmount);
            });
            
            assertThrows(NullPointerException.class, () -> {
                new ValueObject.Order("ORD-001", customer, null);
            });
        }
    }
    
    @Test
    @DisplayName("値オブジェクトのequals契約")
    void testValueObjectEqualsContract() {
        // 同じ値を持つインスタンスは等価
        Money money1 = new Money(1000);
        Money money2 = new Money(1000);
        assertEquals(money1, money2);
        
        // 異なる値を持つインスタンスは非等価
        Money money3 = new Money(2000);
        assertNotEquals(money1, money3);
        
        // 反射性
        assertEquals(money1, money1);
        
        // 対称性
        assertEquals(money1, money2);
        assertEquals(money2, money1);
        
        // 推移性
        Money money4 = new Money(1000);
        assertEquals(money1, money2);
        assertEquals(money2, money4);
        assertEquals(money1, money4);
    }
    
    @Test
    @DisplayName("値オブジェクトのhashCode契約")
    void testValueObjectHashCodeContract() {
        Money money1 = new Money(1000);
        Money money2 = new Money(1000);
        
        // 等価なオブジェクトは同じハッシュコード
        assertEquals(money1.hashCode(), money2.hashCode());
        
        // 同じオブジェクトは常に同じハッシュコード
        assertEquals(money1.hashCode(), money1.hashCode());
    }
    
    @Test
    @DisplayName("demonstrateUsageメソッドのテスト")
    void testDemonstrateUsage() {
        // 例外が発生しないことを確認
        assertDoesNotThrow(() -> ValueObject.demonstrateUsage());
    }
}