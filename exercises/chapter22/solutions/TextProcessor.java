package chapter22.solutions;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Apache Commons Langを使用した文字列処理ユーティリティクラス。
 * <p>
 * StringUtilsクラスの機能を活用して、効率的な文字列処理を提供します。
 * </p>
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 * @since 2024-01-01
 */
public class TextProcessor {
    
    /**
     * 文字列が空（null、空文字、空白のみ）かチェックします。
     * 
     * @param text チェックする文字列
     * @return 空の場合true、それ以外はfalse
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * TextProcessor processor = new TextProcessor();
     * processor.isBlank(null);      // true
     * processor.isBlank("");        // true
     * processor.isBlank("   ");     // true
     * processor.isBlank("abc");     // false
     * processor.isBlank("  abc  "); // false
     * }</pre>
     */
    public boolean isBlank(String text) {
        return StringUtils.isBlank(text);
    }
    
    /**
     * 文字列を指定文字数で省略し、省略記号（...）を追加します。
     * 
     * @param text 省略する文字列
     * @param maxLength 最大文字数（省略記号を含む）
     * @return 省略された文字列
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * TextProcessor processor = new TextProcessor();
     * processor.abbreviate("Hello World", 10);  // "Hello W..."
     * processor.abbreviate("Hello", 10);        // "Hello"
     * processor.abbreviate(null, 10);           // null
     * }</pre>
     */
    public String abbreviate(String text, int maxLength) {
        return StringUtils.abbreviate(text, maxLength);
    }
    
    /**
     * 文字列の最初の文字を大文字に変換します。
     * 
     * @param text 変換する文字列
     * @return 最初の文字が大文字になった文字列
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * TextProcessor processor = new TextProcessor();
     * processor.capitalize("hello");     // "Hello"
     * processor.capitalize("HELLO");     // "HELLO"
     * processor.capitalize("hELLO");     // "HELLO"
     * processor.capitalize("");          // ""
     * processor.capitalize(null);        // null
     * }</pre>
     */
    public String capitalize(String text) {
        return StringUtils.capitalize(text);
    }
    
    /**
     * 配列を指定された区切り文字で結合します。
     * 
     * @param array 結合する文字列配列
     * @param separator 区切り文字
     * @return 結合された文字列
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * TextProcessor processor = new TextProcessor();
     * String[] array = {"apple", "banana", "orange"};
     * processor.joinArray(array, ", ");     // "apple, banana, orange"
     * processor.joinArray(array, "-");      // "apple-banana-orange"
     * processor.joinArray(null, ", ");      // null
     * }</pre>
     */
    public String joinArray(String[] array, String separator) {
        return StringUtils.join(array, separator);
    }
    
    /**
     * 文字列から数字のみを抽出します。
     * 
     * @param text 抽出元の文字列
     * @return 数字のみを含む文字列
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * TextProcessor processor = new TextProcessor();
     * processor.extractDigits("abc123def456");   // "123456"
     * processor.extractDigits("Hello World!");    // ""
     * processor.extractDigits("2024年1月1日");    // "202411"
     * processor.extractDigits(null);              // null
     * }</pre>
     */
    public String extractDigits(String text) {
        return StringUtils.getDigits(text);
    }
    
    /**
     * 追加機能: 文字列を指定した文字数で左側をパディングします。
     * 
     * @param text パディングする文字列
     * @param size 最終的な文字列の長さ
     * @param padChar パディング文字
     * @return パディングされた文字列
     */
    public String leftPad(String text, int size, char padChar) {
        return StringUtils.leftPad(text, size, padChar);
    }
    
    /**
     * 追加機能: 文字列を指定した文字数で右側をパディングします。
     * 
     * @param text パディングする文字列
     * @param size 最終的な文字列の長さ
     * @param padChar パディング文字
     * @return パディングされた文字列
     */
    public String rightPad(String text, int size, char padChar) {
        return StringUtils.rightPad(text, size, padChar);
    }
    
    /**
     * 追加機能: 文字列を逆順にします。
     * 
     * @param text 逆順にする文字列
     * @return 逆順になった文字列
     */
    public String reverse(String text) {
        return StringUtils.reverse(text);
    }
    
    /**
     * 追加機能: 文字列内の部分文字列の出現回数を数えます。
     * 
     * @param text 検索対象の文字列
     * @param searchStr 検索する部分文字列
     * @return 出現回数
     */
    public int countMatches(String text, String searchStr) {
        return StringUtils.countMatches(text, searchStr);
    }
    
    /**
     * 追加機能: 文字列が数値かどうかをチェックします。
     * 
     * @param text チェックする文字列
     * @return 数値の場合true、それ以外はfalse
     */
    public boolean isNumeric(String text) {
        return StringUtils.isNumeric(text);
    }
    
    /**
     * 追加機能: 文字列をスネークケースからキャメルケースに変換します。
     * 
     * @param text 変換する文字列（例: "hello_world"）
     * @return キャメルケース文字列（例: "helloWorld"）
     */
    public String snakeToCamel(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        
        String[] parts = text.toLowerCase().split("_");
        StringBuilder result = new StringBuilder(parts[0]);
        
        for (int i = 1; i < parts.length; i++) {
            result.append(StringUtils.capitalize(parts[i]));
        }
        
        return result.toString();
    }
    
    /**
     * 追加機能: 文字列をキャメルケースからスネークケースに変換します。
     * 
     * @param text 変換する文字列（例: "helloWorld"）
     * @return スネークケース文字列（例: "hello_world"）
     */
    public String camelToSnake(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        
        return text.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    /**
     * デモ用のメインメソッド
     */
    public static void main(String[] args) {
        TextProcessor processor = new TextProcessor();
        
        System.out.println("=== TextProcessor デモ ===\n");
        
        // isBlank のテスト
        System.out.println("--- isBlank ---");
        System.out.println("null: " + processor.isBlank(null));
        System.out.println("空文字列: " + processor.isBlank(""));
        System.out.println("空白のみ: " + processor.isBlank("   "));
        System.out.println("テキスト: " + processor.isBlank("Hello"));
        
        // abbreviate のテスト
        System.out.println("\n--- abbreviate ---");
        System.out.println("長い文字列: " + processor.abbreviate("Hello World, this is a long text", 20));
        System.out.println("短い文字列: " + processor.abbreviate("Short", 20));
        
        // capitalize のテスト
        System.out.println("\n--- capitalize ---");
        System.out.println("hello: " + processor.capitalize("hello"));
        System.out.println("HELLO: " + processor.capitalize("HELLO"));
        System.out.println("hELLO: " + processor.capitalize("hELLO"));
        
        // joinArray のテスト
        System.out.println("\n--- joinArray ---");
        String[] fruits = {"apple", "banana", "orange", "grape"};
        System.out.println("カンマ区切り: " + processor.joinArray(fruits, ", "));
        System.out.println("ハイフン区切り: " + processor.joinArray(fruits, "-"));
        
        // extractDigits のテスト
        System.out.println("\n--- extractDigits ---");
        System.out.println("abc123def456: " + processor.extractDigits("abc123def456"));
        System.out.println("電話番号03-1234-5678: " + processor.extractDigits("03-1234-5678"));
        System.out.println("2024年1月15日: " + processor.extractDigits("2024年1月15日"));
        
        // 追加機能のテスト
        System.out.println("\n--- 追加機能 ---");
        System.out.println("左パディング: " + processor.leftPad("123", 10, '0'));
        System.out.println("右パディング: " + processor.rightPad("Hello", 10, '*'));
        System.out.println("文字列逆順: " + processor.reverse("Hello World"));
        System.out.println("出現回数: " + processor.countMatches("Hello Hello World", "Hello"));
        System.out.println("数値チェック: " + processor.isNumeric("12345"));
        System.out.println("スネーク→キャメル: " + processor.snakeToCamel("hello_world_example"));
        System.out.println("キャメル→スネーク: " + processor.camelToSnake("helloWorldExample"));
    }
}