/**
 * ゲームの状態を持つクラス（課題4の解答例）
 */
public class Game {
    // privateフィールドを宣言する
    private String playerName;
    private int level;
    private int score;
    private boolean isGameOver;
    
    // コンストラクタ
    public Game(String playerName) {
        this.playerName = playerName;
        this.level = 1;
        this.score = 0;
        this.isGameOver = false;
    }
    
    // 各フィールドのgetterメソッド
    public String getPlayerName() {
        return playerName;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getScore() {
        return score;
    }
    
    public boolean isGameOver() {
        return isGameOver;
    }
    
    // スコア加算メソッド
    public void addScore(int points) {
        if (isGameOver) {
            IO.println("ゲームオーバー中です。スコアを追加できません。");
            return;
        }
        
        if (points > 0) {
            score += points;
            IO.println(points + "点を獲得しました！");
            IO.println("現在のスコア: " + score + "点");
            
            // レベルアップ判定（100点ごとにレベルアップ）
            checkLevelUp();
        }
    }
    
    // レベルアップ判定のprivateメソッド
    private void checkLevelUp() {
        int newLevel = (score / 100) + 1;
        if (newLevel > level) {
            level = newLevel;
            IO.println("レベルアップ！レベル" + level + "になりました。");
            IO.println("現在のレベル: " + level);
        }
    }
    
    // ゲームオーバーメソッド
    public void gameOver() {
        isGameOver = true;
        IO.println("ゲームオーバー！");
        IO.println("最終スコア: " + score + "点");
        IO.println("最終レベル: " + level);
    }
    
    // ゲーム再開メソッド
    public void restart() {
        level = 1;
        score = 0;
        isGameOver = false;
        IO.println("ゲームを再開しました。");
    }
    
    // ゲーム状態表示メソッド
    public void showGameStatus() {
        IO.println("プレイヤー: " + playerName);
        IO.println("レベル: " + level);
        IO.println("スコア: " + score + "点");
        if (isGameOver) {
            IO.println("ゲーム状態: ゲームオーバー");
        } else {
            IO.println("ゲーム状態: プレイ中");
        }
    }
}