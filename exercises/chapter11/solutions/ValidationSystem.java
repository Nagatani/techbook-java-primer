package chapter11.solutions;

import java.util.function.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 関数型インターフェイスを活用したバリデーションシステム
 * 
 * 学習内容：
 * - Predicate<T>の使用方法
 * - バリデーションロジックの組み合わせ
 * - カスタムバリデーションルール
 * - 複合バリデーション
 */
public class ValidationSystem {
    
    /**
     * バリデーション結果を表すクラス
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        
        public static ValidationResult success() {
            return new ValidationResult(true, new ArrayList<>());
        }
        
        public static ValidationResult failure(String error) {
            List<String> errors = new ArrayList<>();
            errors.add(error);
            return new ValidationResult(false, errors);
        }
        
        public static ValidationResult failure(List<String> errors) {
            return new ValidationResult(false, errors);
        }
        
        @Override
        public String toString() {
            return valid ? "ValidationResult{valid=true}" : 
                   "ValidationResult{valid=false, errors=" + errors + "}";
        }
    }
    
    /**
     * 基本的な文字列バリデーション
     */
    public static class StringValidators {
        
        /**
         * 空文字でないことを確認
         */
        public static Predicate<String> notEmpty() {
            return s -> s != null && !s.trim().isEmpty();
        }
        
        /**
         * 最小長チェック
         */
        public static Predicate<String> minLength(int minLength) {
            return s -> s != null && s.length() >= minLength;
        }
        
        /**
         * 最大長チェック
         */
        public static Predicate<String> maxLength(int maxLength) {
            return s -> s != null && s.length() <= maxLength;
        }
        
        /**
         * 長さの範囲チェック
         */
        public static Predicate<String> lengthBetween(int min, int max) {
            return s -> s != null && s.length() >= min && s.length() <= max;
        }
        
        /**
         * 正規表現パターンマッチ
         */
        public static Predicate<String> matches(String pattern) {
            Pattern compiledPattern = Pattern.compile(pattern);
            return s -> s != null && compiledPattern.matcher(s).matches();
        }
        
        /**
         * 英数字のみチェック
         */
        public static Predicate<String> alphanumeric() {
            return matches("^[a-zA-Z0-9]+$");
        }
        
        /**
         * メールアドレス形式チェック
         */
        public static Predicate<String> email() {
            return matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        }
        
        /**
         * 電話番号形式チェック（日本の形式）
         */
        public static Predicate<String> phoneNumber() {
            return matches("^0\\d{2,3}-\\d{2,4}-\\d{4}$");
        }
        
        /**
         * 郵便番号形式チェック（日本の形式）
         */
        public static Predicate<String> zipCode() {
            return matches("^\\d{3}-\\d{4}$");
        }
        
        /**
         * 特定の文字列を含むかチェック
         */
        public static Predicate<String> contains(String substring) {
            return s -> s != null && s.contains(substring);
        }
        
        /**
         * 特定の文字列で開始するかチェック
         */
        public static Predicate<String> startsWith(String prefix) {
            return s -> s != null && s.startsWith(prefix);
        }
        
        /**
         * 特定の文字列で終了するかチェック
         */
        public static Predicate<String> endsWith(String suffix) {
            return s -> s != null && s.endsWith(suffix);
        }
    }
    
    /**
     * 数値バリデーション
     */
    public static class NumberValidators {
        
        /**
         * 正の数チェック
         */
        public static Predicate<Integer> positive() {
            return n -> n != null && n > 0;
        }
        
        /**
         * 負でない数チェック
         */
        public static Predicate<Integer> nonNegative() {
            return n -> n != null && n >= 0;
        }
        
        /**
         * 範囲チェック
         */
        public static Predicate<Integer> range(int min, int max) {
            return n -> n != null && n >= min && n <= max;
        }
        
        /**
         * 偶数チェック
         */
        public static Predicate<Integer> even() {
            return n -> n != null && n % 2 == 0;
        }
        
        /**
         * 奇数チェック
         */
        public static Predicate<Integer> odd() {
            return n -> n != null && n % 2 != 0;
        }
        
        /**
         * 倍数チェック
         */
        public static Predicate<Integer> multipleOf(int divisor) {
            return n -> n != null && n % divisor == 0;
        }
    }
    
    /**
     * コレクションバリデーション
     */
    public static class CollectionValidators {
        
        /**
         * 空でないことを確認
         */
        public static <T> Predicate<List<T>> notEmpty() {
            return list -> list != null && !list.isEmpty();
        }
        
        /**
         * サイズチェック
         */
        public static <T> Predicate<List<T>> size(int expectedSize) {
            return list -> list != null && list.size() == expectedSize;
        }
        
        /**
         * 最小サイズチェック
         */
        public static <T> Predicate<List<T>> minSize(int minSize) {
            return list -> list != null && list.size() >= minSize;
        }
        
        /**
         * 最大サイズチェック
         */
        public static <T> Predicate<List<T>> maxSize(int maxSize) {
            return list -> list != null && list.size() <= maxSize;
        }
        
        /**
         * すべての要素が条件を満たすかチェック
         */
        public static <T> Predicate<List<T>> allMatch(Predicate<T> condition) {
            return list -> list != null && list.stream().allMatch(condition);
        }
        
        /**
         * 少なくとも一つの要素が条件を満たすかチェック
         */
        public static <T> Predicate<List<T>> anyMatch(Predicate<T> condition) {
            return list -> list != null && list.stream().anyMatch(condition);
        }
        
        /**
         * 重複がないかチェック
         */
        public static <T> Predicate<List<T>> noDuplicates() {
            return list -> list != null && list.stream().distinct().count() == list.size();
        }
    }
    
    /**
     * カスタムバリデーションルール
     */
    public static class CustomValidators {
        
        /**
         * パスワード強度チェック
         */
        public static Predicate<String> strongPassword() {
            return password -> {
                if (password == null || password.length() < 8) {
                    return false;
                }
                
                boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
                boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
                boolean hasDigit = password.chars().anyMatch(Character::isDigit);
                boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*".indexOf(ch) >= 0);
                
                return hasUpper && hasLower && hasDigit && hasSpecial;
            };
        }
        
        /**
         * 年齢チェック
         */
        public static Predicate<Integer> validAge() {
            return age -> age != null && age >= 0 && age <= 150;
        }
        
        /**
         * 成人チェック
         */
        public static Predicate<Integer> adult() {
            return age -> age != null && age >= 18;
        }
        
        /**
         * 日付形式チェック（YYYY-MM-DD）
         */
        public static Predicate<String> dateFormat() {
            return StringValidators.matches("^\\d{4}-\\d{2}-\\d{2}$");
        }
        
        /**
         * 時刻形式チェック（HH:MM）
         */
        public static Predicate<String> timeFormat() {
            return StringValidators.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
        }
        
        /**
         * URLチェック
         */
        public static Predicate<String> url() {
            return StringValidators.matches("^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*$");
        }
    }
    
    /**
     * バリデーションチェーン
     */
    public static class ValidationChain<T> {
        private final List<ValidationRule<T>> rules = new ArrayList<>();
        
        public static <T> ValidationChain<T> of(Class<T> type) {
            return new ValidationChain<>();
        }
        
        public ValidationChain<T> rule(Predicate<T> condition, String errorMessage) {
            rules.add(new ValidationRule<>(condition, errorMessage));
            return this;
        }
        
        public ValidationResult validate(T value) {
            List<String> errors = new ArrayList<>();
            
            for (ValidationRule<T> rule : rules) {
                if (!rule.condition.test(value)) {
                    errors.add(rule.errorMessage);
                }
            }
            
            return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
        }
        
        private static class ValidationRule<T> {
            final Predicate<T> condition;
            final String errorMessage;
            
            ValidationRule(Predicate<T> condition, String errorMessage) {
                this.condition = condition;
                this.errorMessage = errorMessage;
            }
        }
    }
    
    /**
     * 複合バリデーション
     */
    public static class CompositeValidation {
        
        /**
         * 複数の条件をAND結合
         */
        @SafeVarargs
        public static <T> Predicate<T> allOf(Predicate<T>... predicates) {
            return value -> {
                for (Predicate<T> predicate : predicates) {
                    if (!predicate.test(value)) {
                        return false;
                    }
                }
                return true;
            };
        }
        
        /**
         * 複数の条件をOR結合
         */
        @SafeVarargs
        public static <T> Predicate<T> anyOf(Predicate<T>... predicates) {
            return value -> {
                for (Predicate<T> predicate : predicates) {
                    if (predicate.test(value)) {
                        return true;
                    }
                }
                return false;
            };
        }
        
        /**
         * 条件を否定
         */
        public static <T> Predicate<T> not(Predicate<T> predicate) {
            return predicate.negate();
        }
    }
    
    /**
     * ユーザー情報のバリデーション例
     */
    public static class UserValidator {
        
        public static ValidationResult validateUser(String name, String email, Integer age, String password) {
            List<String> errors = new ArrayList<>();
            
            // 名前のバリデーション
            ValidationChain<String> nameChain = ValidationChain.of(String.class)
                .rule(StringValidators.notEmpty(), "名前は必須です")
                .rule(StringValidators.lengthBetween(2, 50), "名前は2文字以上50文字以下で入力してください");
            
            ValidationResult nameResult = nameChain.validate(name);
            if (!nameResult.isValid()) {
                errors.addAll(nameResult.getErrors());
            }
            
            // メールアドレスのバリデーション
            ValidationChain<String> emailChain = ValidationChain.of(String.class)
                .rule(StringValidators.notEmpty(), "メールアドレスは必須です")
                .rule(StringValidators.email(), "有効なメールアドレスを入力してください");
            
            ValidationResult emailResult = emailChain.validate(email);
            if (!emailResult.isValid()) {
                errors.addAll(emailResult.getErrors());
            }
            
            // 年齢のバリデーション
            ValidationChain<Integer> ageChain = ValidationChain.of(Integer.class)
                .rule(CustomValidators.validAge(), "年齢は0歳以上150歳以下で入力してください")
                .rule(CustomValidators.adult(), "18歳以上である必要があります");
            
            ValidationResult ageResult = ageChain.validate(age);
            if (!ageResult.isValid()) {
                errors.addAll(ageResult.getErrors());
            }
            
            // パスワードのバリデーション
            ValidationChain<String> passwordChain = ValidationChain.of(String.class)
                .rule(StringValidators.notEmpty(), "パスワードは必須です")
                .rule(StringValidators.minLength(8), "パスワードは8文字以上で入力してください")
                .rule(CustomValidators.strongPassword(), "パスワードは大文字、小文字、数字、特殊文字をそれぞれ1文字以上含む必要があります");
            
            ValidationResult passwordResult = passwordChain.validate(password);
            if (!passwordResult.isValid()) {
                errors.addAll(passwordResult.getErrors());
            }
            
            return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
        }
    }
    
    /**
     * 条件付きバリデーション
     */
    public static class ConditionalValidator<T> {
        private final Predicate<T> condition;
        private final Predicate<T> validator;
        private final String errorMessage;
        
        public ConditionalValidator(Predicate<T> condition, Predicate<T> validator, String errorMessage) {
            this.condition = condition;
            this.validator = validator;
            this.errorMessage = errorMessage;
        }
        
        public ValidationResult validate(T value) {
            if (condition.test(value)) {
                return validator.test(value) ? ValidationResult.success() : ValidationResult.failure(errorMessage);
            }
            return ValidationResult.success();
        }
    }
}