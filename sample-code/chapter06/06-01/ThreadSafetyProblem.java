// マルチスレッド環境での競合状態の例
public class ThreadSafetyProblem {
    public static void main(String[] args) {
        MutablePoint point = new MutablePoint(0, 0);
        
        // 複数のスレッドが同じオブジェクトを変更
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                point.setX(point.getX() + 1);
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                point.setX(point.getX() + 1);
            }
        });
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            // スレッドが中断された場合の処理
            Thread.currentThread().interrupt();
            System.err.println("スレッドの待機が中断されました");
        }
        
        // 期待値: 2000, 実際の値: 予測不可能（1000～2000の間）
        IO.println("X座標: " + point.getX());
    }
}
