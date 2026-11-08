import java.util.Optional;

public class OptionalPresenceExample {
    public static void main(String[] args) {
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        
        // 1. isPresent() - 値が存在するかをboolean値で返す
        if (present.isPresent()) {
            System.out.println("isPresent: " + present.get());
        }
        
        // 2. isEmpty() - 値が存在しないかをboolean値で返す（Java 11以降）
        if (empty.isEmpty()) {
            System.out.println("isEmpty: 値が存在しません");
        }
        
        // 3. ifPresent() - 値が存在する場合のみ処理を実行（関数型スタイル）
        present.ifPresent(value -> 
            System.out.println("ifPresent: " + value));
        
        empty.ifPresent(value -> 
            System.out.println("この行は実行されません"));
        
        // 4. ifPresentOrElse() - 値の有無で処理を分岐（Java 9以降）
        present.ifPresentOrElse(
            value -> System.out.println("値あり: " + value),
            () -> System.out.println("値なし")
        );
        
        empty.ifPresentOrElse(
            value -> System.out.println("この行は実行されません"),
            () -> System.out.println("空のOptional")
        );
    }
}
