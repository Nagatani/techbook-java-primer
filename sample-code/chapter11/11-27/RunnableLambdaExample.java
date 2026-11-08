public class RunnableLambdaExample {
    public static void main(String[] args) {
        System.out.println("メインスレッド開始");
        
        // 匿名クラスでのRunnable
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                System.out.println("Thread t1 (Anonymous) is running...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread t1 (Anonymous) finished");
            }
        });
        t1.start();

        // ラムダ式でのRunnable
        Thread t2 = new Thread(() -> {
            System.out.println("Thread t2 (Lambda) is running...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread t2 (Lambda) finished");
        });
        t2.start();
        
        // より簡潔なラムダ式（単一ステートメント）
        Thread t3 = new Thread(() -> System.out.println("Thread t3 (Simple Lambda) executed"));
        t3.start();
        
        // すべてのスレッドの完了を待機
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("メインスレッド終了");
    }
}
