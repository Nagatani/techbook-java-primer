/**
 * コンストラクターがthisを外へ漏らすと、finalフィールドが
 * 代入される前の値（nullや0）を見られてしまうことを観察する。
 * EarlyProductは、Java 25の柔軟なコンストラクター本体で、
 * サブクラスのフィールドをsuper(...)の前に代入して防ぐ例。
 *   javac -Xlint:this-escape ThisEscape.java && java ThisEscape
 */
public class ThisEscape {
    public static class Product {
        private final String name;

        public Product(String name) {
            describe();             // オーバーライドできるメソッドを呼んでいる
            this.name = name;
        }

        protected void describe() {
        }

        public String name() {
            return name;
        }
    }

    public static class LoggedProduct extends Product {
        private final int stock;

        public LoggedProduct(String name, int stock) {
            super(name);
            this.stock = stock;
        }

        @Override
        protected void describe() {
            // スーパークラスのコンストラクターの途中で呼ばれる
            IO.println("describe(): name=" + name() + " stock=" + stock);
        }
    }

    // Java 25の柔軟なコンストラクター本体（JEP 513）: super(...)の前にフィールドへ代入できる
    public static class EarlyProduct extends Product {
        private final int stock;

        public EarlyProduct(String name, int stock) {
            this.stock = stock;     // super(...)より前の代入
            super(name);
        }

        @Override
        protected void describe() {
            IO.println("describe(): name=" + name() + " stock=" + stock);
        }
    }

    public static void main(String[] args) {
        LoggedProduct p = new LoggedProduct("pen", 3);
        IO.println("完成後: name=" + p.name() + " stock=" + p.stock);
        IO.println("--- super(...)の前に代入した場合 ---");
        EarlyProduct e = new EarlyProduct("ink", 5);
        IO.println("完成後: name=" + e.name() + " stock=" + e.stock);
    }
}
