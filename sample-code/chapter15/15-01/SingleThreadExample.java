public class SingleThreadExample {
    public static void main(String[] args) {
        IO.println("朝食の準備を開始");
        
        // コーヒーを淹れる（3秒かかる）
        makeCoffee();
        
        // トーストを焼く（2秒かかる）
        makeToast();
        
        // 卵を茹でる（5秒かかる）
        boilEgg();
        
        IO.println("朝食の準備完了！");
        // 合計: 10秒かかる
    }
    
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
