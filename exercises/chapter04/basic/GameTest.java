/**
 * 第4章 基本課題4: GameTest クラス
 * 
 * Gameクラスをテストするためのクラスです。
 * ゲーム状態の管理とレベルアップ処理を確認してください。
 * 
 * 実行例:
 * === ゲーム管理システム ===
 * プレイヤー: 太郎
 * レベル: 1
 * スコア: 0点
 * ゲーム状態: プレイ中
 *
 * 100点を獲得しました！
 * 現在のスコア: 100点
 * レベルアップ！レベル2になりました。
 * 現在のレベル: 2
 *
 * 150点を獲得しました！
 * 現在のスコア: 250点
 * レベルアップ！レベル3になりました。
 * 現在のレベル: 3
 *
 * 100点を獲得しました！
 * 現在のスコア: 350点
 * レベルアップ！レベル4になりました。
 * 現在のレベル: 4
 *
 * 現在のゲーム状態:
 * プレイヤー: 太郎
 * レベル: 4
 * スコア: 350点
 * ゲーム状態: プレイ中
 *
 * ゲームオーバー！
 * 最終スコア: 350点
 * 最終レベル: 4
 *
 * ゲームオーバー中です。スコアを追加できません。
 *
 * ゲームを再開しました。
 * プレイヤー: 太郎
 * レベル: 1
 * スコア: 0点
 * ゲーム状態: プレイ中
 *
 * getterメソッドでの取得:
 * プレイヤー名: 太郎
 * レベル: 1
 * スコア: 0
 * ゲームオーバー: false
 */
public class GameTest {
    public static void main(String[] args) {
        IO.println("=== ゲーム管理システム ===");
        
        // TODO: Gameオブジェクトを作成してください
        // Game game = new Game("太郎");
        // game.showGameStatus();
        // IO.println();
        
        // TODO: スコアを追加してレベルアップを確認してください
        // game.addScore(100);
        // IO.println();
        
        // TODO: さらにスコアを追加してください
        // game.addScore(150);
        // IO.println();
        
        // TODO: さらにスコアを追加してレベルアップを確認してください
        // game.addScore(100);
        // IO.println();
        
        // TODO: 現在のゲーム状態を表示してください
        // IO.println("現在のゲーム状態:");
        // game.showGameStatus();
        // IO.println();
        
        // TODO: ゲームオーバーにしてください
        // game.gameOver();
        // IO.println();
        
        // TODO: ゲームオーバー後にスコア追加を試行してください
        // game.addScore(50);
        // IO.println();
        
        // TODO: ゲームを再開してください
        // game.restart();
        // game.showGameStatus();
        // IO.println();
        
        // TODO: getterメソッドを使ってデータを取得してください
        // IO.println("getterメソッドでの取得:");
        // IO.println("プレイヤー名: " + game.getPlayerName());
        // IO.println("レベル: " + game.getLevel());
        // IO.println("スコア: " + game.getScore());
        // IO.println("ゲームオーバー: " + game.isGameOver());
    }
}