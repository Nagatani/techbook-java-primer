/**
 * ゲームキャラクターを表す抽象クラス
 * 
 * 【学習ポイント】
 * 1. 抽象クラスとインターフェイスの組み合わせ
 * 2. 複数インターフェイスの実装
 * 3. テンプレートメソッドパターンの活用
 * 4. ゲームロジックの抽象化
 * 5. 状態管理と行動パターン
 * 
 * 【設計のポイント】
 * - Drawableインターフェイスを実装
 * - キャラクター共通の状態と行動を定義
 * - サブクラスで具体的な行動を実装
 * - ゲームループでの更新処理を考慮
 */
public abstract class GameCharacter implements Drawable, Drawable.Movable, Drawable.ZOrderable {
    
    // === 基本属性 ===
    protected final String name;
    protected final CharacterType type;
    protected double x, y;  // 位置
    protected double width, height;  // サイズ
    protected boolean visible;
    protected int zOrder;
    
    // === ステータス ===
    protected int maxHealth;
    protected int currentHealth;
    protected int attackPower;
    protected int defense;
    protected double moveSpeed;
    
    // === 状態 ===
    protected CharacterState state;
    protected Direction direction;
    protected long lastUpdateTime;
    protected boolean isAlive;
    
    /**
     * ゲームキャラクターのコンストラクタ
     */
    protected GameCharacter(String name, CharacterType type, double x, double y, 
                          double width, double height, int maxHealth, int attackPower, 
                          int defense, double moveSpeed) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.attackPower = attackPower;
        this.defense = defense;
        this.moveSpeed = moveSpeed;
        this.visible = true;
        this.zOrder = 0;
        this.state = CharacterState.IDLE;
        this.direction = Direction.RIGHT;
        this.lastUpdateTime = System.currentTimeMillis();
        this.isAlive = true;
    }
    
    // === Drawable インターフェイスの実装 ===
    
    @Override
    public Drawable.Position getPosition() {
        return new Drawable.Position(x, y);
    }
    
    @Override
    public Drawable.Size getSize() {
        return new Drawable.Size(width, height);
    }
    
    @Override
    public boolean isVisible() {
        return visible && isAlive;
    }
    
    /**
     * キャラクターの描画（抽象メソッド）
     * サブクラスで具体的な描画処理を実装
     */
    @Override
    public abstract void draw();
    
    // === Movable インターフェイスの実装 ===
    
    @Override
    public void moveTo(double newX, double newY) {
        this.x = newX;
        this.y = newY;
        updateDirection(newX, newY);
        setState(CharacterState.MOVING);
    }
    
    /**
     * 移動方向を更新
     */
    protected void updateDirection(double newX, double newY) {
        if (newX > this.x) {
            direction = Direction.RIGHT;
        } else if (newX < this.x) {
            direction = Direction.LEFT;
        }
        // Y方向の処理も必要に応じて追加
    }
    
    // === ZOrderable インターフェイスの実装 ===
    
    @Override
    public int getZOrder() {
        return zOrder;
    }
    
    @Override
    public void setZOrder(int zOrder) {
        this.zOrder = zOrder;
    }
    
    // === 抽象メソッド（サブクラスで実装が必要） ===
    
    /**
     * キャラクターの更新処理
     * ゲームループで呼び出される
     */
    public abstract void update(long currentTime);
    
    /**
     * 攻撃処理
     * @param target 攻撃対象
     */
    public abstract void attack(GameCharacter target);
    
    /**
     * ダメージ計算
     * @param attacker 攻撃者
     * @param baseDamage 基本ダメージ
     * @return 実際のダメージ
     */
    protected abstract int calculateDamage(GameCharacter attacker, int baseDamage);
    
    /**
     * AI行動パターン（NPCの場合）
     */
    public abstract void performAI();
    
    /**
     * キャラクター固有のスキル使用
     */
    public abstract void useSpecialSkill();
    
    // === 共通メソッド ===
    
    /**
     * ダメージを受ける
     */
    public void takeDamage(int damage) {
        if (!isAlive) return;
        
        int actualDamage = Math.max(1, damage - defense);  // 最低1ダメージ
        currentHealth = Math.max(0, currentHealth - actualDamage);
        
        setState(CharacterState.HURT);
        
        if (currentHealth <= 0) {
            die();
        }
        
        onTakeDamage(actualDamage);
    }
    
    /**
     * 回復処理
     */
    public void heal(int amount) {
        if (!isAlive) return;
        
        currentHealth = Math.min(maxHealth, currentHealth + amount);
        onHeal(amount);
    }
    
    /**
     * 死亡処理
     */
    protected void die() {
        isAlive = false;
        setState(CharacterState.DEAD);
        onDeath();
    }
    
    /**
     * 指定した座標への移動（アニメーション付き）
     */
    public void moveTowards(double targetX, double targetY, double deltaTime) {
        if (!isAlive || state == CharacterState.DEAD) return;
        
        double distance = Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2));
        
        if (distance <= moveSpeed * deltaTime) {
            // 目標に到達
            moveTo(targetX, targetY);
            setState(CharacterState.IDLE);
        } else {
            // 目標に向かって移動
            double ratio = (moveSpeed * deltaTime) / distance;
            double newX = x + (targetX - x) * ratio;
            double newY = y + (targetY - y) * ratio;
            moveTo(newX, newY);
        }
    }
    
    /**
     * 他のキャラクターとの距離を計算
     */
    public double getDistanceTo(GameCharacter other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * 攻撃範囲内にいるかを判定
     */
    public boolean isInAttackRange(GameCharacter target) {
        return getDistanceTo(target) <= getAttackRange();
    }
    
    /**
     * 攻撃範囲を取得（サブクラスでオーバーライド可能）
     */
    protected double getAttackRange() {
        return Math.max(width, height) + 20;  // デフォルトは自分のサイズ + 20
    }
    
    // === 状態管理 ===
    
    /**
     * 状態を設定
     */
    protected void setState(CharacterState newState) {
        CharacterState oldState = this.state;
        this.state = newState;
        onStateChanged(oldState, newState);
    }
    
    /**
     * 状態変更時のコールバック
     */
    protected void onStateChanged(CharacterState oldState, CharacterState newState) {
        // サブクラスでオーバーライドして具体的な処理を実装
    }
    
    /**
     * ダメージを受けた時のコールバック
     */
    protected void onTakeDamage(int damage) {
        // サブクラスでオーバーライドして具体的な処理を実装
    }
    
    /**
     * 回復時のコールバック
     */
    protected void onHeal(int amount) {
        // サブクラスでオーバーライドして具体的な処理を実装
    }
    
    /**
     * 死亡時のコールバック
     */
    protected void onDeath() {
        // サブクラスでオーバーライドして具体的な処理を実装
    }
    
    // === ゲッター・セッター ===
    
    public String getName() { return name; }
    public CharacterType getType() { return type; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttackPower() { return attackPower; }
    public int getDefense() { return defense; }
    public double getMoveSpeed() { return moveSpeed; }
    public CharacterState getState() { return state; }
    public Direction getDirection() { return direction; }
    public boolean isAlive() { return isAlive; }
    
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setAttackPower(int attackPower) { this.attackPower = attackPower; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setMoveSpeed(double moveSpeed) { this.moveSpeed = moveSpeed; }
    
    /**
     * 健康状態を取得
     */
    public double getHealthPercentage() {
        return (double) currentHealth / maxHealth;
    }
    
    /**
     * 重傷かどうかを判定
     */
    public boolean isCriticallyInjured() {
        return getHealthPercentage() < 0.2;  // 20%以下
    }
    
    // === 描画関連のオーバーライド ===
    
    @Override
    public void beforeDraw() {
        // キャラクター描画前の共通処理
        if (!isAlive) {
            // 死亡時の透明度処理など
        }
    }
    
    @Override
    public void afterDraw() {
        // キャラクター描画後の共通処理
        drawHealthBar();  // 体力バーの描画
        drawStateIndicator();  // 状態インジケーターの描画
    }
    
    /**
     * 体力バーを描画
     */
    protected void drawHealthBar() {
        if (!isAlive || !visible) return;
        
        double barWidth = width;
        double barHeight = 4;
        double barX = x;
        double barY = y - 10;
        
        // 背景（赤）
        System.out.println("Health Bar Background: (" + barX + ", " + barY + ") " + 
                          barWidth + "x" + barHeight);
        
        // 現在の体力（緑）
        double healthWidth = barWidth * getHealthPercentage();
        System.out.println("Health Bar Foreground: (" + barX + ", " + barY + ") " + 
                          healthWidth + "x" + barHeight);
    }
    
    /**
     * 状態インジケーターを描画
     */
    protected void drawStateIndicator() {
        if (!isAlive || !visible) return;
        
        String stateText = state.toString();
        System.out.println("State Indicator: " + stateText + " at (" + (x + width/2) + ", " + (y - 15) + ")");
    }
    
    // === 列挙型定義 ===
    
    /**
     * キャラクタータイプ
     */
    public enum CharacterType {
        PLAYER,     // プレイヤー
        ENEMY,      // 敵
        NPC,        // ノンプレイヤーキャラクター
        BOSS        // ボス
    }
    
    /**
     * キャラクター状態
     */
    public enum CharacterState {
        IDLE,       // 待機
        MOVING,     // 移動中
        ATTACKING,  // 攻撃中
        HURT,       // ダメージを受けた
        DEAD,       // 死亡
        CASTING,    // 魔法詠唱中
        DEFENDING   // 防御中
    }
    
    /**
     * 移動方向
     */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT,
        UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }
    
    // === 便利メソッド ===
    
    /**
     * キャラクター情報を文字列で取得
     */
    public String getCharacterInfo() {
        return String.format("%s (%s) - HP: %d/%d, ATK: %d, DEF: %d, State: %s",
                           name, type, currentHealth, maxHealth, attackPower, defense, state);
    }
    
    /**
     * ステータス詳細を取得
     */
    public String getDetailedStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(name).append(" ===\n");
        sb.append("Type: ").append(type).append("\n");
        sb.append("Position: (").append(String.format("%.1f", x))
          .append(", ").append(String.format("%.1f", y)).append(")\n");
        sb.append("Health: ").append(currentHealth).append("/").append(maxHealth)
          .append(" (").append(String.format("%.1f", getHealthPercentage() * 100)).append("%)\n");
        sb.append("Attack Power: ").append(attackPower).append("\n");
        sb.append("Defense: ").append(defense).append("\n");
        sb.append("Move Speed: ").append(moveSpeed).append("\n");
        sb.append("State: ").append(state).append("\n");
        sb.append("Direction: ").append(direction).append("\n");
        sb.append("Alive: ").append(isAlive).append("\n");
        sb.append("Visible: ").append(visible).append("\n");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("GameCharacter{name='%s', type=%s, hp=%d/%d, position=(%.1f,%.1f), state=%s}",
                           name, type, currentHealth, maxHealth, x, y, state);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        GameCharacter character = (GameCharacter) obj;
        return name.equals(character.name) && type == character.type;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, type);
    }
    
    // === 静的ユーティリティメソッド ===
    
    /**
     * 2つのキャラクター間の距離を計算
     */
    public static double calculateDistance(GameCharacter char1, GameCharacter char2) {
        double dx = char1.x - char2.x;
        double dy = char1.y - char2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * キャラクターの配列から生存者のみを抽出
     */
    public static GameCharacter[] getAliveCharacters(GameCharacter[] characters) {
        return java.util.Arrays.stream(characters)
                               .filter(GameCharacter::isAlive)
                               .toArray(GameCharacter[]::new);
    }
    
    /**
     * キャラクターの配列を体力順でソート
     */
    public static void sortByHealth(GameCharacter[] characters) {
        java.util.Arrays.sort(characters, (a, b) -> 
            Integer.compare(b.currentHealth, a.currentHealth));  // 降順
    }
    
    /**
     * 指定範囲内のキャラクターを取得
     */
    public static GameCharacter[] getCharactersInRange(GameCharacter center, 
                                                      GameCharacter[] characters, 
                                                      double range) {
        return java.util.Arrays.stream(characters)
                               .filter(c -> c != center && c.isAlive())
                               .filter(c -> center.getDistanceTo(c) <= range)
                               .toArray(GameCharacter[]::new);
    }
}