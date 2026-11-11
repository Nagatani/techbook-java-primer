import java.util.ArrayList;
import java.util.List;

public class WildcardExample {
    // 上限境界ワイルドカード：Numberとそのサブクラスのリストを受け取り、合計値を計算する
    public static double sum(List<? extends Number> numberList) {
        double total = 0.0;
        for (Number num : numberList) {
            total += num.doubleValue();
        }
        return total;
    }

    // 下限境界ワイルドカード：Integerとそのスーパークラスのリストを受け取り、1からcountまでを追加する
    public static void addNumbers(List<? super Integer> list, int count) {
        for (int i = 1; i <= count; i++) {
            list.add(i);
        }
    }

    public static void main(String[] args) {
        List<Integer> intList = List.of(1, 2, 3);
        List<Double> doubleList = List.of(1.1, 2.2, 3.3);

        IO.println("Integerの合計: " + sum(intList));
        IO.println("Doubleの合計: " + sum(doubleList));

        List<Number> numberList = new ArrayList<>();
        addNumbers(numberList, 3);
        numberList.add(0.5);
        IO.println("Numberのリスト: " + numberList);
        IO.println("Numberの合計: " + sum(numberList));
    }
}
