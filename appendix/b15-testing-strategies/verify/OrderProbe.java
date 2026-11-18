import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * テストメソッドの実行順と、テストクラスのインスタンスの作られ方を観察する。
 */
public class OrderProbe {

    static final List<String> log = new ArrayList<>();

    /** 実行順を指定しないクラス（メソッドはfirst〜fifthの順に書いてある） */
    static class DefaultOrder {
        @Test void first() { log.add("first"); }
        @Test void second() { log.add("second"); }
        @Test void third() { log.add("third"); }
        @Test void fourth() { log.add("fourth"); }
        @Test void fifth() { log.add("fifth"); }
    }

    /** @Orderの値の小さい順に実行させるクラス */
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    static class Ordered {
        @Test @Order(3) void step1() { log.add("step1"); }
        @Test @Order(1) void step2() { log.add("step2"); }
        @Test @Order(2) void step3() { log.add("step3"); }
    }

    /** 既定（PER_METHOD）では、テストメソッドごとに新しいインスタンスが作られる */
    static class PerMethod {
        int count = 0;
        @Test void a() { count++; log.add("count=" + count); }
        @Test void b() { count++; log.add("count=" + count); }
        @Test void c() { count++; log.add("count=" + count); }
    }

    /** PER_CLASSでは、すべてのテストメソッドが1つのインスタンスを共有する */
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    static class PerClass {
        int count = 0;
        @Test void a() { count++; log.add("count=" + count); }
        @Test void b() { count++; log.add("count=" + count); }
        @Test void c() { count++; log.add("count=" + count); }
    }

    /** 前のテストが静的フィールドに残した状態に頼るテスト。実行順や実行の回数で結果が変わる */
    static class Dependent {
        static List<String> items = new ArrayList<>();
        @Test void addItem() { items.add("x"); }
        @Test void hasOneItem() { assertEquals(1, items.size()); }
    }

    static String runAndLog(Class<?> c) {
        log.clear();
        ProbeRunner.run(c, false);
        return String.join(" ", log);
    }

    public static void main(String[] args) {
        IO.println("default order (1st run): " + runAndLog(DefaultOrder.class));
        IO.println("default order (2nd run): " + runAndLog(DefaultOrder.class));
        IO.println("@Order(3,1,2):           " + runAndLog(Ordered.class));
        IO.println("PER_METHOD:              " + runAndLog(PerMethod.class));
        IO.println("PER_CLASS:               " + runAndLog(PerClass.class));
        IO.println("dependent tests (1st run):");
        ProbeRunner.run(Dependent.class);
        IO.println("dependent tests (2nd run, static field kept):");
        ProbeRunner.run(Dependent.class);
    }
}
