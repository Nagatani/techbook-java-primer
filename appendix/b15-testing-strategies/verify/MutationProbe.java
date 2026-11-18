import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * ミューテーションテストの考え方を、小さな自作の仕組みで観察する。
 * 元のコードに小さな変更（変異）を1つずつ入れた版（ミュータント）を用意し、
 * 同じテストクラスをJUnitで実行する。テストが1つでも失敗すれば、そのミュータントは「殺された」。
 * PITなどのツールは、同じことをバイトコードの書き換えで自動的に行う。
 */
public class MutationProbe {

    /** テストの対象：会員で1000円以上なら1割引きにした価格を返す */
    interface Pricing {
        int priceAfter(int price, boolean member);
    }

    static final Pricing ORIGINAL =
            (price, member) -> member && price >= 1000 ? price - price / 10 : price;

    /** ミュータント（名前 → 変異を入れた実装） */
    static final Map<String, Pricing> MUTANTS = new LinkedHashMap<>();
    static {
        MUTANTS.put(">= を > に",
                (price, member) -> member && price > 1000 ? price - price / 10 : price);
        MUTANTS.put("&& を || に",
                (price, member) -> member || price >= 1000 ? price - price / 10 : price);
        MUTANTS.put("- を + に",
                (price, member) -> member && price >= 1000 ? price + price / 10 : price);
        MUTANTS.put("10 を 9 に",
                (price, member) -> member && price >= 1000 ? price - price / 9 : price);
        MUTANTS.put("割引きの式を消す",
                (price, member) -> price);
        MUTANTS.put(">= 1000 を > 999 に",
                (price, member) -> member && price > 999 ? price - price / 10 : price);
    }

    /** テストが使う実装（ミュータントに差し替える） */
    static Pricing target = ORIGINAL;

    /** 弱いテスト：割引きする場合としない場合を1つずつ（どちらの分岐も通る） */
    static class WeakSuite {
        @Test
        void memberGetsDiscount() {
            assertEquals(1800, target.priceAfter(2000, true));
        }

        @Test
        void nonMemberPaysFullPrice() {
            assertEquals(2000, target.priceAfter(2000, false));
        }
    }

    /** 強いテスト：境界の1000円と999円も確かめる */
    static class StrongSuite {
        @Test
        void memberGetsDiscount() {
            assertEquals(1800, target.priceAfter(2000, true));
        }

        @Test
        void nonMemberPaysFullPrice() {
            assertEquals(2000, target.priceAfter(2000, false));
        }

        @Test
        void discountStartsAt1000() {
            assertEquals(900, target.priceAfter(1000, true));
        }

        @Test
        void noDiscountBelow1000() {
            assertEquals(999, target.priceAfter(999, true));
        }
    }

    static boolean allPass(Class<?> suite) {
        return ProbeRunner.run(suite, false).stream()
                .allMatch(o -> o.status().equals("SUCCESSFUL"));
    }

    static void evaluate(Class<?> suite) {
        IO.println("--- " + suite.getSimpleName());
        target = ORIGINAL;
        IO.println("original: " + (allPass(suite) ? "all tests pass" : "tests fail"));
        int killed = 0;
        for (Map.Entry<String, Pricing> m : MUTANTS.entrySet()) {
            target = m.getValue();
            boolean survived = allPass(suite);
            if (!survived) {
                killed++;
            }
            IO.println("  %-8s %s".formatted(survived ? "SURVIVED" : "KILLED", m.getKey()));
        }
        IO.println("mutation score: %d/%d".formatted(killed, MUTANTS.size()));
    }

    public static void main(String[] args) {
        evaluate(WeakSuite.class);
        evaluate(StrongSuite.class);
    }
}
