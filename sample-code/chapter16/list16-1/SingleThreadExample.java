/**
 * リスト16-1
 * SingleThreadExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (43行目)
 */

public class SingleThreadExample {
    public static void main(String[] args) {
        System.out.println("朝食の準備を開始");
        
        // コーヒーを淹れる（3秒かかる）
        makeCoffee();
        
        // トーストを焼く（2秒かかる）
        makeToast();
        
        // 卵を茹でる（5秒かかる）
        boilEgg();
        
        System.out.println("朝食の準備完了！");
        // 合計: 10秒かかる
    }
    
    private static void makeCoffee() {
        System.out.println("コーヒーを淹れています...");
        sleep(3000);
        System.out.println("コーヒーができました");
    }
    
    private static void makeToast() {
        System.out.println("トーストを焼いています...");
        sleep(2000);
        System.out.println("トーストができました");
    }
    
    private static void boilEgg() {
        System.out.println("卵を茹でています...");
        sleep(5000);
        System.out.println("ゆで卵ができました");
    }
    
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}