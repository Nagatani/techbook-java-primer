/** 匿名クラスから参照するローカル変数も、実質的にfinalでなければならない */
public class AnonymousCapture {
    static Runnable make() {
        int n = 1;
        n = 2;
        return new Runnable() {
            @Override
            public void run() {
                IO.println(n);
            }
        };
    }
}
