public class LambdaIntroExample {
    public static void main(String[] args) {
        // 従来の匿名クラスを使った方法
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                IO.println("Hello from anonymous class!");
            }
        };

        // ラムダ式を使った方法（同じ動作）
        Runnable task2 = () -> IO.println("Hello from lambda!");

        // 実行（どちらも同じ結果）
        task1.run();  // Hello from anonymous class!
        task2.run();  // Hello from lambda!
    }
}
