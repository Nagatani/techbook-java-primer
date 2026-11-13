public class MultiThreadExample {
    public static void main(String[] args) throws InterruptedException {
        IO.println("朝食の準備を開始");
        
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
        
        IO.println("朝食の準備完了！");
        // 合計: 約5秒（もっとも時間のかかる卵の時間）で完了
    }
    
    // makeCoffee、makeToast、boilEgg、sleepメソッドはサンプルコード15-1と同じ
    private static void makeCoffee() {
        IO.println("コーヒーを淹れています...");
        sleep(3000);
        IO.println("コーヒーができました");
    }
    
    private static void makeToast() {
        IO.println("トーストを焼いています...");
        sleep(2000);
        IO.println("トーストができました");
    }
    
    private static void boilEgg() {
        IO.println("卵を茹でています...");
        sleep(5000);
        IO.println("ゆで卵ができました");
    }
    
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
