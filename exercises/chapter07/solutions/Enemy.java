/**
 * 敵キャラクターを表すクラス
 * 
 * 【学習ポイント】
 * 1. 抽象クラスの具象実装
 * 2. AI行動パターンの実装
 * 3. 敵固有の機能と特殊能力
 * 4. 状態機械パターンの実装
 * 5. プレイヤーとの相互作用
 * 
 * 【実装のポイント】
 * - GameCharacterを継承
 * - AI行動パターンの実装
 * - 敵タイプ別の特殊能力
 * - プレイヤー追跡と攻撃AI
 * - ドロップアイテムシステム
 */
public class Enemy extends GameCharacter implements Drawable.Transparent {
    
    // === 敵固有の属性 ===
    private EnemyType enemyType;
    private double detectionRange;
    private double attackRange;
    private double patrolRange;
    private double alpha;  // 透明度
    
    // === AI関連 ===
    private AIState aiState;
    private GameCharacter target;
    private double originalX, originalY;  // 初期位置（パトロール用）
    private long lastAIUpdate;
    private long lastAttackTime;
    private int attackCooldown;
    
    // === 特殊能力 ===
    private boolean canFly;
    private boolean canTeleport;
    private boolean hasRangedAttack;
    private boolean hasAreaAttack;
    private int specialAbilityCooldown;
    private long lastSpecialAbilityTime;
    
    // === ドロップアイテム ===
    private java.util.List<String> dropItems;
    private double dropRate;
    
    // === パトロール関連 ===
    private java.util.List<Drawable.Position> patrolPoints;
    private int currentPatrolIndex;
    
    /**
     * 敵キャラクターのコンストラクタ
     */
    public Enemy(String name, EnemyType enemyType, double x, double y) {
        super(name, CharacterType.ENEMY, x, y, 
              getDefaultSize(enemyType), getDefaultSize(enemyType),
              getDefaultHealth(enemyType), getDefaultAttack(enemyType), 
              getDefaultDefense(enemyType), getDefaultSpeed(enemyType));
        
        this.enemyType = enemyType;
        this.originalX = x;
        this.originalY = y;
        this.alpha = 1.0;  // 完全不透明
        
        // 敵タイプに応じた初期設定
        initializeByType(enemyType);
        
        // AI初期化
        this.aiState = AIState.PATROL;
        this.target = null;
        this.lastAIUpdate = System.currentTimeMillis();
        this.lastAttackTime = 0;
        
        // パトロールポイント初期化
        this.patrolPoints = new java.util.ArrayList<>();
        this.currentPatrolIndex = 0;
        setupDefaultPatrolPoints();
        
        // ドロップアイテム初期化
        this.dropItems = new java.util.ArrayList<>();
        setupDropItems();
    }
    
    /**
     * 敵タイプ別のデフォルト値を取得
     */
    private static double getDefaultSize(EnemyType type) {
        switch (type) {
            case GOBLIN: return 24;
            case ORC: return 32;
            case SKELETON: return 28;
            case DRAGON: return 64;
            case SLIME: return 20;
            case BOSS: return 96;
            default: return 32;
        }
    }
    
    private static int getDefaultHealth(EnemyType type) {
        switch (type) {
            case GOBLIN: return 30;
            case ORC: return 60;
            case SKELETON: return 40;
            case DRAGON: return 200;
            case SLIME: return 20;
            case BOSS: return 500;
            default: return 50;
        }
    }
    
    private static int getDefaultAttack(EnemyType type) {
        switch (type) {
            case GOBLIN: return 8;
            case ORC: return 15;
            case SKELETON: return 12;
            case DRAGON: return 25;
            case SLIME: return 5;
            case BOSS: return 40;
            default: return 10;
        }
    }
    
    private static int getDefaultDefense(EnemyType type) {
        switch (type) {
            case GOBLIN: return 2;
            case ORC: return 5;
            case SKELETON: return 3;
            case DRAGON: return 10;
            case SLIME: return 1;
            case BOSS: return 15;
            default: return 3;
        }
    }
    
    private static double getDefaultSpeed(EnemyType type) {
        switch (type) {
            case GOBLIN: return 120;
            case ORC: return 80;
            case SKELETON: return 100;
            case DRAGON: return 200;
            case SLIME: return 60;
            case BOSS: return 90;
            default: return 100;
        }
    }
    
    /**
     * 敵タイプに応じた初期化
     */
    private void initializeByType(EnemyType type) {
        switch (type) {
            case GOBLIN:
                detectionRange = 100;
                attackRange = 30;
                patrolRange = 80;
                attackCooldown = 1500;
                canFly = false;
                canTeleport = false;
                hasRangedAttack = false;
                hasAreaAttack = false;
                dropRate = 0.3;
                break;
                
            case ORC:
                detectionRange = 120;
                attackRange = 35;
                patrolRange = 60;
                attackCooldown = 2000;
                canFly = false;
                canTeleport = false;
                hasRangedAttack = false;
                hasAreaAttack = true;
                dropRate = 0.4;
                break;
                
            case SKELETON:
                detectionRange = 150;
                attackRange = 80;
                patrolRange = 100;
                attackCooldown = 1000;
                canFly = false;
                canTeleport = false;
                hasRangedAttack = true;
                hasAreaAttack = false;
                dropRate = 0.25;
                break;
                
            case DRAGON:
                detectionRange = 200;
                attackRange = 60;
                patrolRange = 150;
                attackCooldown = 3000;
                canFly = true;
                canTeleport = false;
                hasRangedAttack = true;
                hasAreaAttack = true;
                dropRate = 0.8;
                break;
                
            case SLIME:
                detectionRange = 60;
                attackRange = 25;
                patrolRange = 40;
                attackCooldown = 800;
                canFly = false;
                canTeleport = false;
                hasRangedAttack = false;
                hasAreaAttack = false;
                dropRate = 0.1;
                break;
                
            case BOSS:
                detectionRange = 300;
                attackRange = 80;
                patrolRange = 200;
                attackCooldown = 4000;
                canFly = true;
                canTeleport = true;
                hasRangedAttack = true;
                hasAreaAttack = true;
                dropRate = 1.0;
                break;
        }
        
        specialAbilityCooldown = attackCooldown * 3;
        lastSpecialAbilityTime = 0;
    }
    
    /**
     * デフォルトのパトロールポイントを設定
     */
    private void setupDefaultPatrolPoints() {
        // 初期位置を中心とした四角形のパトロール
        patrolPoints.add(new Drawable.Position(originalX, originalY));
        patrolPoints.add(new Drawable.Position(originalX + patrolRange, originalY));
        patrolPoints.add(new Drawable.Position(originalX + patrolRange, originalY + patrolRange));
        patrolPoints.add(new Drawable.Position(originalX, originalY + patrolRange));
    }
    
    /**
     * ドロップアイテムを設定
     */
    private void setupDropItems() {
        switch (enemyType) {
            case GOBLIN:
                dropItems.add("Coin");
                dropItems.add("Small Potion");
                break;
            case ORC:
                dropItems.add("Coin");
                dropItems.add("Health Potion");
                dropItems.add("Iron Sword");
                break;
            case SKELETON:
                dropItems.add("Bone");
                dropItems.add("Mana Potion");
                dropItems.add("Magic Scroll");
                break;
            case DRAGON:
                dropItems.add("Dragon Scale");
                dropItems.add("Gold Coin");
                dropItems.add("Fire Crystal");
                dropItems.add("Dragon Sword");
                break;
            case SLIME:
                dropItems.add("Slime Gel");
                break;
            case BOSS:
                dropItems.add("Boss Key");
                dropItems.add("Legendary Weapon");
                dropItems.add("Rare Armor");
                dropItems.add("Skill Crystal");
                break;
        }
    }
    
    // === GameCharacter抽象メソッドの実装 ===
    
    @Override
    public void draw() {
        System.out.println("=== Enemy Drawing ===");
        System.out.println("Type: " + enemyType);
        System.out.println("Name: " + name);
        System.out.println("Position: (" + x + ", " + y + ")");
        System.out.println("HP: " + currentHealth + "/" + maxHealth);
        System.out.println("State: " + state);
        System.out.println("AI State: " + aiState);
        System.out.println("Alpha: " + String.format("%.2f", alpha));
        
        if (target != null) {
            System.out.println("Target: " + target.getName());
            System.out.println("Distance to target: " + String.format("%.1f", getDistanceTo(target)));
        }
        
        // 敵のASCII表現
        drawEnemySprite();
        
        // 特殊効果の表示
        if (canFly && Math.random() < 0.3) {
            System.out.println("(Flying)");
        }
        if (alpha < 1.0) {
            System.out.println("(Translucent)");
        }
    }
    
    /**
     * 敵のスプライトを描画
     */
    private void drawEnemySprite() {
        System.out.println(enemyType + " Sprite:");
        String[] sprite = getEnemySprite();
        for (String line : sprite) {
            System.out.println(line);
        }
    }
    
    /**
     * 敵タイプ別のスプライトを取得
     */
    private String[] getEnemySprite() {
        switch (enemyType) {
            case GOBLIN:
                return new String[] {
                    " >:( ",
                    " /|\\ ",
                    "  |  ",
                    " / \\ "
                };
            case ORC:
                return new String[] {
                    " >:D ",
                    "=/|\\=",
                    "  |  ",
                    " /_\\ "
                };
            case SKELETON:
                return new String[] {
                    "  O  ",
                    " /|\\ ",
                    "  |  ",
                    " / \\ ",
                    "(bones)"
                };
            case DRAGON:
                return new String[] {
                    "  /\\  ",
                    " (OO) ",
                    "  \\/  ",
                    "~~~~~~",
                    " FIRE "
                };
            case SLIME:
                return new String[] {
                    " ~~~ ",
                    "(blob)",
                    " ~~~ "
                };
            case BOSS:
                return new String[] {
                    " ##### ",
                    "#(O O)#",
                    "# \\_/ #",
                    " ##### ",
                    "BOSS!!!"
                };
            default:
                return new String[] {
                    " ??? ",
                    " /|\\ ",
                    "  |  ",
                    " / \\ "
                };
        }
    }
    
    @Override
    public void update(long currentTime) {
        super.lastUpdateTime = currentTime;
        
        if (!isAlive) {
            return;
        }
        
        // AI更新頻度制御（60FPSを想定して約16ms間隔）
        if (currentTime - lastAIUpdate >= 16) {
            performAI();
            lastAIUpdate = currentTime;
        }
        
        // 状態の自動変更
        if (state == CharacterState.HURT && currentTime - lastAttackTime > 300) {
            setState(CharacterState.IDLE);
        }
        
        // 透明度の変化（特殊能力）
        if (enemyType == EnemyType.BOSS && Math.random() < 0.01) {
            // ボスは稀に透明化
            alpha = Math.max(0.3, alpha - 0.1);
        } else if (alpha < 1.0) {
            // 透明度を徐々に回復
            alpha = Math.min(1.0, alpha + 0.02);
        }
    }
    
    @Override
    public void attack(GameCharacter target) {
        if (!isAlive || target == null || !target.isAlive()) return;
        if (state == CharacterState.ATTACKING) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime < attackCooldown) {
            return;  // クールダウン中
        }
        
        // 攻撃範囲チェック
        if (!isInAttackRange(target)) {
            return;
        }
        
        setState(CharacterState.ATTACKING);
        lastAttackTime = currentTime;
        
        int damage = attackPower;
        
        // 敵タイプ別の特殊攻撃
        switch (enemyType) {
            case GOBLIN:
                performGoblinAttack(target, damage);
                break;
            case ORC:
                performOrcAttack(target, damage);
                break;
            case SKELETON:
                performSkeletonAttack(target, damage);
                break;
            case DRAGON:
                performDragonAttack(target, damage);
                break;
            case SLIME:
                performSlimeAttack(target, damage);
                break;
            case BOSS:
                performBossAttack(target, damage);
                break;
            default:
                performBasicAttack(target, damage);
                break;
        }
        
        setState(CharacterState.IDLE);
    }
    
    /**
     * ゴブリンの攻撃
     */
    private void performGoblinAttack(GameCharacter target, int damage) {
        System.out.println(name + " slashes " + target.getName() + " with claws!");
        target.takeDamage(damage);
    }
    
    /**
     * オークの攻撃
     */
    private void performOrcAttack(GameCharacter target, int damage) {
        System.out.println(name + " smashes " + target.getName() + " with a club!");
        
        // エリア攻撃の場合
        if (hasAreaAttack && Math.random() < 0.3) {
            System.out.println("Area attack! Shockwave spreads!");
            damage = (int)(damage * 1.2);
        }
        
        target.takeDamage(damage);
    }
    
    /**
     * スケルトンの攻撃
     */
    private void performSkeletonAttack(GameCharacter target, int damage) {
        if (hasRangedAttack && getDistanceTo(target) > 40) {
            System.out.println(name + " shoots a bone arrow at " + target.getName() + "!");
        } else {
            System.out.println(name + " strikes " + target.getName() + " with a bone sword!");
        }
        target.takeDamage(damage);
    }
    
    /**
     * ドラゴンの攻撃
     */
    private void performDragonAttack(GameCharacter target, int damage) {
        double random = Math.random();
        
        if (random < 0.4) {
            System.out.println(name + " breathes fire at " + target.getName() + "!");
            damage = (int)(damage * 1.5);
        } else if (random < 0.7) {
            System.out.println(name + " claws " + target.getName() + "!");
        } else {
            System.out.println(name + " tail whips " + target.getName() + "!");
            damage = (int)(damage * 0.8);
        }
        
        target.takeDamage(damage);
    }
    
    /**
     * スライムの攻撃
     */
    private void performSlimeAttack(GameCharacter target, int damage) {
        System.out.println(name + " bounces into " + target.getName() + "!");
        
        // スライムは分裂する場合がある
        if (Math.random() < 0.1) {
            System.out.println(name + " splits after the attack!");
        }
        
        target.takeDamage(damage);
    }
    
    /**
     * ボスの攻撃
     */
    private void performBossAttack(GameCharacter target, int damage) {
        double random = Math.random();
        
        if (random < 0.2) {
            System.out.println(name + " uses ULTIMATE ATTACK on " + target.getName() + "!");
            damage *= 3;
        } else if (random < 0.4) {
            System.out.println(name + " casts a powerful spell at " + target.getName() + "!");
            damage = (int)(damage * 2);
        } else if (random < 0.6) {
            System.out.println(name + " performs an area devastation attack!");
            damage = (int)(damage * 1.5);
        } else {
            System.out.println(name + " strikes " + target.getName() + " with massive force!");
        }
        
        target.takeDamage(damage);
    }
    
    /**
     * 基本攻撃
     */
    private void performBasicAttack(GameCharacter target, int damage) {
        System.out.println(name + " attacks " + target.getName() + "!");
        target.takeDamage(damage);
    }
    
    @Override
    protected int calculateDamage(GameCharacter attacker, int baseDamage) {
        int damage = baseDamage;
        
        // 敵タイプ別の防御特性
        switch (enemyType) {
            case SKELETON:
                // スケルトンは物理攻撃に強い
                if (Math.random() < 0.2) {
                    damage /= 2;
                    System.out.println(name + " resists physical damage!");
                }
                break;
            case SLIME:
                // スライムは攻撃を吸収することがある
                if (Math.random() < 0.1) {
                    heal(damage / 4);
                    System.out.println(name + " absorbs some damage!");
                }
                break;
            case DRAGON:
                // ドラゴンは火属性攻撃に免疫
                System.out.println(name + " has thick scales!");
                break;
        }
        
        return Math.max(1, damage - defense);
    }
    
    @Override
    public void performAI() {
        if (!isAlive) return;
        
        long currentTime = System.currentTimeMillis();
        
        // ターゲットの有効性チェック
        if (target != null && (!target.isAlive() || getDistanceTo(target) > detectionRange * 2)) {
            target = null;
            aiState = AIState.PATROL;
        }
        
        // AI状態に応じた行動
        switch (aiState) {
            case PATROL:
                performPatrol();
                searchForTarget();
                break;
                
            case CHASE:
                performChase();
                break;
                
            case ATTACK:
                performAttackAI();
                break;
                
            case RETREAT:
                performRetreat();
                break;
                
            case GUARD:
                performGuard();
                break;
        }
        
        // 特殊能力の使用
        if (currentTime - lastSpecialAbilityTime > specialAbilityCooldown) {
            if (target != null && Math.random() < 0.1) {
                useSpecialAbility();
                lastSpecialAbilityTime = currentTime;
            }
        }
    }
    
    /**
     * パトロールAI
     */
    private void performPatrol() {
        if (patrolPoints.isEmpty()) return;
        
        Drawable.Position targetPoint = patrolPoints.get(currentPatrolIndex);
        double distance = Math.sqrt(Math.pow(targetPoint.getX() - x, 2) + Math.pow(targetPoint.getY() - y, 2));
        
        if (distance < 10) {
            // 目標ポイントに到達
            currentPatrolIndex = (currentPatrolIndex + 1) % patrolPoints.size();
            setState(CharacterState.IDLE);
        } else {
            // 目標ポイントに向かって移動
            moveTowards(targetPoint.getX(), targetPoint.getY(), 0.016);  // 60FPS想定
        }
    }
    
    /**
     * ターゲット検索
     */
    private void searchForTarget() {
        // 実際のゲームでは、プレイヤーやその他のキャラクターを検索
        // ここではシミュレーション
        if (Math.random() < 0.01) {  // 1%の確率でターゲット発見
            // ダミーターゲットの設定
            System.out.println(name + " detects an enemy!");
            aiState = AIState.CHASE;
        }
    }
    
    /**
     * 追跡AI
     */
    private void performChase() {
        if (target == null) {
            aiState = AIState.PATROL;
            return;
        }
        
        double distance = getDistanceTo(target);
        
        if (distance <= attackRange) {
            aiState = AIState.ATTACK;
        } else if (distance > detectionRange) {
            target = null;
            aiState = AIState.PATROL;
        } else {
            // ターゲットに向かって移動
            moveTowards(target.getPosition().getX(), target.getPosition().getY(), 0.016);
        }
    }
    
    /**
     * 攻撃AI
     */
    private void performAttackAI() {
        if (target == null) {
            aiState = AIState.PATROL;
            return;
        }
        
        double distance = getDistanceTo(target);
        
        if (distance > attackRange) {
            aiState = AIState.CHASE;
        } else {
            // 攻撃実行
            attack(target);
            
            // 体力が少ない場合は逃走を検討
            if (getHealthPercentage() < 0.2 && Math.random() < 0.3) {
                aiState = AIState.RETREAT;
            }
        }
    }
    
    /**
     * 逃走AI
     */
    private void performRetreat() {
        if (target == null) {
            aiState = AIState.PATROL;
            return;
        }
        
        // ターゲットから離れる方向に移動
        double dx = x - target.getPosition().getX();
        double dy = y - target.getPosition().getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            dx /= distance;
            dy /= distance;
            
            moveTowards(x + dx * 100, y + dy * 100, 0.016);
        }
        
        // 十分距離を取ったら通常状態に戻る
        if (getDistanceTo(target) > detectionRange) {
            aiState = AIState.PATROL;
        }
    }
    
    /**
     * 警戒AI
     */
    private void performGuard() {
        // 初期位置周辺を警戒
        double distanceFromOrigin = Math.sqrt(Math.pow(x - originalX, 2) + Math.pow(y - originalY, 2));
        
        if (distanceFromOrigin > patrolRange) {
            // 初期位置に戻る
            moveTowards(originalX, originalY, 0.016);
        }
        
        // ターゲット検索
        searchForTarget();
    }
    
    @Override
    public void useSpecialSkill() {
        System.out.println(name + " uses special ability!");
        
        switch (enemyType) {
            case GOBLIN:
                // 素早い連続攻撃
                System.out.println(name + " performs rapid strikes!");
                break;
                
            case ORC:
                // 怒り状態（攻撃力上昇）
                System.out.println(name + " enters rage mode!");
                attackPower = (int)(attackPower * 1.5);
                break;
                
            case SKELETON:
                // 骨を召喚
                System.out.println(name + " summons bone minions!");
                break;
                
            case DRAGON:
                // 飛行状態
                if (canFly) {
                    System.out.println(name + " takes flight!");
                    moveSpeed *= 1.5;
                }
                break;
                
            case SLIME:
                // 分裂
                System.out.println(name + " splits into smaller slimes!");
                break;
                
            case BOSS:
                // テレポート
                if (canTeleport) {
                    System.out.println(name + " teleports!");
                    teleport();
                }
                break;
        }
    }
    
    /**
     * テレポート能力
     */
    private void teleport() {
        if (target != null) {
            // ターゲットの背後にテレポート
            double angle = Math.random() * 2 * Math.PI;
            double distance = 50;
            
            double newX = target.getPosition().getX() + Math.cos(angle) * distance;
            double newY = target.getPosition().getY() + Math.sin(angle) * distance;
            
            moveTo(newX, newY);
            alpha = 0.5;  // 一時的に半透明
        }
    }
    
    // === Transparent インターフェイスの実装 ===
    
    @Override
    public double getAlpha() {
        return alpha;
    }
    
    @Override
    public void setAlpha(double alpha) {
        this.alpha = Math.max(0.0, Math.min(1.0, alpha));
    }
    
    // === ドロップシステム ===
    
    /**
     * アイテムをドロップ
     */
    public java.util.List<String> dropItems() {
        java.util.List<String> dropped = new java.util.ArrayList<>();
        
        for (String item : dropItems) {
            if (Math.random() < dropRate) {
                dropped.add(item);
            }
        }
        
        // 確実にコインはドロップ
        if (!dropped.contains("Coin") && Math.random() < 0.8) {
            dropped.add("Coin");
        }
        
        return dropped;
    }
    
    // === 覆回りメソッド ===
    
    @Override
    protected void onTakeDamage(int damage) {
        super.onTakeDamage(damage);
        
        // ダメージを受けたらターゲットに設定
        if (target == null && aiState == AIState.PATROL) {
            // 実際のゲームでは攻撃者をターゲットに設定
            aiState = AIState.CHASE;
        }
    }
    
    @Override
    protected void onDeath() {
        super.onDeath();
        
        System.out.println(name + " has been defeated!");
        
        // アイテムドロップ
        java.util.List<String> dropped = dropItems();
        if (!dropped.isEmpty()) {
            System.out.println("Items dropped: " + dropped);
        }
        
        // 経験値の報告（実際のゲームではプレイヤーに経験値を与える）
        int expReward = maxHealth / 2;
        System.out.println("Experience reward: " + expReward);
    }
    
    // === セッター ===
    
    public void setTarget(GameCharacter target) {
        this.target = target;
        if (target != null) {
            aiState = AIState.CHASE;
        }
    }
    
    public void addPatrolPoint(double x, double y) {
        patrolPoints.add(new Drawable.Position(x, y));
    }
    
    public void clearPatrolPoints() {
        patrolPoints.clear();
        currentPatrolIndex = 0;
    }
    
    // === ゲッター ===
    
    public EnemyType getEnemyType() { return enemyType; }
    public AIState getAIState() { return aiState; }
    public GameCharacter getTarget() { return target; }
    public double getDetectionRange() { return detectionRange; }
    public double getAttackRange() { return attackRange; }
    public java.util.List<String> getDropItems() { return new java.util.ArrayList<>(dropItems); }
    public double getDropRate() { return dropRate; }
    
    public boolean canFly() { return canFly; }
    public boolean canTeleport() { return canTeleport; }
    public boolean hasRangedAttack() { return hasRangedAttack; }
    public boolean hasAreaAttack() { return hasAreaAttack; }
    
    /**
     * 敵の詳細情報を取得
     */
    public String getEnemyInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ENEMY STATUS ===\n");
        sb.append("Type: ").append(enemyType).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Health: ").append(currentHealth).append("/").append(maxHealth).append("\n");
        sb.append("Attack Power: ").append(attackPower).append("\n");
        sb.append("Defense: ").append(defense).append("\n");
        sb.append("Move Speed: ").append(moveSpeed).append("\n");
        sb.append("AI State: ").append(aiState).append("\n");
        sb.append("Position: (").append(String.format("%.1f", x)).append(", ").append(String.format("%.1f", y)).append(")\n");
        sb.append("Detection Range: ").append(detectionRange).append("\n");
        sb.append("Attack Range: ").append(attackRange).append("\n");
        sb.append("Drop Rate: ").append(String.format("%.1f", dropRate * 100)).append("%\n");
        sb.append("Alpha: ").append(String.format("%.2f", alpha)).append("\n");
        sb.append("Abilities: ");
        if (canFly) sb.append("Fly ");
        if (canTeleport) sb.append("Teleport ");
        if (hasRangedAttack) sb.append("Ranged ");
        if (hasAreaAttack) sb.append("Area ");
        sb.append("\n");
        if (target != null) {
            sb.append("Target: ").append(target.getName()).append("\n");
        }
        return sb.toString();
    }
    
    // === 列挙型定義 ===
    
    /**
     * 敵のタイプ
     */
    public enum EnemyType {
        GOBLIN,     // ゴブリン
        ORC,        // オーク
        SKELETON,   // スケルトン
        DRAGON,     // ドラゴン
        SLIME,      // スライム
        BOSS        // ボス
    }
    
    /**
     * AI状態
     */
    public enum AIState {
        PATROL,     // パトロール
        CHASE,      // 追跡
        ATTACK,     // 攻撃
        RETREAT,    // 逃走
        GUARD       // 警戒
    }
    
    @Override
    public String toString() {
        return String.format("Enemy{type=%s, name='%s', hp=%d/%d, aiState=%s, position=(%.1f,%.1f)}",
                           enemyType, name, currentHealth, maxHealth, aiState, x, y);
    }
}