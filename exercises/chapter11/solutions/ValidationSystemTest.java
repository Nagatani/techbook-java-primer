package chapter11.solutions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * ValidationSystemクラスのテストクラス
 */
public class ValidationSystemTest {
    
    @Test
    void testValidationResult() {
        ValidationSystem.ValidationResult success = ValidationSystem.ValidationResult.success();
        assertTrue(success.isValid());
        assertTrue(success.getErrors().isEmpty());
        
        ValidationSystem.ValidationResult failure = ValidationSystem.ValidationResult.failure("Error message");
        assertFalse(failure.isValid());
        assertEquals(1, failure.getErrors().size());
        assertEquals("Error message", failure.getErrors().get(0));
    }
    
    @Test
    void testStringValidators() {
        // notEmpty
        assertTrue(ValidationSystem.StringValidators.notEmpty().test("hello"));
        assertFalse(ValidationSystem.StringValidators.notEmpty().test(""));
        assertFalse(ValidationSystem.StringValidators.notEmpty().test("   "));
        assertFalse(ValidationSystem.StringValidators.notEmpty().test(null));
        
        // minLength
        assertTrue(ValidationSystem.StringValidators.minLength(5).test("hello"));
        assertTrue(ValidationSystem.StringValidators.minLength(5).test("hello world"));
        assertFalse(ValidationSystem.StringValidators.minLength(5).test("hi"));
        
        // maxLength
        assertTrue(ValidationSystem.StringValidators.maxLength(10).test("hello"));
        assertFalse(ValidationSystem.StringValidators.maxLength(10).test("hello world"));
        
        // lengthBetween
        assertTrue(ValidationSystem.StringValidators.lengthBetween(3, 7).test("hello"));
        assertFalse(ValidationSystem.StringValidators.lengthBetween(3, 7).test("hi"));
        assertFalse(ValidationSystem.StringValidators.lengthBetween(3, 7).test("hello world"));
        
        // alphanumeric
        assertTrue(ValidationSystem.StringValidators.alphanumeric().test("hello123"));
        assertFalse(ValidationSystem.StringValidators.alphanumeric().test("hello-123"));
        
        // email
        assertTrue(ValidationSystem.StringValidators.email().test("test@example.com"));
        assertFalse(ValidationSystem.StringValidators.email().test("invalid-email"));
        
        // phoneNumber
        assertTrue(ValidationSystem.StringValidators.phoneNumber().test("090-1234-5678"));
        assertFalse(ValidationSystem.StringValidators.phoneNumber().test("09012345678"));
        
        // zipCode
        assertTrue(ValidationSystem.StringValidators.zipCode().test("123-4567"));
        assertFalse(ValidationSystem.StringValidators.zipCode().test("1234567"));
        
        // contains
        assertTrue(ValidationSystem.StringValidators.contains("test").test("This is a test"));
        assertFalse(ValidationSystem.StringValidators.contains("test").test("This is a demo"));
        
        // startsWith
        assertTrue(ValidationSystem.StringValidators.startsWith("Hello").test("Hello World"));
        assertFalse(ValidationSystem.StringValidators.startsWith("Hello").test("Hi World"));
        
        // endsWith
        assertTrue(ValidationSystem.StringValidators.endsWith("World").test("Hello World"));
        assertFalse(ValidationSystem.StringValidators.endsWith("World").test("Hello Universe"));
    }
    
    @Test
    void testNumberValidators() {
        // positive
        assertTrue(ValidationSystem.NumberValidators.positive().test(5));
        assertFalse(ValidationSystem.NumberValidators.positive().test(0));
        assertFalse(ValidationSystem.NumberValidators.positive().test(-5));
        
        // nonNegative
        assertTrue(ValidationSystem.NumberValidators.nonNegative().test(5));
        assertTrue(ValidationSystem.NumberValidators.nonNegative().test(0));
        assertFalse(ValidationSystem.NumberValidators.nonNegative().test(-5));
        
        // range
        assertTrue(ValidationSystem.NumberValidators.range(1, 10).test(5));
        assertTrue(ValidationSystem.NumberValidators.range(1, 10).test(1));
        assertTrue(ValidationSystem.NumberValidators.range(1, 10).test(10));
        assertFalse(ValidationSystem.NumberValidators.range(1, 10).test(0));
        assertFalse(ValidationSystem.NumberValidators.range(1, 10).test(11));
        
        // even
        assertTrue(ValidationSystem.NumberValidators.even().test(4));
        assertFalse(ValidationSystem.NumberValidators.even().test(5));
        
        // odd
        assertTrue(ValidationSystem.NumberValidators.odd().test(5));
        assertFalse(ValidationSystem.NumberValidators.odd().test(4));
        
        // multipleOf
        assertTrue(ValidationSystem.NumberValidators.multipleOf(3).test(9));
        assertFalse(ValidationSystem.NumberValidators.multipleOf(3).test(10));
    }
    
    @Test
    void testCollectionValidators() {
        List<String> emptyList = Arrays.asList();
        List<String> singletonList = Arrays.asList("item");
        List<String> multipleList = Arrays.asList("item1", "item2", "item3");
        List<String> duplicateList = Arrays.asList("item1", "item2", "item1");
        
        // notEmpty
        assertFalse(ValidationSystem.CollectionValidators.notEmpty().test(emptyList));
        assertTrue(ValidationSystem.CollectionValidators.notEmpty().test(singletonList));
        
        // size
        assertTrue(ValidationSystem.CollectionValidators.size(1).test(singletonList));
        assertFalse(ValidationSystem.CollectionValidators.size(1).test(multipleList));
        
        // minSize
        assertTrue(ValidationSystem.CollectionValidators.minSize(2).test(multipleList));
        assertFalse(ValidationSystem.CollectionValidators.minSize(2).test(singletonList));
        
        // maxSize
        assertTrue(ValidationSystem.CollectionValidators.maxSize(5).test(multipleList));
        assertFalse(ValidationSystem.CollectionValidators.maxSize(2).test(multipleList));
        
        // allMatch
        List<Integer> positiveNumbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> mixedNumbers = Arrays.asList(1, -2, 3, 4, 5);
        assertTrue(ValidationSystem.CollectionValidators.allMatch(n -> n > 0).test(positiveNumbers));
        assertFalse(ValidationSystem.CollectionValidators.allMatch(n -> n > 0).test(mixedNumbers));
        
        // anyMatch
        assertTrue(ValidationSystem.CollectionValidators.anyMatch(n -> n > 3).test(positiveNumbers));
        assertFalse(ValidationSystem.CollectionValidators.anyMatch(n -> n > 10).test(positiveNumbers));
        
        // noDuplicates
        assertTrue(ValidationSystem.CollectionValidators.noDuplicates().test(multipleList));
        assertFalse(ValidationSystem.CollectionValidators.noDuplicates().test(duplicateList));
    }
    
    @Test
    void testCustomValidators() {
        // strongPassword
        assertTrue(ValidationSystem.CustomValidators.strongPassword().test("Password123!"));
        assertFalse(ValidationSystem.CustomValidators.strongPassword().test("password"));
        assertFalse(ValidationSystem.CustomValidators.strongPassword().test("PASSWORD"));
        assertFalse(ValidationSystem.CustomValidators.strongPassword().test("Password"));
        assertFalse(ValidationSystem.CustomValidators.strongPassword().test("Pass123!"));
        
        // validAge
        assertTrue(ValidationSystem.CustomValidators.validAge().test(25));
        assertTrue(ValidationSystem.CustomValidators.validAge().test(0));
        assertTrue(ValidationSystem.CustomValidators.validAge().test(150));
        assertFalse(ValidationSystem.CustomValidators.validAge().test(-1));
        assertFalse(ValidationSystem.CustomValidators.validAge().test(151));
        
        // adult
        assertTrue(ValidationSystem.CustomValidators.adult().test(18));
        assertTrue(ValidationSystem.CustomValidators.adult().test(25));
        assertFalse(ValidationSystem.CustomValidators.adult().test(17));
        
        // dateFormat
        assertTrue(ValidationSystem.CustomValidators.dateFormat().test("2023-12-25"));
        assertFalse(ValidationSystem.CustomValidators.dateFormat().test("2023/12/25"));
        assertFalse(ValidationSystem.CustomValidators.dateFormat().test("25-12-2023"));
        
        // timeFormat
        assertTrue(ValidationSystem.CustomValidators.timeFormat().test("14:30"));
        assertTrue(ValidationSystem.CustomValidators.timeFormat().test("09:05"));
        assertFalse(ValidationSystem.CustomValidators.timeFormat().test("25:30"));
        assertFalse(ValidationSystem.CustomValidators.timeFormat().test("14:70"));
        
        // url
        assertTrue(ValidationSystem.CustomValidators.url().test("https://example.com"));
        assertTrue(ValidationSystem.CustomValidators.url().test("http://example.com/path"));
        assertFalse(ValidationSystem.CustomValidators.url().test("ftp://example.com"));
        assertFalse(ValidationSystem.CustomValidators.url().test("example.com"));
    }
    
    @Test
    void testValidationChain() {
        ValidationSystem.ValidationChain<String> chain = ValidationSystem.ValidationChain.of(String.class)
            .rule(ValidationSystem.StringValidators.notEmpty(), "値は必須です")
            .rule(ValidationSystem.StringValidators.minLength(5), "5文字以上で入力してください")
            .rule(ValidationSystem.StringValidators.maxLength(10), "10文字以下で入力してください");
        
        // 成功ケース
        ValidationSystem.ValidationResult result = chain.validate("hello");
        assertTrue(result.isValid());
        
        // 失敗ケース
        result = chain.validate("");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("値は必須です"));
        
        result = chain.validate("hi");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("5文字以上で入力してください"));
        
        result = chain.validate("hello world test");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("10文字以下で入力してください"));
    }
    
    @Test
    void testCompositeValidation() {
        Predicate<String> notEmpty = ValidationSystem.StringValidators.notEmpty();
        Predicate<String> minLength = ValidationSystem.StringValidators.minLength(5);
        Predicate<String> maxLength = ValidationSystem.StringValidators.maxLength(10);
        
        // allOf
        Predicate<String> allConditions = ValidationSystem.CompositeValidation.allOf(notEmpty, minLength, maxLength);
        assertTrue(allConditions.test("hello"));
        assertFalse(allConditions.test(""));
        assertFalse(allConditions.test("hi"));
        assertFalse(allConditions.test("hello world test"));
        
        // anyOf
        Predicate<String> anyCondition = ValidationSystem.CompositeValidation.anyOf(
            s -> s.contains("test"),
            s -> s.contains("hello")
        );
        assertTrue(anyCondition.test("hello world"));
        assertTrue(anyCondition.test("test case"));
        assertFalse(anyCondition.test("example"));
        
        // not
        Predicate<String> notEmpty2 = ValidationSystem.CompositeValidation.not(s -> s.isEmpty());
        assertTrue(notEmpty2.test("hello"));
        assertFalse(notEmpty2.test(""));
    }
    
    @Test
    void testUserValidator() {
        // 正常なユーザー
        ValidationSystem.ValidationResult result = ValidationSystem.UserValidator.validateUser(
            "John Doe", "john@example.com", 25, "Password123!");
        assertTrue(result.isValid());
        
        // 名前が空
        result = ValidationSystem.UserValidator.validateUser(
            "", "john@example.com", 25, "Password123!");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("名前は必須です"));
        
        // 無効なメールアドレス
        result = ValidationSystem.UserValidator.validateUser(
            "John Doe", "invalid-email", 25, "Password123!");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("有効なメールアドレスを入力してください"));
        
        // 未成年
        result = ValidationSystem.UserValidator.validateUser(
            "John Doe", "john@example.com", 17, "Password123!");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("18歳以上である必要があります"));
        
        // 弱いパスワード
        result = ValidationSystem.UserValidator.validateUser(
            "John Doe", "john@example.com", 25, "password");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(error -> 
            error.contains("パスワードは大文字、小文字、数字、特殊文字をそれぞれ1文字以上含む必要があります")));
    }
    
    @Test
    void testConditionalValidator() {
        // 年齢が18歳以上の場合のみ、パスワードが強力である必要がある
        ValidationSystem.ConditionalValidator<Integer> validator = 
            new ValidationSystem.ConditionalValidator<>(
                age -> age >= 18,
                age -> age >= 21,
                "成人の場合は21歳以上である必要があります"
            );
        
        // 条件を満たす場合
        ValidationSystem.ValidationResult result = validator.validate(25);
        assertTrue(result.isValid());
        
        // 条件を満たさない場合
        result = validator.validate(19);
        assertFalse(result.isValid());
        assertEquals("成人の場合は21歳以上である必要があります", result.getErrors().get(0));
        
        // 条件に該当しない場合
        result = validator.validate(16);
        assertTrue(result.isValid());
    }
}