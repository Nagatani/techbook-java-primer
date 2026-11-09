import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * ValidationHelperクラスのテストクラス
 */
public class ValidationHelperTest {
    
    @Test
    public void testValidateRequiredSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validateRequired("test", "テストフィールド");
        });
    }
    
    @Test
    public void testValidateRequiredFailure() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateRequired(null, "テストフィールド")
        );
        
        assertEquals("テストフィールド", exception.getField());
        assertTrue(exception.getMessage().contains("必須項目です"));
    }
    
    @Test
    public void testValidateRequiredEmptyString() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateRequired("", "テストフィールド")
        );
        
        assertEquals("テストフィールド", exception.getField());
        assertTrue(exception.getMessage().contains("必須項目です"));
    }
    
    @Test
    public void testValidateRequiredWhitespace() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateRequired("   ", "テストフィールド")
        );
        
        assertEquals("テストフィールド", exception.getField());
        assertTrue(exception.getMessage().contains("必須項目です"));
    }
    
    @Test
    public void testValidateLengthSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validateLength("test", "テストフィールド", 3, 10);
        });
    }
    
    @Test
    public void testValidateLengthTooShort() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateLength("ab", "テストフィールド", 3, 10)
        );
        
        assertTrue(exception.getMessage().contains("最小3文字以上"));
        assertTrue(exception.getMessage().contains("現在2文字"));
    }
    
    @Test
    public void testValidateLengthTooLong() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateLength("abcdefghijk", "テストフィールド", 3, 10)
        );
        
        assertTrue(exception.getMessage().contains("最大10文字以下"));
        assertTrue(exception.getMessage().contains("現在11文字"));
    }
    
    @Test
    public void testValidateRangeSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validateRange(50, "年齢", 0, 150);
        });
    }
    
    @Test
    public void testValidateRangeTooLow() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateRange(-1, "年齢", 0, 150)
        );
        
        assertTrue(exception.getMessage().contains("最小値0以上"));
        assertTrue(exception.getMessage().contains("現在-1"));
    }
    
    @Test
    public void testValidateRangeTooHigh() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateRange(200, "年齢", 0, 150)
        );
        
        assertTrue(exception.getMessage().contains("最大値150以下"));
        assertTrue(exception.getMessage().contains("現在200"));
    }
    
    @Test
    public void testValidateEmailSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validateEmail("test@example.com", "メールアドレス");
            ValidationHelper.validateEmail("user.name+tag@example.co.jp", "メールアドレス");
        });
    }
    
    @Test
    public void testValidateEmailFailure() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateEmail("invalid-email", "メールアドレス")
        );
        
        assertTrue(exception.getMessage().contains("有効なメールアドレス"));
    }
    
    @Test
    public void testValidatePhoneSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validatePhone("090-1234-5678", "電話番号");
        });
    }
    
    @Test
    public void testValidatePhoneFailure() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validatePhone("090-123-4567", "電話番号")
        );
        
        assertTrue(exception.getMessage().contains("XXX-XXXX-XXXX"));
    }
    
    @Test
    public void testValidatePasswordSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validatePassword("SecurePass123!", "パスワード");
        });
    }
    
    @Test
    public void testValidatePasswordFailure() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validatePassword("weak", "パスワード")
        );
        
        List<String> errors = exception.getErrors();
        assertTrue(errors.size() > 1);
        assertTrue(errors.stream().anyMatch(e -> e.contains("8文字以上")));
        assertTrue(errors.stream().anyMatch(e -> e.contains("大文字を含む")));
        assertTrue(errors.stream().anyMatch(e -> e.contains("数字を含む")));
        assertTrue(errors.stream().anyMatch(e -> e.contains("特殊文字")));
    }
    
    @Test
    public void testValidateCustomSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validateCustom("test@example.com", "メール", 
                email -> email.contains("@"), "メールアドレスに@を含む必要があります");
        });
    }
    
    @Test
    public void testValidateCustomFailure() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateCustom("invalid", "メール", 
                email -> email.contains("@"), "メールアドレスに@を含む必要があります")
        );
        
        assertTrue(exception.getMessage().contains("メールアドレスに@を含む必要があります"));
    }
    
    @Test
    public void testValidateAgeSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validateAge(25, "年齢");
            ValidationHelper.validateAge(0, "年齢");
            ValidationHelper.validateAge(150, "年齢");
        });
    }
    
    @Test
    public void testValidateAgeFailure() {
        ValidationHelper.ValidationException exception1 = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateAge(-1, "年齢")
        );
        assertTrue(exception1.getMessage().contains("0以上"));
        
        ValidationHelper.ValidationException exception2 = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validateAge(200, "年齢")
        );
        assertTrue(exception2.getMessage().contains("150歳以下"));
    }
    
    @Test
    public void testValidatePastDateSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validatePastDate("2020-01-01", "誕生日");
        });
    }
    
    @Test
    public void testValidatePastDateFailure() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validatePastDate("2030-12-31", "誕生日")
        );
        
        assertTrue(exception.getMessage().contains("過去の日付"));
    }
    
    @Test
    public void testValidatePastDateInvalidFormat() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.validatePastDate("invalid-date", "誕生日")
        );
        
        assertTrue(exception.getMessage().contains("YYYY-MM-DD"));
    }
    
    @Test
    public void testValidateUserInfoSuccess() {
        assertDoesNotThrow(() -> {
            ValidationHelper.validateUserInfo(
                "田中太郎",
                "tanaka@example.com",
                "090-1234-5678",
                30,
                "SecurePass123!"
            );
        });
    }
    
    @Test
    public void testValidateUserInfoMultipleErrors() {
        ValidationHelper.MultipleValidationException exception = assertThrows(
            ValidationHelper.MultipleValidationException.class,
            () -> ValidationHelper.validateUserInfo(
                "", // 名前が空
                "invalid-email", // 無効なメール
                "090-123-4567", // 無効な電話番号
                -1, // 無効な年齢
                "weak" // 弱いパスワード
            )
        );
        
        List<ValidationHelper.ValidationException> errors = exception.getValidationErrors();
        assertEquals(5, errors.size());
        
        assertTrue(exception.hasErrorForField("名前"));
        assertTrue(exception.hasErrorForField("メールアドレス"));
        assertTrue(exception.hasErrorForField("電話番号"));
        assertTrue(exception.hasErrorForField("年齢"));
        assertTrue(exception.hasErrorForField("パスワード"));
    }
    
    @Test
    public void testSafeParseIntSuccess() {
        assertDoesNotThrow(() -> {
            Integer result = ValidationHelper.safeParseInt("123", "数値");
            assertEquals(123, result);
        });
    }
    
    @Test
    public void testSafeParseIntNull() {
        assertDoesNotThrow(() -> {
            Integer result = ValidationHelper.safeParseInt(null, "数値");
            assertNull(result);
        });
    }
    
    @Test
    public void testSafeParseIntEmpty() {
        assertDoesNotThrow(() -> {
            Integer result = ValidationHelper.safeParseInt("", "数値");
            assertNull(result);
        });
    }
    
    @Test
    public void testSafeParseIntFailure() {
        ValidationHelper.ValidationException exception = assertThrows(
            ValidationHelper.ValidationException.class,
            () -> ValidationHelper.safeParseInt("abc", "数値")
        );
        
        assertTrue(exception.getMessage().contains("有効な数値"));
    }
    
    @Test
    public void testSafeToString() {
        assertEquals("test", ValidationHelper.safeToString("test"));
        assertEquals("123", ValidationHelper.safeToString(123));
        assertNull(ValidationHelper.safeToString(null));
    }
    
    @Test
    public void testValidationResultSuccess() {
        ValidationHelper.ValidationResult<String> result = 
            ValidationHelper.ValidationResult.success("test");
        
        assertTrue(result.isValid());
        assertEquals("test", result.getValue());
        assertTrue(result.getErrors().isEmpty());
    }
    
    @Test
    public void testValidationResultFailure() {
        ValidationHelper.ValidationResult<String> result = 
            ValidationHelper.ValidationResult.failure("エラーメッセージ");
        
        assertFalse(result.isValid());
        assertNull(result.getValue());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().contains("エラーメッセージ"));
    }
    
    @Test
    public void testValidateEmailSafelySuccess() {
        ValidationHelper.ValidationResult<String> result = 
            ValidationHelper.validateEmailSafely("test@example.com");
        
        assertTrue(result.isValid());
        assertEquals("test@example.com", result.getValue());
        assertTrue(result.getErrors().isEmpty());
    }
    
    @Test
    public void testValidateEmailSafelyFailure() {
        ValidationHelper.ValidationResult<String> result = 
            ValidationHelper.validateEmailSafely("invalid-email");
        
        assertFalse(result.isValid());
        assertNull(result.getValue());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().get(0).contains("有効なメールアドレス"));
    }
}