/**
 * リスト13-17
 * ThreadLambdaExampleクラス
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (688行目)
 */

public class ThreadLambdaExample {
    public static void main(String[] args) {
        // 匿名クラスでのRunnable
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                System.out.println("Thread t1 (Anonymous) is running...");
            }
        });
        t1.start();

        // ラムダ式でのRunnable
        Thread t2 = new Thread(() -> System.out.println("Thread t2 (Lambda) is running..."));
        t2.start();
    }
}