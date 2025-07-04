/**
 * 第4章 演習問題4: GameTestクラスの解答例
 */
public class GameTest {
    public static void main(String[] args) {
        System.out.println("=== Game クラスのテスト ===");
        
        Game game = new Game("太郎");
        
        System.out.println("初期状態:");
        game.displayStatus();
        
        System.out.println("\n--- ゲームプレイ ---");
        game.addScore(100);
        game.addScore(50);
        game.addScore(200);
        
        System.out.println("\n現在の状態:");
        game.displayStatus();
        
        System.out.println("\n--- ゲーム終了 ---");
        game.gameOver();
        
        System.out.println("\nゲーム終了後のスコア追加試行:");
        game.addScore(50);  // ゲーム終了後なので無効
        
        game.displayStatus();
    }
}