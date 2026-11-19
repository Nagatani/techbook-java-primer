// 第11章：匿名クラスから、finalを付けていない実質的にfinalな変数を参照する（Java 8から）
public class EffectivelyFinal {
    static Runnable make() {
        String message = "hello";
        return new Runnable() {
            @Override
            public void run() {
                message.length();
            }
        };
    }
}
