import java.util.Optional;
import java.util.NoSuchElementException;

public class OptionalRetrievalExample {
    public static void main(String[] args) {
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        
        // 1. get() - 値が存在しない場合はNoSuchElementExceptionを投げる（非推奨）
        try {
            String value1 = present.get();
            IO.println("get() with value: " + value1);  // Hello
            
            String value2 = empty.get();  // 例外発生！
        } catch (NoSuchElementException e) {
            IO.println("get() on empty: " + e.getMessage());
        }
        
        // 2. orElse() - 値が存在しない場合のデフォルト値を指定
        String value3 = present.orElse("Default");
        String value4 = empty.orElse("Default");
        IO.println("orElse() with value: " + value3);     // Hello
        IO.println("orElse() on empty: " + value4);       // Default
        
        // 3. orElseGet() - 値が存在しない場合にSupplierから値を生成
        // orElseよりコストが低い（デフォルト値の生成コストが高い場合）
        String value5 = empty.orElseGet(() -> {
            IO.println("Generating default value...");
            return "Generated Default";
        });
        IO.println("orElseGet(): " + value5);  // Generated Default
        
        // 4. orElseThrow() - 値が存在しない場合に指定した例外を投げる
        try {
            String value6 = empty.orElseThrow(() -> 
                new IllegalStateException("値が存在しません"));
        } catch (IllegalStateException e) {
            IO.println("orElseThrow(): " + e.getMessage());
        }
    }
}
