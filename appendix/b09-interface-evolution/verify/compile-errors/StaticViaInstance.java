/** インターフェイスのstaticメソッドは、インターフェイスの型の変数からも呼べない（クラスのstaticメソッドとの違い） */
public class StaticViaInstance {
    interface Greeter {
        static String hello() {
            return "hello";
        }
    }

    static String call(Greeter greeter) {
        return greeter.hello();
    }
}
