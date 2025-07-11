/**
 * リスト13-1
 * コードスニペット
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (73行目)
 */

// 従来の匿名クラスを使った方法
Runnable task1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello from anonymous class!");
    }
};

// ラムダ式を使った方法（同じ動作）
Runnable task2 = () -> System.out.println("Hello from lambda!");

// 実行（どちらも同じ結果）
task1.run();  // Hello from anonymous class!
task2.run();  // Hello from lambda!