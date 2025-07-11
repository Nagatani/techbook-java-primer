/**
 * リスト22-5
 * CommonsLangExampleクラス
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (500行目)
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.time.LocalDateTime;

public class CommonsLangExample {
    
    static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
        
        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }
        
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }
    
    public static void main(String[] args) {
        // 1. StringUtils - 文字列操作
        String text = "  Hello World  ";
        System.out.println("Original: '" + text + "'");
        System.out.println("Trimmed: '" + StringUtils.trim(text) + "'");
        System.out.println("Is blank?: " + StringUtils.isBlank("   "));
        System.out.println("Reverse: " + StringUtils.reverse("Hello"));
        System.out.println("Capitalize: " + StringUtils.capitalize("java"));
        
        // 空文字列の安全な処理
        String nullStr = null;
        System.out.println("Default: " + StringUtils.defaultString(nullStr, "デフォルト値"));
        
        // 2. ArrayUtils - 配列操作
        int[] numbers = {1, 2, 3, 4, 5};
        System.out.println("\nArray contains 3?: " + ArrayUtils.contains(numbers, 3));
        int[] reversed = ArrayUtils.clone(numbers);
        ArrayUtils.reverse(reversed);
        System.out.println("Reversed: " + ArrayUtils.toString(reversed));
        
        // 3. RandomStringUtils - ランダム文字列生成
        System.out.println("\nRandom alphabetic: " + 
            RandomStringUtils.randomAlphabetic(10));
        System.out.println("Random numeric: " + 
            RandomStringUtils.randomNumeric(6));
        System.out.println("Random alphanumeric: " + 
            RandomStringUtils.randomAlphanumeric(8));
        
        // 4. DateFormatUtils - 日付フォーマット
        Date now = new Date();
        System.out.println("\nDate formats:");
        System.out.println("ISO: " + DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(now));
        System.out.println("Custom: " + DateFormatUtils.format(now, "yyyy年MM月dd日 HH:mm:ss"));
        
        // 5. Builder utilities
        Person person = new Person("田中太郎", 30);
        System.out.println("\nPerson: " + person);
    }
}