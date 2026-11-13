import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValidationFramework {
    // 基本的なバリデーション関数
    public static final Predicate<String> notEmpty = s -> s != null && !s.trim().isEmpty();
    public static final Predicate<String> validEmail = s -> s.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    public static final Function<Integer, Predicate<String>> minLength = min -> s -> s.length() >= min;
    public static final Function<Integer, Predicate<String>> maxLength = max -> s -> s.length() <= max;
    
    // 複合バリデーション
    public static final Predicate<User> isValidUser = user ->
        notEmpty.test(user.getName()) &&
        validEmail.test(user.getEmail()) &&
        minLength.apply(8).test(user.getPassword());
    
    // エラーメッセージ付きバリデーション
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = List.copyOf(errors);
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        
        @Override
        public String toString() {
            return valid ? "有効" : "無効: " + String.join(", ", errors);
        }
    }
    
    public static final Function<User, ValidationResult> validateUserWithErrors = user -> {
        List<String> errors = new ArrayList<>();
        
        if (!notEmpty.test(user.getName())) {
            errors.add("名前は必須です");
        }
        if (!validEmail.test(user.getEmail())) {
            errors.add("有効なメールアドレスを入力してください");
        }
        if (!minLength.apply(8).test(user.getPassword())) {
            errors.add("パスワードは8文字以上である必要があります");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    };
    
    public static void main(String[] args) {
        // テストユーザー
        User validUser = new User("太郎", "taro@example.com", "password123");
        User invalidUser = new User("", "invalid-email", "123");
        
        IO.println("有効なユーザーのバリデーション:");
        ValidationResult result1 = validateUserWithErrors.apply(validUser);
        IO.println(result1);
        
        IO.println("\n無効なユーザーのバリデーション:");
        ValidationResult result2 = validateUserWithErrors.apply(invalidUser);
        IO.println(result2);
    }
}
