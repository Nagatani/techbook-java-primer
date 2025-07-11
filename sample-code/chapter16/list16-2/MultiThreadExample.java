/**
 * リスト16-2
 * MultiThreadExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (92行目)
 */

public class MultiThreadExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("朝食の準備を開始");
        
        // それぞれ別々のスレッドで同時に実行
        Thread coffeeThread = new Thread(() -> makeCoffee());
        Thread toastThread = new Thread(() -> makeToast());
        Thread eggThread = new Thread(() -> boilEgg());
        
        // スレッドを開始
        coffeeThread.start();
        toastThread.start();
        eggThread.start();
        
        // すべてのスレッドが終了するのを待つ
        coffeeThread.join();
        toastThread.join();
        eggThread.join();
        
        System.out.println("朝食の準備完了！");
        // 合計: 約5秒（最も時間のかかる卵の時間）で完了
    }
    
    // makeCoffee、makeToast、boilEgg、sleepメソッドは同じ
}