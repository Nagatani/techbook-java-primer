import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.FieldLayout;

/**
 * JOL（Java Object Layout）で、カウンターのフィールドがメモリのどこに置かれるかを観察する。
 * JOLのJAR（org.openjdk.jol:jol-core:0.17）をクラスパスに加えて実行する。
 *
 *   javac -cp jol-core-0.17.jar ContendedLayout.java
 *   java -Djdk.attach.allowAttachSelf=true -cp jol-core-0.17.jar:. ContendedLayout
 *   java -XX:-RestrictContended -Djdk.attach.allowAttachSelf=true -cp jol-core-0.17.jar:. ContendedLayout
 *
 * JOLは@Contendedの値を読もうとするので、実行時にも
 * --add-exports java.base/jdk.internal.vm.annotation=ALL-UNNAMED を付けるとエラーの表示が出ない。
 *
 * @Contendedは公開されていないJDKの内部のアノテーションで、
 * コンパイルに --add-exports java.base/jdk.internal.vm.annotation=ALL-UNNAMED が要る。
 * このプログラムは、だれでも同じ手順で動かせるように、@Contendedを付けたクラスを
 * 実行中にjavac（javax.tools）でコンパイルして読み込む。
 */
public class ContendedLayout {

    /** 何もしないと、4つのカウンターは8バイトおきに並ぶ */
    static class PlainCounters {
        volatile long counter1;
        volatile long counter2;
        volatile long counter3;
        volatile long counter4;
    }

    /** 手でパディングしたカウンター（src/のFalseSharingDemo.PaddedCountersと同じ考え方） */
    static class ManualPadding {
        volatile long p1, p2, p3, p4, p5, p6, p7;
        volatile long counter1;
        volatile long q1, q2, q3, q4, q5, q6, q7;
        volatile long r1, r2, r3, r4, r5, r6, r7;
        volatile long counter2;
    }

    static final String CONTENDED_SOURCE = """
            public class ContendedCounters {
                @jdk.internal.vm.annotation.Contended
                volatile long counter1;
                @jdk.internal.vm.annotation.Contended
                volatile long counter2;
            }
            """;

    public static void main(String[] args) throws Exception {
        // JOLは最初に使うときに警告を出すことがあるので、表示の前に一度使っておく
        ClassLayout.parseClass(Object.class);

        IO.println("== PlainCounters ==");
        printCounters(PlainCounters.class);

        IO.println("== ManualPadding ==");
        printCounters(ManualPadding.class);

        IO.println("== ContendedCounters（アプリケーションのクラス） ==");
        printCounters(compileContended());

        // LongAdderの内部で使うセル。JDKのクラスなので、-XX:-RestrictContendedがなくても効く
        Class<?> cell = Class.forName("java.util.concurrent.atomic.Striped64$Cell");
        IO.println("== Striped64$Cell（JDKのクラス、@Contendedが付いている） ==");
        IO.println(ClassLayout.parseClass(cell).toPrintable());
        IO.println("Striped64$Cellの大きさ: " + ClassLayout.parseClass(cell).instanceSize() + "バイト");
    }

    /** counterで始まるフィールドのオフセットと、隣のカウンターとの距離を表示する */
    static void printCounters(Class<?> c) {
        ClassLayout layout = ClassLayout.parseClass(c);
        long prev = -1;
        for (FieldLayout f : layout.fields()) {
            if (!f.name().startsWith("counter")) {
                continue;
            }
            String gap = prev < 0 ? "" : "（前のカウンターから" + (f.offset() - prev) + "バイト）";
            IO.println(f.name() + ": オフセット" + f.offset() + gap);
            prev = f.offset();
        }
        IO.println("インスタンスの大きさ: " + layout.instanceSize() + "バイト");
    }

    /** @Contendedを付けたクラスを一時ディレクトリでコンパイルして読み込む */
    static Class<?> compileContended() throws Exception {
        Path dir = Files.createTempDirectory("contended");
        try {
            Path src = dir.resolve("ContendedCounters.java");
            Files.writeString(src, CONTENDED_SOURCE);
            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
            int rc = javac.run(null, null, null,
                    "--add-exports", "java.base/jdk.internal.vm.annotation=ALL-UNNAMED",
                    "-d", dir.toString(), src.toString());
            if (rc != 0) {
                throw new IllegalStateException("コンパイルできない");
            }
            URLClassLoader loader = new URLClassLoader(new URL[] {dir.toUri().toURL()});
            Class<?> c = loader.loadClass("ContendedCounters");
            ClassLayout.parseClass(c); // 削除する前に読み込みを済ませる
            return c;
        } finally {
            try (Stream<Path> files = Files.walk(dir)) {
                files.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
    }
}
