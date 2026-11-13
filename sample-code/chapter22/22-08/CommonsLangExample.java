import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Date;

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
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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
        IO.println("Original: '" + text + "'");
        IO.println("Trimmed: '" + StringUtils.trim(text) + "'");
        IO.println("Is blank?: " + StringUtils.isBlank("   "));
        IO.println("Reverse: " + StringUtils.reverse("Hello"));
        IO.println("Capitalize: " + StringUtils.capitalize("java"));
        
        // nullや空文字列の安全な処理
        String nullStr = null;
        IO.println("Default: " + StringUtils.defaultIfEmpty(nullStr, "デフォルト値"));
        
        // 2. ArrayUtils - 配列操作
        int[] numbers = {1, 2, 3, 4, 5};
        IO.println("\nArray contains 3?: " + ArrayUtils.contains(numbers, 3));
        int[] reversed = ArrayUtils.clone(numbers);
        ArrayUtils.reverse(reversed);
        IO.println("Reversed: " + ArrayUtils.toString(reversed));
        
        // 3. RandomStringUtils - ランダム文字列生成
        IO.println("\nRandom alphabetic: " + 
            RandomStringUtils.secure().nextAlphabetic(10));
        IO.println("Random numeric: " + 
            RandomStringUtils.secure().nextNumeric(6));
        IO.println("Random alphanumeric: " + 
            RandomStringUtils.secure().nextAlphanumeric(8));
        
        // 4. DateFormatUtils - 日付フォーマット
        Date now = new Date();
        IO.println("\nDate formats:");
        IO.println("ISO: " + DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(now));
        IO.println("Custom: " + DateFormatUtils.format(now, "yyyy年MM月dd日 HH:mm:ss"));
        
        // 5. Builder utilities
        Person person = new Person("田中太郎", 30);
        IO.println("\nPerson: " + person);
    }
}
