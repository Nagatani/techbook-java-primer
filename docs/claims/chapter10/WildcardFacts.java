// 第10章：ワイルドカードとvarで書けること
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class WildcardFacts {
    static double sum(List<? extends Number> list) {
        double s = 0;
        for (Number n : list) s += n.doubleValue();
        return s;
    }

    static void fill(List<? super Integer> list) {
        list.add(1);
    }

    public static void main(String[] args) throws Exception {
        List<?> unknown = new ArrayList<String>();
        unknown.add(null);
        System.out.println("List<?>.add(null): " + unknown.size());

        System.out.println("extends: " + sum(List.of(1, 2)) + " " + sum(List.of(1.5)));
        List<Number> nums = new ArrayList<>();
        List<Object> objs = new ArrayList<>();
        fill(nums);
        fill(objs);
        System.out.println("super: " + nums + " " + objs);

        var diamond = new ArrayList<>();
        diamond.add(1);
        diamond.add("s");
        System.out.println("var with <>: " + diamond);

        for (var i = 0; i < 1; i++) { }
        for (var s : List.of("a")) { }
        try (var r = new StringReader("x")) { }
        BiFunction<Integer, Integer, Integer> add = (var a, var b) -> a + b;
        int var = add.apply(1, 2);
        System.out.println("var as a variable name: " + var);
    }
}
