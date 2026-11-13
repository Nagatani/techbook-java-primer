package chapter10.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 解答例の動作確認（課題1〜4）
 *
 * 期待する値と実際の値を比べ、「OK」か「NG」を表示します。
 * 解答例を書き換えたら実行し、すべてOKになることを確かめます。
 * 第21章で学ぶJUnitを使うと、同じ確認をテストとして書けます。
 */
public class SolutionCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== 第10章 解答例の動作確認 ===");

        GenericBox<String> box = new GenericBox<>();
        check("作ったばかりの箱は空", true, box.isEmpty());
        box.put("Hello");
        String value = box.get(); // キャストなしで受け取れる
        check("入れた値を取り出せる", "Hello", value);
        box.clear();
        check("clearすると空になる", null, box.get());

        Pair<String, Integer> pair = new Pair<>("項目", 100);
        Pair<Integer, String> swapped = pair.swap();
        check("swapで1つ目と2つ目が入れ替わる", 100, swapped.getFirst());
        check("swapの2つ目", "項目", swapped.getSecond());
        check("同じ値の組はequalsがtrue", new Pair<>("a", 1), new Pair<>("a", 1));
        check("同じ値の組はhashCodeが同じ", new Pair<>("a", 1).hashCode(), new Pair<>("a", 1).hashCode());
        check("値が違う組はequalsがfalse", false, new Pair<>("a", 1).equals(new Pair<>("a", 2)));
        check("nullを含む組も比べられる", new Pair<>(null, 1), new Pair<>(null, 1));

        NumberBox<Integer> intBox = new NumberBox<>(42);
        NumberBox<Double> doubleBox = new NumberBox<>(3.14);
        check("型の違う箱と比べられる（42 > 3.14）", 1, intBox.compareTo(doubleBox));
        check("型の違う箱と比べられる（3.14 < 42）", -1, doubleBox.compareTo(intBox));
        check("intとして取り出す", 3, doubleBox.getAsInt());
        check("正の数", true, intBox.isPositive());
        check("負の数は正でない", false, new NumberBox<>(-1L).isPositive());

        String[] words = {"Hello", "World", "Java"};
        GenericUtils.swap(words, 0, 2);
        check("配列の要素を入れ替える", "Java,World,Hello", String.join(",", words));
        check("要素の位置を探す", 1, GenericUtils.find(words, "World"));
        check("ない要素は-1", -1, GenericUtils.find(words, "C"));
        // 範囲外の位置を渡したときに例外が起きることの確かめ方は、第13章で学びます

        check("大きいほうを返す", "Banana", GenericUtils.max("Apple", "Banana"));
        check("リストの最小値", 1, GenericUtils.findMin(Arrays.asList(5, 2, 8, 1, 9)));
        check("空のリストの最小値はnull", null, GenericUtils.findMin(new ArrayList<Integer>()));
        check("nullでない要素の数", 2, GenericUtils.countNonNull(Arrays.asList("a", null, "b")));
        check("組の結合", new Pair<>("Start", "End"),
                GenericUtils.combinePairs(new Pair<>("Start", 1), new Pair<>(3.14, "End")));

        check("Integerのリストの合計", 15.0, NumberLists.sum(List.of(1, 2, 3, 4, 5)));
        check("Doubleのリストの合計", 7.5, NumberLists.sum(List.of(1.5, 2.5, 3.5)));
        List<Object> objects = new ArrayList<>();
        NumberLists.addSquares(objects, 3);
        check("Objectのリストに平方を足せる", List.of(1, 4, 9), objects);

        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, Object expected, Object actual) {
        total++;
        if (Objects.equals(expected, actual)) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
