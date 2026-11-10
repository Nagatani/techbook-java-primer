/**
 * 第4章 演習問題4: GameTestクラスの解答例
 */
public class GameTest {
    public static void main(String[] args) {
        IO.println("=== Game クラスのテスト ===");
        
        Game game = new Game("太郎");
        
        IO.println("初期状態:");
        game.displayStatus();
        
        IO.println("\n--- ゲームプレイ ---");
        game.addScore(100);
        game.addScore(50);
        game.addScore(200);
        
        IO.println("\n現在の状態:");
        game.displayStatus();
        
        IO.println("\n--- ゲーム終了 ---");
        game.gameOver();
        
        IO.println("\nゲーム終了後のスコア追加試行:");
        game.addScore(50);  // ゲーム終了後なので無効
        
        game.displayStatus();
    }
}