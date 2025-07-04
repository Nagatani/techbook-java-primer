/**
 * 第4章 演習問題4: Gameクラスの解答例
 */
public class Game {
    private String playerName;
    private int level;
    private int score;
    private boolean isGameOver;
    
    public Game(String playerName) {
        this.playerName = playerName;
        this.level = 1;
        this.score = 0;
        this.isGameOver = false;
    }
    
    public void addScore(int points) {
        if (isGameOver) {
            System.out.println("ゲームが終了しています。");
            return;
        }
        
        if (points > 0) {
            score += points;
            System.out.println(points + "点を獲得しました！現在のスコア: " + score + "点");
            checkLevelUp();
        }
    }
    
    private void checkLevelUp() {
        int newLevel = (score / 100) + 1;
        if (newLevel > level) {
            level = newLevel;
            System.out.println("レベルアップ！レベル" + level + "になりました。");
        }
    }
    
    public void gameOver() {
        isGameOver = true;
        System.out.println("ゲームオーバー！最終スコア: " + score + "点、最終レベル: " + level);
    }
    
    public void displayStatus() {
        System.out.println("プレイヤー: " + playerName);
        System.out.println("レベル: " + level);
        System.out.println("スコア: " + score + "点");
        System.out.println("ゲーム状態: " + (isGameOver ? "終了" : "プレイ中"));
    }
    
    // getterメソッド
    public String getPlayerName() { return playerName; }
    public int getLevel() { return level; }
    public int getScore() { return score; }
    public boolean isGameOver() { return isGameOver; }
}