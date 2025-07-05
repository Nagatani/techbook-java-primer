/**
 * プレイヤーキャラクターを表すクラス
 * 
 * 【学習ポイント】
 * 1. 抽象クラスの具象実装
 * 2. インターフェイスの多重実装
 * 3. プレイヤー固有の機能実装
 * 4. 入力処理とゲーム状態の管理
 * 5. 経験値・レベルシステムの実装
 * 
 * 【実装のポイント】
 * - GameCharacterを継承
 * - プレイヤー固有のスキルとアビリティ
 * - 入力コマンドの処理
 * - インベントリとアイテム管理
 * - 経験値とレベルアップシステム
 */
public class Player extends GameCharacter implements Drawable.Resizable, Drawable.Rotatable {
    
    // === プレイヤー固有の属性 ===
    private int level;
    private int experience;
    private int experienceToNextLevel;
    private int mana;
    private int maxMana;
    private int skillPoints;
    private double rotation;
    
    // === スキルとアビリティ ===
    private boolean hasDoubleJump;
    private boolean hasFireball;
    private boolean hasShield;
    private int comboCount;
    private long lastAttackTime;
    
    // === インベントリ ===
    private java.util.List<String> inventory;
    private String equippedWeapon;
    private String equippedArmor;
    
    // === 入力状態 ===
    private boolean[] keysPressed;
    private static final int MAX_KEYS = 256;
    
    /**
     * プレイヤーのコンストラクタ
     */
    public Player(String name, double x, double y) {
        super(name, CharacterType.PLAYER, x, y, 32, 32, 100, 20, 5, 150.0);
        
        // プレイヤー固有の初期化
        this.level = 1;
        this.experience = 0;
        this.experienceToNextLevel = 100;
        this.maxMana = 50;
        this.mana = maxMana;
        this.skillPoints = 0;
        this.rotation = 0.0;
        
        // スキル初期化
        this.hasDoubleJump = false;
        this.hasFireball = false;
        this.hasShield = false;
        this.comboCount = 0;
        this.lastAttackTime = 0;
        
        // インベントリ初期化
        this.inventory = new java.util.ArrayList<>();
        this.equippedWeapon = "Basic Sword";
        this.equippedArmor = "Cloth Armor";
        
        // 入力状態初期化
        this.keysPressed = new boolean[MAX_KEYS];
        
        // 初期アイテム追加
        inventory.add("Health Potion");
        inventory.add("Mana Potion");
        
        // プレイヤーは常に最前面
        setZOrder(1000);
    }
    
    // === GameCharacter抽象メソッドの実装 ===
    
    @Override
    public void draw() {
        System.out.println("=== Player Drawing ===");
        System.out.println("Name: " + name);
        System.out.println("Position: (" + x + ", " + y + ")");
        System.out.println("Level: " + level + " (EXP: " + experience + "/" + experienceToNextLevel + ")");
        System.out.println("HP: " + currentHealth + "/" + maxHealth);
        System.out.println("MP: " + mana + "/" + maxMana);
        System.out.println("Equipment: " + equippedWeapon + " / " + equippedArmor);
        System.out.println("Direction: " + direction);
        System.out.println("Rotation: " + String.format("%.1f", rotation) + "°");
        
        // プレイヤーのASCII表現
        drawPlayerSprite();
        
        // スキル効果の表示
        if (hasShield && state == CharacterState.DEFENDING) {
            drawShieldEffect();
        }
    }
    
    /**
     * プレイヤーのスプライトを描画
     */
    private void drawPlayerSprite() {
        System.out.println("Player Sprite:");
        String[] sprite = getPlayerSprite();
        for (String line : sprite) {
            System.out.println(line);
        }
    }
    
    /**
     * プレイヤーのスプライトを取得
     */
    private String[] getPlayerSprite() {
        switch (direction) {
            case RIGHT:
                return new String[] {
                    "  O  ",
                    " /|\\ ",
                    "  |  ",
                    " / \\ "
                };
            case LEFT:
                return new String[] {
                    "  O  ",
                    " /|\\ ",
                    "  |  ",
                    " / \\ "
                };
            case UP:
                return new String[] {
                    "  O  ",
                    " \\|/ ",
                    "  |  ",
                    " / \\ "
                };
            case DOWN:
                return new String[] {
                    "  O  ",
                    " /|\\ ",
                    "  |  ",
                    " /_\\ "
                };
            default:
                return new String[] {
                    "  O  ",
                    " /|\\ ",
                    "  |  ",
                    " / \\ "
                };
        }
    }
    
    /**
     * シールド効果を描画
     */
    private void drawShieldEffect() {
        System.out.println("Shield Effect:");
        System.out.println("  +++  ");
        System.out.println(" +   + ");
        System.out.println("+  O  +");
        System.out.println(" + | + ");
        System.out.println("  +++  ");
    }
    
    @Override
    public void update(long currentTime) {
        super.lastUpdateTime = currentTime;
        
        // 状態の自動変更
        if (state == CharacterState.HURT && currentTime - lastAttackTime > 500) {
            setState(CharacterState.IDLE);
        }
        
        // マナの自動回復
        if (mana < maxMana && currentTime % 1000 < 50) {  // 1秒ごと
            mana = Math.min(maxMana, mana + 2);
        }
        
        // コンボのリセット
        if (currentTime - lastAttackTime > 2000) {  // 2秒でコンボリセット
            comboCount = 0;
        }
        
        // 入力処理
        processInput();
        
        // レベルアップチェック
        checkLevelUp();
    }
    
    /**
     * 入力処理
     */
    private void processInput() {
        // 実際のゲームでは、キーボード入力を処理
        // ここではシミュレーション
        
        // 移動処理の例
        if (isKeyPressed('W')) {
            moveBy(0, -moveSpeed * 0.016);  // 60FPSを想定
        }
        if (isKeyPressed('S')) {
            moveBy(0, moveSpeed * 0.016);
        }
        if (isKeyPressed('A')) {
            moveBy(-moveSpeed * 0.016, 0);
        }
        if (isKeyPressed('D')) {
            moveBy(moveSpeed * 0.016, 0);
        }
        
        // スキル使用
        if (isKeyPressed('1')) {
            useHealthPotion();
        }
        if (isKeyPressed('2')) {
            useManaPotion();
        }
        if (isKeyPressed(' ')) {
            useSpecialSkill();
        }
    }
    
    /**
     * キーが押されているかを判定
     */
    private boolean isKeyPressed(char key) {
        return key < MAX_KEYS && keysPressed[key];
    }
    
    /**
     * キーの状態を設定
     */
    public void setKeyPressed(char key, boolean pressed) {
        if (key < MAX_KEYS) {
            keysPressed[key] = pressed;
        }
    }
    
    @Override
    public void attack(GameCharacter target) {
        if (!isAlive || target == null || !target.isAlive()) return;
        if (state == CharacterState.ATTACKING) return;
        
        // 攻撃範囲チェック
        if (!isInAttackRange(target)) {
            System.out.println(name + " is too far to attack " + target.getName());
            return;
        }
        
        setState(CharacterState.ATTACKING);
        lastAttackTime = System.currentTimeMillis();
        
        // コンボ攻撃
        comboCount++;
        int damage = attackPower;
        
        // コンボボーナス
        if (comboCount > 1) {
            damage += comboCount * 2;
            System.out.println("Combo x" + comboCount + "! Bonus damage!");
        }
        
        // 武器ボーナス
        damage += getWeaponBonus();
        
        // クリティカルヒット判定
        if (Math.random() < 0.1) {  // 10%の確率
            damage *= 2;
            System.out.println("Critical Hit!");
        }
        
        System.out.println(name + " attacks " + target.getName() + " for " + damage + " damage!");
        target.takeDamage(damage);
        
        // 経験値獲得
        if (!target.isAlive()) {
            gainExperience(target.getMaxHealth() / 2);
        }
        
        // 状態をIDLEに戻す
        setState(CharacterState.IDLE);
    }
    
    /**
     * 武器ボーナスを取得
     */
    private int getWeaponBonus() {
        switch (equippedWeapon) {
            case "Basic Sword": return 0;
            case "Iron Sword": return 5;
            case "Steel Sword": return 10;
            case "Magic Sword": return 15;
            default: return 0;
        }
    }
    
    @Override
    protected int calculateDamage(GameCharacter attacker, int baseDamage) {
        int damage = baseDamage;
        
        // 防具ボーナス
        damage -= getArmorBonus();
        
        // 防御スキル
        if (hasShield && state == CharacterState.DEFENDING) {
            damage = damage / 2;
            System.out.println(name + " blocks with shield! Damage reduced!");
        }
        
        return Math.max(1, damage);
    }
    
    /**
     * 防具ボーナスを取得
     */
    private int getArmorBonus() {
        switch (equippedArmor) {
            case "Cloth Armor": return 0;
            case "Leather Armor": return 2;
            case "Chain Mail": return 5;
            case "Plate Armor": return 10;
            default: return 0;
        }
    }
    
    @Override
    public void performAI() {
        // プレイヤーにはAI処理は不要
        // 代わりにオートパイロット機能を実装
        if (getCurrentHealth() < getMaxHealth() * 0.2) {
            // 体力が20%以下の場合、自動で回復アイテムを使用
            useHealthPotion();
        }
    }
    
    @Override
    public void useSpecialSkill() {
        System.out.println("=== Special Skill ===");
        
        // レベルに応じてスキルを変更
        if (level >= 5 && hasFireball && mana >= 20) {
            castFireball();
        } else if (level >= 3 && hasDoubleJump) {
            performDoubleJump();
        } else {
            performBasicSkill();
        }
    }
    
    /**
     * ファイアボール呪文
     */
    private void castFireball() {
        if (mana < 20) {
            System.out.println("Not enough mana!");
            return;
        }
        
        mana -= 20;
        setState(CharacterState.CASTING);
        
        System.out.println(name + " casts Fireball!");
        
        // 範囲内の敵すべてにダメージ
        System.out.println("Fireball explodes in a 3x3 area!");
        
        // 実際のゲームでは、範囲内の敵を取得してダメージを与える
        System.out.println("Fireball deals " + (attackPower * 2) + " magic damage!");
        
        setState(CharacterState.IDLE);
    }
    
    /**
     * ダブルジャンプ
     */
    private void performDoubleJump() {
        System.out.println(name + " performs a double jump!");
        
        // 一時的に移動速度を向上
        double oldSpeed = moveSpeed;
        moveSpeed *= 1.5;
        
        // 1秒後に元に戻す（実際のゲームではタイマーを使用）
        System.out.println("Movement speed temporarily increased!");
        
        // ここでは即座に元に戻す
        moveSpeed = oldSpeed;
    }
    
    /**
     * 基本スキル
     */
    private void performBasicSkill() {
        System.out.println(name + " performs a basic skill attack!");
        
        // 基本攻撃の1.5倍のダメージ
        int skillDamage = (int)(attackPower * 1.5);
        System.out.println("Skill damage: " + skillDamage);
    }
    
    // === アイテム使用 ===
    
    /**
     * 体力ポーション使用
     */
    public void useHealthPotion() {
        if (inventory.contains("Health Potion")) {
            int healAmount = 30;
            heal(healAmount);
            inventory.remove("Health Potion");
            System.out.println(name + " uses Health Potion and recovers " + healAmount + " HP!");
        } else {
            System.out.println("No Health Potion available!");
        }
    }
    
    /**
     * マナポーション使用
     */
    public void useManaPotion() {
        if (inventory.contains("Mana Potion")) {
            int manaAmount = 25;
            mana = Math.min(maxMana, mana + manaAmount);
            inventory.remove("Mana Potion");
            System.out.println(name + " uses Mana Potion and recovers " + manaAmount + " MP!");
        } else {
            System.out.println("No Mana Potion available!");
        }
    }
    
    // === 経験値・レベルシステム ===
    
    /**
     * 経験値を獲得
     */
    public void gainExperience(int exp) {
        experience += exp;
        System.out.println(name + " gains " + exp + " experience points!");
        
        checkLevelUp();
    }
    
    /**
     * レベルアップチェック
     */
    private void checkLevelUp() {
        while (experience >= experienceToNextLevel) {
            levelUp();
        }
    }
    
    /**
     * レベルアップ処理
     */
    private void levelUp() {
        level++;
        experience -= experienceToNextLevel;
        experienceToNextLevel = (int)(experienceToNextLevel * 1.2);  // 20%増加
        
        // ステータス向上
        maxHealth += 10;
        currentHealth = maxHealth;  // 全回復
        maxMana += 5;
        mana = maxMana;  // 全回復
        attackPower += 3;
        defense += 2;
        skillPoints += 2;
        
        System.out.println("*** LEVEL UP! ***");
        System.out.println(name + " reached level " + level + "!");
        System.out.println("Health: " + maxHealth + " (+10)");
        System.out.println("Mana: " + maxMana + " (+5)");
        System.out.println("Attack: " + attackPower + " (+3)");
        System.out.println("Defense: " + defense + " (+2)");
        System.out.println("Skill Points: " + skillPoints + " (+2)");
        
        // 新しいスキル獲得
        unlockNewSkills();
    }
    
    /**
     * 新しいスキルのアンロック
     */
    private void unlockNewSkills() {
        if (level == 3 && !hasDoubleJump) {
            hasDoubleJump = true;
            System.out.println("New skill unlocked: Double Jump!");
        }
        if (level == 5 && !hasFireball) {
            hasFireball = true;
            System.out.println("New skill unlocked: Fireball!");
        }
        if (level == 7 && !hasShield) {
            hasShield = true;
            System.out.println("New skill unlocked: Shield Block!");
        }
    }
    
    // === 装備システム ===
    
    /**
     * 武器を装備
     */
    public void equipWeapon(String weapon) {
        this.equippedWeapon = weapon;
        System.out.println(name + " equipped " + weapon);
        
        // 攻撃力の再計算
        updateStats();
    }
    
    /**
     * 防具を装備
     */
    public void equipArmor(String armor) {
        this.equippedArmor = armor;
        System.out.println(name + " equipped " + armor);
        
        // 防御力の再計算
        updateStats();
    }
    
    /**
     * ステータスを更新
     */
    private void updateStats() {
        // 基本攻撃力に装備ボーナスを適用
        // 実際のゲームでは、基本値と装備ボーナスを分けて管理
        System.out.println("Stats updated based on equipment");
    }
    
    // === インベントリ管理 ===
    
    /**
     * アイテムを追加
     */
    public void addItem(String item) {
        inventory.add(item);
        System.out.println(name + " obtained " + item);
    }
    
    /**
     * アイテムを使用
     */
    public boolean useItem(String item) {
        if (inventory.contains(item)) {
            inventory.remove(item);
            System.out.println(name + " used " + item);
            return true;
        }
        return false;
    }
    
    /**
     * インベントリを表示
     */
    public void showInventory() {
        System.out.println("=== " + name + "'s Inventory ===");
        if (inventory.isEmpty()) {
            System.out.println("Empty");
        } else {
            for (int i = 0; i < inventory.size(); i++) {
                System.out.println((i + 1) + ". " + inventory.get(i));
            }
        }
        System.out.println("Weapon: " + equippedWeapon);
        System.out.println("Armor: " + equippedArmor);
    }
    
    // === Resizable インターフェイスの実装 ===
    
    @Override
    public void resize(double width, double height) {
        this.width = width;
        this.height = height;
        System.out.println(name + " resized to " + width + "x" + height);
    }
    
    // === Rotatable インターフェイスの実装 ===
    
    @Override
    public double getRotation() {
        return rotation;
    }
    
    @Override
    public void setRotation(double degrees) {
        this.rotation = degrees % 360;
        if (this.rotation < 0) {
            this.rotation += 360;
        }
    }
    
    // === 特殊行動 ===
    
    /**
     * 防御モードに入る
     */
    public void defend() {
        setState(CharacterState.DEFENDING);
        System.out.println(name + " enters defensive stance!");
    }
    
    /**
     * 防御モードを解除
     */
    public void stopDefending() {
        if (state == CharacterState.DEFENDING) {
            setState(CharacterState.IDLE);
            System.out.println(name + " exits defensive stance!");
        }
    }
    
    /**
     * 逃走処理
     */
    public void flee() {
        setState(CharacterState.MOVING);
        moveSpeed *= 1.5;  // 一時的に移動速度向上
        System.out.println(name + " flees from battle!");
        
        // 一定時間後に元に戻す（実際のゲームではタイマーを使用）
        moveSpeed /= 1.5;
        setState(CharacterState.IDLE);
    }
    
    // === オーバーライドメソッド ===
    
    @Override
    protected void onTakeDamage(int damage) {
        super.onTakeDamage(damage);
        
        // プレイヤー固有のダメージ処理
        if (comboCount > 0) {
            comboCount = 0;  // ダメージを受けたらコンボリセット
        }
        
        // 重傷時の警告
        if (isCriticallyInjured()) {
            System.out.println("*** WARNING: " + name + " is critically injured! ***");
        }
    }
    
    @Override
    protected void onHeal(int amount) {
        super.onHeal(amount);
        System.out.println(name + " feels better!");
    }
    
    @Override
    protected void onDeath() {
        super.onDeath();
        System.out.println("*** GAME OVER ***");
        System.out.println(name + " has fallen in battle...");
        
        // 死亡時の処理
        comboCount = 0;
        mana = 0;
        setState(CharacterState.DEAD);
    }
    
    // === ゲッター・セッター ===
    
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public int getExperienceToNextLevel() { return experienceToNextLevel; }
    public int getMana() { return mana; }
    public int getMaxMana() { return maxMana; }
    public int getSkillPoints() { return skillPoints; }
    public int getComboCount() { return comboCount; }
    public String getEquippedWeapon() { return equippedWeapon; }
    public String getEquippedArmor() { return equippedArmor; }
    
    public boolean hasDoubleJump() { return hasDoubleJump; }
    public boolean hasFireball() { return hasFireball; }
    public boolean hasShield() { return hasShield; }
    
    public java.util.List<String> getInventory() { 
        return new java.util.ArrayList<>(inventory); 
    }
    
    /**
     * プレイヤーの完全な情報を取得
     */
    public String getPlayerInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PLAYER STATUS ===\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Level: ").append(level).append("\n");
        sb.append("Experience: ").append(experience).append("/").append(experienceToNextLevel).append("\n");
        sb.append("Health: ").append(currentHealth).append("/").append(maxHealth).append("\n");
        sb.append("Mana: ").append(mana).append("/").append(maxMana).append("\n");
        sb.append("Attack Power: ").append(attackPower).append("\n");
        sb.append("Defense: ").append(defense).append("\n");
        sb.append("Move Speed: ").append(moveSpeed).append("\n");
        sb.append("Skill Points: ").append(skillPoints).append("\n");
        sb.append("Combo Count: ").append(comboCount).append("\n");
        sb.append("Position: (").append(String.format("%.1f", x)).append(", ").append(String.format("%.1f", y)).append(")\n");
        sb.append("Rotation: ").append(String.format("%.1f", rotation)).append("°\n");
        sb.append("State: ").append(state).append("\n");
        sb.append("Direction: ").append(direction).append("\n");
        sb.append("Equipped Weapon: ").append(equippedWeapon).append("\n");
        sb.append("Equipped Armor: ").append(equippedArmor).append("\n");
        sb.append("Skills: ");
        if (hasDoubleJump) sb.append("DoubleJump ");
        if (hasFireball) sb.append("Fireball ");
        if (hasShield) sb.append("Shield ");
        sb.append("\n");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("Player{name='%s', level=%d, hp=%d/%d, mp=%d/%d, exp=%d/%d, position=(%.1f,%.1f)}",
                           name, level, currentHealth, maxHealth, mana, maxMana, 
                           experience, experienceToNextLevel, x, y);
    }
}