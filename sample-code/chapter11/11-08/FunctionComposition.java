import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionComposition {
    public static void main(String[] args) {
        Function<Integer, Integer> plusOne = x -> x + 1;
        Function<Integer, Integer> twice = x -> x * 2;

        // andThen: plusOneを先に、twiceをあとに適用する
        Function<Integer, Integer> plusOneThenTwice = plusOne.andThen(twice);
        // compose: twiceを先に、plusOneをあとに適用する
        Function<Integer, Integer> twiceThenPlusOne = plusOne.compose(twice);

        IO.println("andThen: " + plusOneThenTwice.apply(5)); // (5 + 1) * 2
        IO.println("compose: " + twiceThenPlusOne.apply(5)); // 5 * 2 + 1

        Predicate<String> isEmpty = s -> s.isEmpty();
        Predicate<String> isShort = s -> s.length() < 5;
        Predicate<String> startsWithJ = s -> s.startsWith("J");

        Predicate<String> notEmpty = isEmpty.negate();          // 空でない
        Predicate<String> shortAndJ = isShort.and(startsWithJ); // 短く、かつJで始まる
        Predicate<String> emptyOrJ = isEmpty.or(startsWithJ);   // 空、またはJで始まる

        for (String word : List.of("Java", "", "JavaScript", "Go")) {
            IO.println("'%s': negate=%b, and=%b, or=%b".formatted(
                word, notEmpty.test(word), shortAndJ.test(word), emptyOrJ.test(word)));
        }
    }
}
