import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShortCircuitExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 早期終了操作：条件を満たす要素が見つかれば処理を停止
        // すべての要素が偶数かチェック（1つでも奇数があればfalse）
        boolean allEven = numbers.stream()
            .peek(n -> IO.println("チェック中: " + n)) // 処理の流れを観察
            .allMatch(n -> n % 2 == 0);
        IO.println("すべて偶数: " + allEven);
        
        // いずれかの要素が条件を満たすかチェック
        boolean hasLargeNumber = numbers.stream()
            .anyMatch(n -> n > 7);
        IO.println("7より大きい数あり: " + hasLargeNumber);
        
        // 最初のn個に制限して処理
        List<Integer> firstThree = numbers.stream()
            .limit(3)  // 最初の3要素のみ処理
            .collect(Collectors.toList());
        IO.println("最初の3つ: " + firstThree);
    }
}
