/**
 * スタックトレースを作らない例外の作り方と、その性質を見る。
 *
 *   javac NoStackTrace.java && java NoStackTrace
 */
public class NoStackTrace {

    /** 1. コンストラクターでwritableStackTrace=falseを渡す */
    static class NotWritable extends RuntimeException {
        NotWritable(String message) {
            super(message, null, false, false);
        }
    }

    /** 2. fillInStackTrace()をオーバーライドして何もしない */
    static class Overridden extends RuntimeException {
        Overridden(String message) {
            super(message);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    /** fillInStackTrace()が呼ばれた回数を数える */
    static int fillCalls = 0;

    static class Counting extends RuntimeException {
        @Override
        public synchronized Throwable fillInStackTrace() {
            fillCalls++;
            return super.fillInStackTrace();
        }
    }

    /** 3. 誤り: サブクラスのフィールドをfillInStackTrace()で見る */
    static class FieldCondition extends RuntimeException {
        private final Boolean wanted;

        FieldCondition(boolean wanted) {
            super("field");
            this.wanted = wanted;
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            // スーパークラスのコンストラクターから呼ばれた時点では、wantedはまだnull
            return wanted ? super.fillInStackTrace() : this;
        }
    }

    public static void main(String[] args) {
        IO.println("ふつうの例外の要素数: " + new RuntimeException("x").getStackTrace().length);

        NotWritable nw = new NotWritable("not writable");
        IO.println("writableStackTrace=falseの要素数: " + nw.getStackTrace().length);
        nw.setStackTrace(new StackTraceElement[] {
            new StackTraceElement("A", "b", "A.java", 1)
        });
        IO.println("setStackTraceのあとの要素数: " + nw.getStackTrace().length);
        nw.addSuppressed(new RuntimeException("s"));
        IO.println("enableSuppression=falseでaddSuppressedしたあとの数: "
                + nw.getSuppressed().length);

        IO.println("fillInStackTraceを上書きした例外の要素数: "
                + new Overridden("overridden").getStackTrace().length);

        new Counting();
        IO.println("コンストラクターがfillInStackTraceを呼んだ回数: " + fillCalls);

        try {
            new FieldCondition(true);
            IO.println("FieldCondition: 作れた");
        } catch (NullPointerException e) {
            IO.println("FieldCondition: NullPointerException");
        }
    }
}
