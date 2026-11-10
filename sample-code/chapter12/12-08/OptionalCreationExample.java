import java.util.Optional;

public class OptionalCreationExample {
    public static void main(String[] args) {
        // 1. of() - null以外の値を持つOptionalを作成
        // nullを渡すとNullPointerExceptionが発生する
        Optional<String> presentValue = Optional.of("Hello");
        IO.println("of(): " + presentValue);  // Optional[Hello]
        
        // 2. ofNullable() - nullの可能性がある値をラップ
        // nullの場合は空のOptionalを返す
        String nullableString = null;
        Optional<String> nullableValue = Optional.ofNullable(nullableString);
        IO.println("ofNullable(null): " + nullableValue);  // Optional.empty
        
        String nonNullString = "World";
        Optional<String> nonNullValue = Optional.ofNullable(nonNullString);
        IO.println("ofNullable(\"World\"): " + nonNullValue);  // Optional[World]
        
        // 3. empty() - 空のOptionalを作成
        Optional<String> emptyValue = Optional.empty();
        IO.println("empty(): " + emptyValue);  // Optional.empty
    }
}
