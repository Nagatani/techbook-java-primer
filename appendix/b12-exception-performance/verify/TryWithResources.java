/**
 * try-with-resourcesが、closeの呼び出しとaddSuppressedに展開されることを見る。
 *
 *   javac TryWithResources.java && javap -c -p TryWithResources
 *   java TryWithResources
 */
public class TryWithResources {

    static class Res implements AutoCloseable {
        private final String name;
        private final boolean failOnClose;

        Res(String name, boolean failOnClose) {
            this.name = name;
            this.failOnClose = failOnClose;
            IO.println("open " + name);
        }

        @Override
        public void close() {
            IO.println("close " + name);
            if (failOnClose) {
                throw new IllegalStateException("close " + name + " failed");
            }
        }
    }

    static void use(boolean fail) {
        try (Res a = new Res("a", fail); Res b = new Res("b", fail)) {
            IO.println("body");
            if (fail) {
                throw new RuntimeException("body failed");
            }
        }
    }

    public static void main(String[] args) {
        use(false);
        IO.println("---");
        try {
            use(true);
        } catch (RuntimeException e) {
            IO.println("caught: " + e.getMessage());
            for (Throwable s : e.getSuppressed()) {
                IO.println("  suppressed: " + s.getMessage());
            }
        }
    }
}
