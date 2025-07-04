/**
 * 第4章 基本課題4: Gameクラスの状態管理
 * 
 * 課題: ゲームを表すGameクラスを作成し、ゲーム状態の適切な管理を実装してください。
 * 
 * 要求仕様:
 * - privateフィールド: playerName（String）, level（int）, score（int）, isGameOver（boolean）
 * - スコア加算メソッド
 * - レベルアップ処理
 * - ゲーム状態の管理
 * 
 * 評価ポイント:
 * - ゲーム状態の適切な管理
 * - 条件に応じた処理の実装
 * - boolean型フィールドの活用
 */
public class Game {
    // TODO: privateフィールドを宣言してください
    // private String playerName;
    // private int level;
    // private int score;
    // private boolean isGameOver;
    
    // TODO: コンストラクタを実装してください
    // public Game(String playerName) {
    //     this.playerName = playerName;
    //     this.level = 1;
    //     this.score = 0;
    //     this.isGameOver = false;
    // }
    
    // TODO: 各フィールドのgetterメソッドを実装してください
    // public String getPlayerName() {
    //     return playerName;
    // }
    
    // public int getLevel() {
    //     return level;
    // }
    
    // public int getScore() {
    //     return score;
    // }
    
    // public boolean isGameOver() {
    //     return isGameOver;
    // }
    
    // TODO: スコア加算メソッドを実装してください
    // public void addScore(int points) {
    //     if (isGameOver) {
    //         System.out.println("ゲームオーバー中です。スコアを追加できません。");
    //         return;
    //     }
    //     
    //     if (points > 0) {
    //         score += points;
    //         System.out.println(points + "点を獲得しました！");
    //         System.out.println("現在のスコア: " + score + "点");
    //         
    //         // レベルアップ判定（100点ごとにレベルアップ）
    //         checkLevelUp();
    //     }
    // }
    
    // TODO: レベルアップ判定のprivateメソッドを実装してください
    // private void checkLevelUp() {
    //     int newLevel = (score / 100) + 1;
    //     if (newLevel > level) {
    //         level = newLevel;
    //         System.out.println("レベルアップ！レベル" + level + "になりました。");
    //         System.out.println("現在のレベル: " + level);
    //     }
    // }
    
    // TODO: ゲームオーバーメソッドを実装してください
    // public void gameOver() {
    //     isGameOver = true;
    //     System.out.println("ゲームオーバー！");
    //     System.out.println("最終スコア: " + score + "点");
    //     System.out.println("最終レベル: " + level);
    // }
    
    // TODO: ゲーム再開メソッドを実装してください
    // public void restart() {
    //     level = 1;
    //     score = 0;
    //     isGameOver = false;
    //     System.out.println("ゲームを再開しました。");
    // }
    
    // TODO: ゲーム状態表示メソッドを実装してください
    // public void showGameStatus() {
    //     System.out.println("プレイヤー: " + playerName);
    //     System.out.println("レベル: " + level);
    //     System.out.println("スコア: " + score + "点");
    //     if (isGameOver) {
    //         System.out.println("ゲーム状態: ゲームオーバー");
    //     } else {
    //         System.out.println("ゲーム状態: プレイ中");
    //     }
    // }
}