// 第6章：String・日付の読み取り・変更できないリスト・BigDecimalの速さ
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImmutabilityFacts {
    public static void main(String[] args) {
        System.out.println("String is final: " + Modifier.isFinal(String.class.getModifiers()));

        DateTimeFormatter smart = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("SMART 2026-02-30 -> " + LocalDate.parse("2026-02-30", smart));
        DateTimeFormatter strict = DateTimeFormatter.ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate.parse("2026-02-30", strict);
            System.out.println("STRICT accepted");
        } catch (DateTimeParseException e) {
            System.out.println("STRICT 2026-02-30 -> DateTimeParseException");
        }
        DateTimeFormatter strictYyyy = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate.parse("2026-02-28", strictYyyy);
            System.out.println("STRICT yyyy accepted");
        } catch (DateTimeParseException e) {
            System.out.println("STRICT yyyy 2026-02-28 -> DateTimeParseException");
        }

        List<String> original = new ArrayList<>(List.of("a"));
        List<String> view = Collections.unmodifiableList(original);
        List<String> copy = List.copyOf(original);
        original.add("b");
        System.out.println("view: " + view + " copy: " + copy);
        try {
            List.copyOf(Arrays.asList("x", null));
        } catch (NullPointerException e) {
            System.out.println("List.copyOf with null -> NullPointerException");
        }

        // BigDecimalの足し算とlongの足し算の時間を比べる（ウォームアップしてから測る）
        long ratioOk = 0;
        for (int round = 0; round < 5; round++) {
            long t0 = System.nanoTime();
            long sum = 0;
            for (int i = 0; i < 2_000_000; i++) sum += i;
            long t1 = System.nanoTime();
            BigDecimal big = BigDecimal.ZERO;
            for (int i = 0; i < 2_000_000; i++) big = big.add(BigDecimal.valueOf(i));
            long t2 = System.nanoTime();
            if (sum == big.longValue() && (t2 - t1) > 3 * (t1 - t0)) ratioOk++;
        }
        System.out.println("BigDecimal slower than long in 5 rounds: " + ratioOk);
    }
}
