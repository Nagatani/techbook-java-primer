import java.util.regex.Pattern;
import java.util.function.Predicate;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * バリデーション例外処理クラス
 * 様々な検証ロジックと例外処理を提供
 */
public class ValidationHelper {
    
    /**
     * バリデーション例外
     */
    public static class ValidationException extends Exception {
        private final List<String> errors;
        private final String field;
        
        public ValidationException(String field, String message) {
            super(String.format("フィールド '%s' の検証に失敗しました: %s", field, message));
            this.field = field;
            this.errors = List.of(message);
        }
        
        public ValidationException(String field, List<String> errors) {
            super(String.format("フィールド '%s' の検証に失敗しました: %s", field, String.join(", ", errors)));
            this.field = field;
            this.errors = new ArrayList<>(errors);
        }
        
        public String getField() {
            return field;
        }
        
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
    }
    
    /**
     * 複数のバリデーション例外を集約
     */
    public static class MultipleValidationException extends Exception {
        private final List<ValidationException> validationErrors;
        
        public MultipleValidationException(List<ValidationException> validationErrors) {
            super(createMessage(validationErrors));
            this.validationErrors = new ArrayList<>(validationErrors);
        }
        
        private static String createMessage(List<ValidationException> errors) {
            StringBuilder sb = new StringBuilder("複数の検証エラーが発生しました:");
            for (ValidationException error : errors) {
                sb.append("\n- ").append(error.getMessage());
            }
            return sb.toString();
        }
        
        public List<ValidationException> getValidationErrors() {
            return new ArrayList<>(validationErrors);
        }
        
        public boolean hasErrorForField(String field) {
            return validationErrors.stream()
                    .anyMatch(error -> error.getField().equals(field));
        }
    }
    
    // 正規表現パターン
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\d{3}-\\d{4}-\\d{4}$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    /**
     * 文字列の必須検証
     * @param value 検証対象の値
     * @param fieldName フィールド名
     * @throws ValidationException 検証失敗時
     */
    public static void validateRequired(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, "必須項目です");
        }
    }
    
    /**
     * 文字列の長さ検証
     * @param value 検証対象の値
     * @param fieldName フィールド名
     * @param minLength 最小長
     * @param maxLength 最大長
     * @throws ValidationException 検証失敗時
     */
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) 
            throws ValidationException {
        if (value == null) {
            throw new ValidationException(fieldName, "nullは許可されていません");
        }
        
        if (value.length() < minLength) {
            throw new ValidationException(fieldName, 
                String.format("最小%d文字以上で入力してください（現在%d文字）", minLength, value.length()));
        }
        
        if (value.length() > maxLength) {
            throw new ValidationException(fieldName, 
                String.format("最大%d文字以下で入力してください（現在%d文字）", maxLength, value.length()));
        }
    }
    
    /**
     * 数値の範囲検証
     * @param value 検証対象の値
     * @param fieldName フィールド名
     * @param min 最小値
     * @param max 最大値
     * @throws ValidationException 検証失敗時
     */
    public static void validateRange(Number value, String fieldName, Number min, Number max) 
            throws ValidationException {
        if (value == null) {
            throw new ValidationException(fieldName, "nullは許可されていません");
        }
        
        double doubleValue = value.doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        
        if (doubleValue < minValue) {
            throw new ValidationException(fieldName, 
                String.format("最小値%s以上で入力してください（現在%s）", min, value));
        }
        
        if (doubleValue > maxValue) {
            throw new ValidationException(fieldName, 
                String.format("最大値%s以下で入力してください（現在%s）", max, value));
        }
    }
    
    /**
     * メールアドレスの検証
     * @param email メールアドレス
     * @param fieldName フィールド名
     * @throws ValidationException 検証失敗時
     */
    public static void validateEmail(String email, String fieldName) throws ValidationException {
        validateRequired(email, fieldName);
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(fieldName, "有効なメールアドレスを入力してください");
        }
    }
    
    /**
     * 電話番号の検証（XXX-XXXX-XXXX形式）
     * @param phone 電話番号
     * @param fieldName フィールド名
     * @throws ValidationException 検証失敗時
     */
    public static void validatePhone(String phone, String fieldName) throws ValidationException {
        validateRequired(phone, fieldName);
        
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(fieldName, "電話番号は XXX-XXXX-XXXX の形式で入力してください");
        }
    }
    
    /**
     * パスワードの検証
     * @param password パスワード
     * @param fieldName フィールド名
     * @throws ValidationException 検証失敗時
     */
    public static void validatePassword(String password, String fieldName) throws ValidationException {
        validateRequired(password, fieldName);
        
        List<String> errors = new ArrayList<>();
        
        if (password.length() < 8) {
            errors.add("8文字以上である必要があります");
        }
        
        if (!password.matches(".*[a-z].*")) {
            errors.add("小文字を含む必要があります");
        }
        
        if (!password.matches(".*[A-Z].*")) {
            errors.add("大文字を含む必要があります");
        }
        
        if (!password.matches(".*\\d.*")) {
            errors.add("数字を含む必要があります");
        }
        
        if (!password.matches(".*[@$!%*?&].*")) {
            errors.add("特殊文字(@$!%*?&)を含む必要があります");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(fieldName, errors);
        }
    }
    
    /**
     * カスタム検証ルール
     * @param value 検証対象の値
     * @param fieldName フィールド名
     * @param predicate 検証条件
     * @param errorMessage エラーメッセージ
     * @throws ValidationException 検証失敗時
     */
    public static <T> void validateCustom(T value, String fieldName, Predicate<T> predicate, String errorMessage) 
            throws ValidationException {
        if (!predicate.test(value)) {
            throw new ValidationException(fieldName, errorMessage);
        }
    }
    
    /**
     * 年齢の検証
     * @param age 年齢
     * @param fieldName フィールド名
     * @throws ValidationException 検証失敗時
     */
    public static void validateAge(Integer age, String fieldName) throws ValidationException {
        validateRequired(Objects.toString(age, null), fieldName);
        
        if (age < 0) {
            throw new ValidationException(fieldName, "年齢は0以上である必要があります");
        }
        
        if (age > 150) {
            throw new ValidationException(fieldName, "年齢は150歳以下である必要があります");
        }
    }
    
    /**
     * 日付の検証（過去日付チェック）
     * @param date 日付（YYYY-MM-DD形式）
     * @param fieldName フィールド名
     * @throws ValidationException 検証失敗時
     */
    public static void validatePastDate(String date, String fieldName) throws ValidationException {
        validateRequired(date, fieldName);
        
        try {
            java.time.LocalDate inputDate = java.time.LocalDate.parse(date);
            java.time.LocalDate today = java.time.LocalDate.now();
            
            if (inputDate.isAfter(today)) {
                throw new ValidationException(fieldName, "過去の日付を入力してください");
            }
        } catch (java.time.format.DateTimeParseException e) {
            throw new ValidationException(fieldName, "有効な日付を YYYY-MM-DD 形式で入力してください");
        }
    }
    
    /**
     * ユーザー情報の総合検証
     * @param name 名前
     * @param email メールアドレス
     * @param phone 電話番号
     * @param age 年齢
     * @param password パスワード
     * @throws MultipleValidationException 複数の検証エラー
     */
    public static void validateUserInfo(String name, String email, String phone, Integer age, String password) 
            throws MultipleValidationException {
        List<ValidationException> errors = new ArrayList<>();
        
        // 各フィールドの検証
        try {
            validateRequired(name, "名前");
            validateLength(name, "名前", 1, 50);
        } catch (ValidationException e) {
            errors.add(e);
        }
        
        try {
            validateEmail(email, "メールアドレス");
        } catch (ValidationException e) {
            errors.add(e);
        }
        
        try {
            validatePhone(phone, "電話番号");
        } catch (ValidationException e) {
            errors.add(e);
        }
        
        try {
            validateAge(age, "年齢");
        } catch (ValidationException e) {
            errors.add(e);
        }
        
        try {
            validatePassword(password, "パスワード");
        } catch (ValidationException e) {
            errors.add(e);
        }
        
        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }
    
    /**
     * 安全な文字列変換
     * @param value 変換対象の値
     * @return 文字列またはnull
     */
    public static String safeToString(Object value) {
        return value != null ? value.toString() : null;
    }
    
    /**
     * 安全な数値変換
     * @param value 変換対象の文字列
     * @param fieldName フィールド名
     * @return 変換された整数
     * @throws ValidationException 変換失敗時
     */
    public static Integer safeParseInt(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName, "有効な数値を入力してください");
        }
    }
    
    /**
     * 検証結果を含むラッパークラス
     */
    public static class ValidationResult<T> {
        private final T value;
        private final List<String> errors;
        private final boolean isValid;
        
        private ValidationResult(T value, List<String> errors) {
            this.value = value;
            this.errors = new ArrayList<>(errors);
            this.isValid = errors.isEmpty();
        }
        
        public static <T> ValidationResult<T> success(T value) {
            return new ValidationResult<>(value, List.of());
        }
        
        public static <T> ValidationResult<T> failure(List<String> errors) {
            return new ValidationResult<>(null, errors);
        }
        
        public static <T> ValidationResult<T> failure(String error) {
            return new ValidationResult<>(null, List.of(error));
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public T getValue() {
            return value;
        }
        
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
    }
    
    /**
     * 例外を発生させない検証（結果を返却）
     * @param email メールアドレス
     * @return 検証結果
     */
    public static ValidationResult<String> validateEmailSafely(String email) {
        try {
            validateEmail(email, "メールアドレス");
            return ValidationResult.success(email);
        } catch (ValidationException e) {
            return ValidationResult.failure(e.getErrors());
        }
    }
}