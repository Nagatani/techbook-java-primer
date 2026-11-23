// 第13章：オーバーライドと非検査例外、マルチキャッチ、try-with-resourcesの閉じる順と抑制された例外、finallyでの上書き
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExceptionFacts {
    static class Parent {
        void load() throws IOException { }
    }

    static class Child extends Parent {
        @Override
        void load() throws FileNotFoundException, IllegalStateException { }
    }

    record Res(String name, boolean failOnClose) implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("close " + name);
            if (failOnClose) throw new IllegalStateException("close failed: " + name);
        }
    }

    public static void main(String[] args) throws Exception {
        new Child().load();
        try {
            if (args.length == 0) throw new IOException("io");
            Class.forName("Nope");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("multi-catch: " + e.getMessage());
        }

        try (Res a = new Res("A", false); Res b = new Res("B", false)) {
            System.out.println("body");
        }

        try (Res c = new Res("C", true)) {
            throw new RuntimeException("original");
        } catch (RuntimeException e) {
            System.out.println("thrown: " + e.getMessage() + ", suppressed: "
                    + e.getSuppressed()[0].getMessage());
        }

        try {
            Res d = new Res("D", true);
            try {
                throw new RuntimeException("original");
            } finally {
                d.close();
            }
        } catch (RuntimeException e) {
            System.out.println("finally style thrown: " + e.getMessage());
        }
    }
}
