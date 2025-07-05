import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 値オブジェクト（Value Object）の実装例
 * 
 * 【学習ポイント】
 * 1. 値オブジェクトの設計原則
 * 2. 不変性の実現
 * 3. 値の等価性（Value Equality）
 * 4. バリデーションの実装
 * 5. ドメインモデルでの活用
 * 
 * 【よくある間違い】
 * - 値オブジェクトをミュータブルにしてしまう
 * - equals/hashCodeの実装を忘れる
 * - バリデーションを実装しない
 * - プリミティブ型をそのまま使用してしまう
 */

/**
 * 金額を表す値オブジェクト
 */
final class Money {
    private final int amount;  // 円単位
    private final String currency;
    
    public Money(int amount, String currency) {
        if (amount < 0) {
            throw new IllegalArgumentException("金額は0以上でなければなりません: " + amount);
        }
        this.amount = amount;
        this.currency = Objects.requireNonNull(currency, "通貨はnullにできません");
    }
    
    // コンビニエンスコンストラクタ
    public Money(int amount) {
        this(amount, "JPY");
    }
    
    public int getAmount() {
        return amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    // 不変オブジェクトなので、演算結果は新しいインスタンスを返す
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("異なる通貨同士の計算はできません");
        }
        return new Money(this.amount + other.amount, this.currency);
    }
    
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("異なる通貨同士の計算はできません");
        }
        return new Money(this.amount - other.amount, this.currency);
    }
    
    public Money multiply(double multiplier) {
        return new Money((int) (this.amount * multiplier), this.currency);
    }
    
    public boolean isGreaterThan(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("異なる通貨同士の比較はできません");
        }
        return this.amount > other.amount;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Money money = (Money) obj;
        return amount == money.amount && Objects.equals(currency, money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
    
    @Override
    public String toString() {
        return amount + " " + currency;
    }
}

/**
 * メールアドレスを表す値オブジェクト
 */
final class Email {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private final String address;
    
    public Email(String address) {
        this.address = Objects.requireNonNull(address, "メールアドレスはnullにできません");
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("無効なメールアドレス形式です: " + address);
        }
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getLocalPart() {
        return address.substring(0, address.indexOf('@'));
    }
    
    public String getDomain() {
        return address.substring(address.indexOf('@') + 1);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Email email = (Email) obj;
        return Objects.equals(address, email.address);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
    
    @Override
    public String toString() {
        return address;
    }
}

/**
 * 電話番号を表す値オブジェクト
 */
final class PhoneNumber {
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\d{2,4}-\\d{2,4}-\\d{4}$");
    
    private final String number;
    
    public PhoneNumber(String number) {
        this.number = Objects.requireNonNull(number, "電話番号はnullにできません");
        if (!PHONE_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("無効な電話番号形式です: " + number);
        }
    }
    
    public String getNumber() {
        return number;
    }
    
    public String getNumberWithoutHyphens() {
        return number.replaceAll("-", "");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PhoneNumber that = (PhoneNumber) obj;
        return Objects.equals(number, that.number);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
    
    @Override
    public String toString() {
        return number;
    }
}

/**
 * 郵便番号を表す値オブジェクト
 */
final class PostalCode {
    private static final Pattern POSTAL_CODE_PATTERN = 
        Pattern.compile("^\\d{3}-\\d{4}$");
    
    private final String code;
    
    public PostalCode(String code) {
        this.code = Objects.requireNonNull(code, "郵便番号はnullにできません");
        if (!POSTAL_CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("無効な郵便番号形式です: " + code);
        }
    }
    
    public String getCode() {
        return code;
    }
    
    public String getCodeWithoutHyphen() {
        return code.replaceAll("-", "");
    }
    
    public String getFirstThreeDigits() {
        return code.substring(0, 3);
    }
    
    public String getLastFourDigits() {
        return code.substring(4);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PostalCode that = (PostalCode) obj;
        return Objects.equals(code, that.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return code;
    }
}

/**
 * 年齢を表す値オブジェクト
 */
final class Age {
    private final int value;
    
    public Age(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("年齢は0以上でなければなりません: " + value);
        }
        if (value > 150) {
            throw new IllegalArgumentException("年齢は150以下でなければなりません: " + value);
        }
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public boolean isMinor() {
        return value < 20;
    }
    
    public boolean isAdult() {
        return value >= 20;
    }
    
    public boolean isSenior() {
        return value >= 65;
    }
    
    public Age increment() {
        return new Age(value + 1);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Age age = (Age) obj;
        return value == age.value;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value + "歳";
    }
}

/**
 * 住所を表す値オブジェクト（複合値オブジェクト）
 */
final class Address {
    private final PostalCode postalCode;
    private final String prefecture;
    private final String city;
    private final String streetAddress;
    
    public Address(PostalCode postalCode, String prefecture, String city, String streetAddress) {
        this.postalCode = Objects.requireNonNull(postalCode, "郵便番号はnullにできません");
        this.prefecture = Objects.requireNonNull(prefecture, "都道府県はnullにできません");
        this.city = Objects.requireNonNull(city, "市区町村はnullにできません");
        this.streetAddress = Objects.requireNonNull(streetAddress, "番地はnullにできません");
    }
    
    public PostalCode getPostalCode() {
        return postalCode;
    }
    
    public String getPrefecture() {
        return prefecture;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getStreetAddress() {
        return streetAddress;
    }
    
    public String getFullAddress() {
        return String.format("〒%s %s%s%s", 
            postalCode.getCode(), prefecture, city, streetAddress);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Address address = (Address) obj;
        return Objects.equals(postalCode, address.postalCode) &&
               Objects.equals(prefecture, address.prefecture) &&
               Objects.equals(city, address.city) &&
               Objects.equals(streetAddress, address.streetAddress);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(postalCode, prefecture, city, streetAddress);
    }
    
    @Override
    public String toString() {
        return getFullAddress();
    }
}

/**
 * 値オブジェクトの使用例を示すクラス
 */
public final class ValueObject {
    
    /**
     * 顧客情報を表すクラス（値オブジェクトを使用）
     */
    public static final class Customer {
        private final String name;
        private final Age age;
        private final Email email;
        private final PhoneNumber phoneNumber;
        private final Address address;
        
        public Customer(String name, Age age, Email email, PhoneNumber phoneNumber, Address address) {
            this.name = Objects.requireNonNull(name, "名前はnullにできません");
            this.age = Objects.requireNonNull(age, "年齢はnullにできません");
            this.email = Objects.requireNonNull(email, "メールアドレスはnullにできません");
            this.phoneNumber = Objects.requireNonNull(phoneNumber, "電話番号はnullにできません");
            this.address = Objects.requireNonNull(address, "住所はnullにできません");
        }
        
        public String getName() { return name; }
        public Age getAge() { return age; }
        public Email getEmail() { return email; }
        public PhoneNumber getPhoneNumber() { return phoneNumber; }
        public Address getAddress() { return address; }
        
        public boolean canPurchaseAlcohol() {
            return age.isAdult();
        }
        
        public boolean isSeniorDiscount() {
            return age.isSenior();
        }
        
        @Override
        public String toString() {
            return String.format("Customer{name='%s', age=%s, email=%s, phone=%s, address=%s}", 
                name, age, email, phoneNumber, address);
        }
    }
    
    /**
     * 注文を表すクラス（値オブジェクトを使用）
     */
    public static final class Order {
        private final String orderId;
        private final Customer customer;
        private final Money totalAmount;
        private final Money taxAmount;
        
        public Order(String orderId, Customer customer, Money totalAmount) {
            this.orderId = Objects.requireNonNull(orderId, "注文IDはnullにできません");
            this.customer = Objects.requireNonNull(customer, "顧客情報はnullにできません");
            this.totalAmount = Objects.requireNonNull(totalAmount, "合計金額はnullにできません");
            
            // 税額計算（10%）
            this.taxAmount = totalAmount.multiply(0.1);
        }
        
        public String getOrderId() { return orderId; }
        public Customer getCustomer() { return customer; }
        public Money getTotalAmount() { return totalAmount; }
        public Money getTaxAmount() { return taxAmount; }
        
        public Money getSubtotalAmount() {
            return totalAmount.subtract(taxAmount);
        }
        
        public Money getFinalAmount() {
            Money finalAmount = totalAmount;
            
            // シニア割引適用
            if (customer.isSeniorDiscount()) {
                finalAmount = finalAmount.multiply(0.9);  // 10%割引
            }
            
            return finalAmount;
        }
        
        @Override
        public String toString() {
            return String.format("Order{orderId='%s', customer=%s, totalAmount=%s, taxAmount=%s}", 
                orderId, customer.getName(), totalAmount, taxAmount);
        }
    }
    
    /**
     * 使用例を示すメソッド
     */
    public static void demonstrateUsage() {
        System.out.println("=== 値オブジェクトの使用例 ===");
        
        // 値オブジェクトの作成
        Money price1 = new Money(1000);
        Money price2 = new Money(2000);
        
        System.out.println("価格1: " + price1);
        System.out.println("価格2: " + price2);
        System.out.println("合計: " + price1.add(price2));
        
        // 顧客情報の作成
        Age age = new Age(25);
        Email email = new Email("customer@example.com");
        PhoneNumber phone = new PhoneNumber("03-1234-5678");
        PostalCode postalCode = new PostalCode("123-4567");
        Address address = new Address(postalCode, "東京都", "渋谷区", "1-1-1");
        
        Customer customer = new Customer("田中太郎", age, email, phone, address);
        System.out.println("\n顧客情報: " + customer);
        
        // 注文の作成
        Order order = new Order("ORD-001", customer, new Money(5000));
        System.out.println("\n注文情報: " + order);
        System.out.println("小計: " + order.getSubtotalAmount());
        System.out.println("税額: " + order.getTaxAmount());
        System.out.println("最終金額: " + order.getFinalAmount());
        
        // 値オブジェクトの比較
        Money money1 = new Money(1000);
        Money money2 = new Money(1000);
        System.out.println("\n値オブジェクトの比較:");
        System.out.println("money1.equals(money2): " + money1.equals(money2));
        System.out.println("money1 == money2: " + (money1 == money2));
    }
    
    public static void main(String[] args) {
        demonstrateUsage();
    }
}