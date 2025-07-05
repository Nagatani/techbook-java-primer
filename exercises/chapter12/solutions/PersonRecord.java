package chapter12.solutions;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

/**
 * Recordを使用したPersonクラスの実装
 * 
 * 学習内容：
 * - Recordの基本的な使用方法
 * - 自動生成されるメソッド（equals, hashCode, toString）
 * - Recordでのバリデーション
 * - カスタムメソッドの追加
 */
public record PersonRecord(
    String firstName,
    String lastName,
    LocalDate birthDate,
    String email,
    List<String> phoneNumbers
) {
    
    /**
     * コンパクトコンストラクタ - バリデーションを行う
     */
    public PersonRecord {
        // nullチェック
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");
        Objects.requireNonNull(birthDate, "Birth date cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(phoneNumbers, "Phone numbers cannot be null");
        
        // 値の検証
        if (firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // 正規化処理
        firstName = firstName.trim();
        lastName = lastName.trim();
        email = email.trim().toLowerCase();
        
        // 不変性を保つためにコピーを作成
        phoneNumbers = List.copyOf(phoneNumbers);
    }
    
    /**
     * 便利なコンストラクタ - 電話番号なしの場合
     */
    public PersonRecord(String firstName, String lastName, LocalDate birthDate, String email) {
        this(firstName, lastName, birthDate, email, List.of());
    }
    
    /**
     * 便利なコンストラクタ - 文字列の生年月日を受け取る場合
     */
    public PersonRecord(String firstName, String lastName, String birthDate, String email) {
        this(firstName, lastName, LocalDate.parse(birthDate), email);
    }
    
    /**
     * フルネームを取得
     */
    public String fullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * 年齢を計算
     */
    public int age() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    /**
     * 成人かどうかを判定
     */
    public boolean isAdult() {
        return age() >= 18;
    }
    
    /**
     * 指定された年齢かどうかを判定
     */
    public boolean isAge(int targetAge) {
        return age() == targetAge;
    }
    
    /**
     * 年齢範囲内かどうかを判定
     */
    public boolean isAgeBetween(int minAge, int maxAge) {
        int currentAge = age();
        return currentAge >= minAge && currentAge <= maxAge;
    }
    
    /**
     * 電話番号を持っているかどうかを判定
     */
    public boolean hasPhoneNumber() {
        return !phoneNumbers.isEmpty();
    }
    
    /**
     * 電話番号の数を取得
     */
    public int phoneNumberCount() {
        return phoneNumbers.size();
    }
    
    /**
     * 主要な電話番号を取得（最初の電話番号）
     */
    public String primaryPhoneNumber() {
        return phoneNumbers.isEmpty() ? null : phoneNumbers.get(0);
    }
    
    /**
     * 電話番号を追加した新しいPersonRecordを作成
     */
    public PersonRecord addPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        
        List<String> newPhoneNumbers = new java.util.ArrayList<>(phoneNumbers);
        newPhoneNumbers.add(phoneNumber.trim());
        
        return new PersonRecord(firstName, lastName, birthDate, email, newPhoneNumbers);
    }
    
    /**
     * 電話番号を削除した新しいPersonRecordを作成
     */
    public PersonRecord removePhoneNumber(String phoneNumber) {
        List<String> newPhoneNumbers = phoneNumbers.stream()
            .filter(phone -> !phone.equals(phoneNumber))
            .toList();
        
        return new PersonRecord(firstName, lastName, birthDate, email, newPhoneNumbers);
    }
    
    /**
     * 名前を変更した新しいPersonRecordを作成
     */
    public PersonRecord withName(String newFirstName, String newLastName) {
        return new PersonRecord(newFirstName, newLastName, birthDate, email, phoneNumbers);
    }
    
    /**
     * ファーストネームを変更した新しいPersonRecordを作成
     */
    public PersonRecord withFirstName(String newFirstName) {
        return new PersonRecord(newFirstName, lastName, birthDate, email, phoneNumbers);
    }
    
    /**
     * ラストネームを変更した新しいPersonRecordを作成
     */
    public PersonRecord withLastName(String newLastName) {
        return new PersonRecord(firstName, newLastName, birthDate, email, phoneNumbers);
    }
    
    /**
     * メールアドレスを変更した新しいPersonRecordを作成
     */
    public PersonRecord withEmail(String newEmail) {
        return new PersonRecord(firstName, lastName, birthDate, newEmail, phoneNumbers);
    }
    
    /**
     * 生年月日を変更した新しいPersonRecordを作成
     */
    public PersonRecord withBirthDate(LocalDate newBirthDate) {
        return new PersonRecord(firstName, lastName, newBirthDate, email, phoneNumbers);
    }
    
    /**
     * 姓のイニシャルを取得
     */
    public String firstNameInitial() {
        return firstName.substring(0, 1).toUpperCase();
    }
    
    /**
     * 名のイニシャルを取得
     */
    public String lastNameInitial() {
        return lastName.substring(0, 1).toUpperCase();
    }
    
    /**
     * フルネームのイニシャルを取得
     */
    public String initials() {
        return firstNameInitial() + lastNameInitial();
    }
    
    /**
     * 生年月日の月を取得
     */
    public String birthMonth() {
        return birthDate.getMonth().name();
    }
    
    /**
     * 生年月日の年を取得
     */
    public int birthYear() {
        return birthDate.getYear();
    }
    
    /**
     * 誕生日まで何日かを計算
     */
    public int daysUntilBirthday() {
        LocalDate today = LocalDate.now();
        LocalDate nextBirthday = birthDate.withYear(today.getYear());
        
        if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        
        return Period.between(today, nextBirthday).getDays();
    }
    
    /**
     * 今日が誕生日かどうかを判定
     */
    public boolean isBirthdayToday() {
        LocalDate today = LocalDate.now();
        return birthDate.getMonth() == today.getMonth() && 
               birthDate.getDayOfMonth() == today.getDayOfMonth();
    }
    
    /**
     * メールアドレスの形式検証
     */
    private static boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
    
    /**
     * 比較用の年齢順序
     */
    public static java.util.Comparator<PersonRecord> ageComparator() {
        return java.util.Comparator.comparing(PersonRecord::age);
    }
    
    /**
     * 比較用の名前順序
     */
    public static java.util.Comparator<PersonRecord> nameComparator() {
        return java.util.Comparator.comparing(PersonRecord::lastName)
            .thenComparing(PersonRecord::firstName);
    }
    
    /**
     * 比較用の生年月日順序
     */
    public static java.util.Comparator<PersonRecord> birthDateComparator() {
        return java.util.Comparator.comparing(PersonRecord::birthDate);
    }
    
    /**
     * PersonRecordのビルダーパターン実装
     */
    public static class Builder {
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private String email;
        private List<String> phoneNumbers = new java.util.ArrayList<>();
        
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        
        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }
        
        public Builder birthDate(String birthDate) {
            this.birthDate = LocalDate.parse(birthDate);
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder addPhoneNumber(String phoneNumber) {
            this.phoneNumbers.add(phoneNumber);
            return this;
        }
        
        public Builder phoneNumbers(List<String> phoneNumbers) {
            this.phoneNumbers = new java.util.ArrayList<>(phoneNumbers);
            return this;
        }
        
        public PersonRecord build() {
            return new PersonRecord(firstName, lastName, birthDate, email, phoneNumbers);
        }
    }
    
    /**
     * ビルダーを作成
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 既存のPersonRecordからビルダーを作成
     */
    public Builder toBuilder() {
        return new Builder()
            .firstName(firstName)
            .lastName(lastName)
            .birthDate(birthDate)
            .email(email)
            .phoneNumbers(phoneNumbers);
    }
}