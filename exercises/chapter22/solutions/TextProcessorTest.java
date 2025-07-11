package chapter22.solutions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TextProcessorクラスのテストクラス。
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 * @since 2024-01-01
 */
@DisplayName("TextProcessorのテスト")
public class TextProcessorTest {
    
    private TextProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new TextProcessor();
    }
    
    @Test
    @DisplayName("isBlank: nullの場合trueを返す")
    void testIsBlankWithNull() {
        assertTrue(processor.isBlank(null));
    }
    
    @Test
    @DisplayName("isBlank: 空文字列の場合trueを返す")
    void testIsBlankWithEmptyString() {
        assertTrue(processor.isBlank(""));
    }
    
    @Test
    @DisplayName("isBlank: 空白のみの場合trueを返す")
    void testIsBlankWithWhitespace() {
        assertTrue(processor.isBlank("   "));
        assertTrue(processor.isBlank("\t"));
        assertTrue(processor.isBlank("\n"));
        assertTrue(processor.isBlank("  \t\n  "));
    }
    
    @Test
    @DisplayName("isBlank: 文字が含まれる場合falseを返す")
    void testIsBlankWithText() {
        assertFalse(processor.isBlank("Hello"));
        assertFalse(processor.isBlank("  Hello  "));
        assertFalse(processor.isBlank("a"));
    }
    
    @Test
    @DisplayName("abbreviate: 長い文字列を省略する")
    void testAbbreviateLongString() {
        assertEquals("Hello W...", processor.abbreviate("Hello World", 10));
        assertEquals("This is a ...", processor.abbreviate("This is a long text", 13));
    }
    
    @Test
    @DisplayName("abbreviate: 短い文字列はそのまま返す")
    void testAbbreviateShortString() {
        assertEquals("Hello", processor.abbreviate("Hello", 10));
        assertEquals("Hi", processor.abbreviate("Hi", 5));
    }
    
    @Test
    @DisplayName("abbreviate: nullの場合nullを返す")
    void testAbbreviateWithNull() {
        assertNull(processor.abbreviate(null, 10));
    }
    
    @Test
    @DisplayName("capitalize: 最初の文字を大文字にする")
    void testCapitalize() {
        assertEquals("Hello", processor.capitalize("hello"));
        assertEquals("World", processor.capitalize("world"));
        assertEquals("A", processor.capitalize("a"));
    }
    
    @Test
    @DisplayName("capitalize: すでに大文字の場合はそのまま")
    void testCapitalizeAlreadyCapitalized() {
        assertEquals("HELLO", processor.capitalize("HELLO"));
        assertEquals("Hello", processor.capitalize("Hello"));
    }
    
    @Test
    @DisplayName("capitalize: 空文字列とnullの処理")
    void testCapitalizeEdgeCases() {
        assertEquals("", processor.capitalize(""));
        assertNull(processor.capitalize(null));
    }
    
    @Test
    @DisplayName("joinArray: 配列を区切り文字で結合する")
    void testJoinArray() {
        String[] array = {"apple", "banana", "orange"};
        assertEquals("apple, banana, orange", processor.joinArray(array, ", "));
        assertEquals("apple-banana-orange", processor.joinArray(array, "-"));
        assertEquals("applebananaorange", processor.joinArray(array, ""));
    }
    
    @Test
    @DisplayName("joinArray: 空配列の場合")
    void testJoinArrayEmpty() {
        String[] emptyArray = {};
        assertEquals("", processor.joinArray(emptyArray, ", "));
    }
    
    @Test
    @DisplayName("joinArray: nullの場合")
    void testJoinArrayWithNull() {
        assertNull(processor.joinArray(null, ", "));
    }
    
    @Test
    @DisplayName("extractDigits: 数字のみを抽出する")
    void testExtractDigits() {
        assertEquals("123456", processor.extractDigits("abc123def456"));
        assertEquals("0312345678", processor.extractDigits("03-1234-5678"));
        assertEquals("20240115", processor.extractDigits("2024年01月15日"));
    }
    
    @Test
    @DisplayName("extractDigits: 数字がない場合")
    void testExtractDigitsNoDigits() {
        assertEquals("", processor.extractDigits("Hello World!"));
        assertEquals("", processor.extractDigits("あいうえお"));
    }
    
    @Test
    @DisplayName("extractDigits: nullの場合")
    void testExtractDigitsWithNull() {
        assertNull(processor.extractDigits(null));
    }
    
    @Test
    @DisplayName("leftPad: 左側をパディングする")
    void testLeftPad() {
        assertEquals("00000123", processor.leftPad("123", 8, '0'));
        assertEquals("***Hello", processor.leftPad("Hello", 8, '*'));
        assertEquals("   World", processor.leftPad("World", 8, ' '));
    }
    
    @Test
    @DisplayName("reverse: 文字列を逆順にする")
    void testReverse() {
        assertEquals("olleH", processor.reverse("Hello"));
        assertEquals("dlroW olleH", processor.reverse("Hello World"));
        assertEquals("12345", processor.reverse("54321"));
    }
    
    @Test
    @DisplayName("countMatches: 部分文字列の出現回数を数える")
    void testCountMatches() {
        assertEquals(2, processor.countMatches("Hello Hello World", "Hello"));
        assertEquals(3, processor.countMatches("aaabbbccc", "a"));
        assertEquals(0, processor.countMatches("Hello World", "xyz"));
    }
    
    @Test
    @DisplayName("isNumeric: 数値文字列の判定")
    void testIsNumeric() {
        assertTrue(processor.isNumeric("12345"));
        assertTrue(processor.isNumeric("0"));
        assertFalse(processor.isNumeric("123.45"));
        assertFalse(processor.isNumeric("abc123"));
        assertFalse(processor.isNumeric(""));
        assertFalse(processor.isNumeric(null));
    }
    
    @Test
    @DisplayName("snakeToCamel: スネークケースをキャメルケースに変換")
    void testSnakeToCamel() {
        assertEquals("helloWorld", processor.snakeToCamel("hello_world"));
        assertEquals("myVariableName", processor.snakeToCamel("my_variable_name"));
        assertEquals("test", processor.snakeToCamel("test"));
    }
    
    @Test
    @DisplayName("camelToSnake: キャメルケースをスネークケースに変換")
    void testCamelToSnake() {
        assertEquals("hello_world", processor.camelToSnake("helloWorld"));
        assertEquals("my_variable_name", processor.camelToSnake("myVariableName"));
        assertEquals("test", processor.camelToSnake("test"));
    }
}