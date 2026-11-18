/** インターフェイスのstaticメソッドは、実装クラスに継承されない */
public class StaticViaImplClass {
    interface Greeter {
        static String hello() {
            return "hello";
        }
    }

    static class Impl implements Greeter {
    }

    static String call() {
        return Impl.hello();
    }
}
