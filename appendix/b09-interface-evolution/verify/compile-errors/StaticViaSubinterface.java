/** インターフェイスのstaticメソッドは、サブインターフェイスにも継承されない */
public class StaticViaSubinterface {
    interface Greeter {
        static String hello() {
            return "hello";
        }
    }

    interface Polite extends Greeter {
    }

    static String call() {
        return Polite.hello();
    }
}
