/**
 * Gameを試すプログラム（課題4の解答例）
 */
public class GameTest {
    public static void main(String[] args) {
        IO.println("=== ゲーム管理システム ===");
        
        // Gameオブジェクトを作成する
        Game game = new Game("太郎");
        game.showGameStatus();
        IO.println();
        
        // スコアを追加してレベルアップを確認する
        game.addScore(100);
        IO.println();
        
        // さらにスコアを追加する
        game.addScore(150);
        IO.println();
        
        // さらにスコアを追加してレベルアップを確認する
        game.addScore(100);
        IO.println();
        
        // 現在のゲーム状態を表示する
        IO.println("現在のゲーム状態:");
        game.showGameStatus();
        IO.println();
        
        // ゲームオーバーにする
        game.gameOver();
        IO.println();
        
        // ゲームオーバー後にスコア追加を試行する
        game.addScore(50);
        IO.println();
        
        // ゲームを再開する
        game.restart();
        game.showGameStatus();
        IO.println();
        
        // getterメソッドを使ってデータを取得する
        IO.println("getterメソッドでの取得:");
        IO.println("プレイヤー名: " + game.getPlayerName());
        IO.println("レベル: " + game.getLevel());
        IO.println("スコア: " + game.getScore());
        IO.println("ゲームオーバー: " + game.isGameOver());
    }
}