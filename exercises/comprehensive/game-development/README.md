# 総合課題3: ゲーム開発プロジェクト

## 📋 プロジェクト概要

2Dアクションゲームまたはパズルゲームを開発します。リアルタイム処理、物理演算、アニメーション、サウンド等、ゲーム開発特有の技術を習得し、エンターテインメント分野での開発スキルを身につけます。

## 🎯 学習目標

- リアルタイムゲームループの実装
- 物理演算とアニメーション技術
- イベント駆動アーキテクチャの実践
- パフォーマンス最適化技術
- ユーザーエクスペリエンス設計

## 🎮 ゲームオプション

### オプション1: 2Dプラットフォーマー「Super Java Adventure」
**ジャンル**: アクション・プラットフォーマー  
**技術レベル**: ★★★★★  
**推定開発期間**: 4-5週間

#### ゲーム概要
- 横スクロール2Dアクションゲーム
- ジャンプ、移動、敵との戦闘
- アイテム収集とスコアシステム
- 複数ステージとボス戦

### オプション2: パズルゲーム「Java Tetris Enhanced」
**ジャンル**: パズル  
**技術レベル**: ★★★★☆  
**推定開発期間**: 3-4週間

#### ゲーム概要
- テトリス風落ち物パズル
- 特殊ブロックとコンボシステム
- レベル進行とスピード調整
- ハイスコア機能

### オプション3: タワーディフェンス「Code Defenders」
**ジャンル**: ストラテジー  
**技術レベル**: ★★★★★  
**推定開発期間**: 4-5週間

#### ゲーム概要
- 敵の侵入を防ぐタワーディフェンス
- 複数タイプのタワーと敵
- リソース管理とアップグレード
- ウェーブシステム

## 🏗 アーキテクチャ設計（2Dプラットフォーマー例）

### パッケージ構成

```
game.adventure/
├── core/                      # ゲームエンジン
│   ├── GameEngine.java       # メインループ
│   ├── Scene.java            # シーン管理
│   ├── GameObject.java       # ゲームオブジェクト基底
│   └── Component.java        # コンポーネントシステム
├── graphics/                 # 描画システム
│   ├── Renderer.java         # レンダラー
│   ├── Sprite.java           # スプライト
│   ├── Animation.java        # アニメーション
│   └── Camera.java           # カメラ制御
├── physics/                  # 物理演算
│   ├── PhysicsWorld.java     # 物理世界
│   ├── Collider.java         # 衝突判定
│   ├── RigidBody.java        # 剛体
│   └── Vector2D.java         # 2Dベクトル
├── input/                    # 入力処理
│   ├── InputManager.java     # 入力管理
│   ├── KeyboardInput.java    # キーボード
│   └── MouseInput.java       # マウス
├── audio/                    # オーディオ
│   ├── AudioManager.java     # 音声管理
│   ├── SoundEffect.java      # 効果音
│   └── BackgroundMusic.java  # BGM
├── entities/                 # ゲームエンティティ
│   ├── Player.java           # プレイヤー
│   ├── Enemy.java            # 敵
│   ├── Platform.java         # 足場
│   └── Item.java             # アイテム
├── levels/                   # レベル管理
│   ├── Level.java            # レベル
│   ├── LevelLoader.java      # レベルローダー
│   └── Tilemap.java          # タイルマップ
├── ui/                       # ユーザーインターフェイス
│   ├── GameUI.java           # ゲームUI
│   ├── MenuSystem.java       # メニュー
│   └── HUD.java              # ヘッドアップディスプレイ
└── states/                   # ゲーム状態
    ├── GameState.java        # 状態基底クラス
    ├── MenuState.java        # メニュー状態
    ├── PlayState.java        # プレイ状態
    └── GameOverState.java    # ゲームオーバー
```

### コアクラス設計

#### GameEngineクラス（メインループ）
```java
public class GameEngine implements Runnable {
    private static final int TARGET_FPS = 60;
    private static final long FRAME_TIME = 1000000000L / TARGET_FPS;
    
    private boolean running = false;
    private GameState currentState;
    private Renderer renderer;
    private InputManager inputManager;
    private AudioManager audioManager;
    
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0.0;
        int frames = 0;
        int updates = 0;
        
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / FRAME_TIME;
            lastTime = now;
            
            while (delta >= 1.0) {
                update();
                updates++;
                delta--;
            }
            
            render();
            frames++;
            
            // FPS計測
            if (System.currentTimeMillis() - timer > 1000) {
                System.out.println("FPS: " + frames + ", UPS: " + updates);
                frames = 0;
                updates = 0;
                timer += 1000;
            }
        }
    }
    
    private void update() {
        inputManager.update();
        currentState.update();
        audioManager.update();
    }
    
    private void render() {
        renderer.clear();
        currentState.render(renderer);
        renderer.present();
    }
}
```

#### GameObjectクラス（エンティティ基底）
```java
public abstract class GameObject {
    protected Vector2D position;
    protected Vector2D velocity;
    protected Sprite sprite;
    protected Collider collider;
    protected boolean active = true;
    protected List<Component> components = new ArrayList<>();
    
    public void update() {
        if (!active) return;
        
        // コンポーネント更新
        for (Component component : components) {
            component.update();
        }
        
        // 基本物理更新
        position.add(velocity);
    }
    
    public void render(Renderer renderer) {
        if (!active || sprite == null) return;
        
        sprite.render(renderer, position);
    }
    
    public <T extends Component> T getComponent(Class<T> type) {
        for (Component component : components) {
            if (type.isInstance(component)) {
                return type.cast(component);
            }
        }
        return null;
    }
    
    public void addComponent(Component component) {
        components.add(component);
        component.setOwner(this);
    }
}
```

#### Playerクラス（プレイヤーキャラクター）
```java
public class Player extends GameObject {
    private static final double MOVE_SPEED = 5.0;
    private static final double JUMP_FORCE = -15.0;
    private static final double GRAVITY = 0.8;
    
    private boolean onGround = false;
    private Animation runAnimation;
    private Animation idleAnimation;
    private Animation jumpAnimation;
    private int health = 3;
    private int score = 0;
    
    public Player(Vector2D position) {
        this.position = position;
        this.velocity = new Vector2D(0, 0);
        
        // アニメーション設定
        loadAnimations();
        
        // 衝突判定設定
        this.collider = new BoxCollider(32, 48);
        
        // コンポーネント追加
        addComponent(new PhysicsComponent());
        addComponent(new AnimationComponent());
    }
    
    @Override
    public void update() {
        handleInput();
        applyPhysics();
        updateAnimation();
        super.update();
    }
    
    private void handleInput() {
        InputManager input = InputManager.getInstance();
        
        // 左右移動
        if (input.isKeyPressed(KeyEvent.VK_LEFT)) {
            velocity.x = -MOVE_SPEED;
        } else if (input.isKeyPressed(KeyEvent.VK_RIGHT)) {
            velocity.x = MOVE_SPEED;
        } else {
            velocity.x = 0;
        }
        
        // ジャンプ
        if (input.isKeyJustPressed(KeyEvent.VK_SPACE) && onGround) {
            velocity.y = JUMP_FORCE;
            onGround = false;
        }
    }
    
    private void applyPhysics() {
        // 重力適用
        if (!onGround) {
            velocity.y += GRAVITY;
        }
        
        // 終端速度制限
        velocity.y = Math.min(velocity.y, 20.0);
    }
    
    private void updateAnimation() {
        if (!onGround) {
            sprite.setAnimation(jumpAnimation);
        } else if (Math.abs(velocity.x) > 0.1) {
            sprite.setAnimation(runAnimation);
        } else {
            sprite.setAnimation(idleAnimation);
        }
        
        // スプライトの向き
        sprite.setFlipX(velocity.x < 0);
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            // ゲームオーバー処理
            GameStateManager.getInstance().pushState(new GameOverState());
        }
    }
    
    public void collectItem(Item item) {
        score += item.getValue();
        AudioManager.getInstance().playSound("item_collect");
    }
}
```

## 🖥 UI設計

### メインメニュー

```
┌─────────────────────────────────────────┐
│                                         │
│          SUPER JAVA ADVENTURE           │
│                                         │
│              [NEW GAME]                 │
│              [CONTINUE]                 │
│              [OPTIONS]                  │
│              [HIGH SCORES]              │
│              [EXIT]                     │
│                                         │
│                                         │
│         Press ENTER to select          │
│         Use ↑↓ to navigate             │
└─────────────────────────────────────────┘
```

### ゲーム画面HUD

```
┌─────────────────────────────────────────┐
│ ❤❤❤ Score: 12,340  Level: 1-3  00:45   │ ← HUD
├─────────────────────────────────────────┤
│                                         │
│      🧱🧱🧱🧱🧱                        │
│                                         │
│    👤                              🏆   │ ← ゲーム世界
│  🧱🧱🧱                         🧱🧱🧱  │
│                                         │
│                 🦹                      │
│  🧱🧱🧱🧱🧱🧱🧱🧱🧱🧱🧱🧱🧱🧱🧱  │
└─────────────────────────────────────────┘
```

## 📈 実装フェーズ

### フェーズ1: ゲームエンジン基盤（2週間）

#### Week 1: コアシステム
- [ ] GameEngineクラスとメインループ
- [ ] 基本的な描画システム
- [ ] 入力管理システム
- [ ] シーン・状態管理システム

#### Week 2: 物理・衝突システム
- [ ] 物理演算システム
- [ ] 衝突判定システム
- [ ] GameObject基底クラス
- [ ] コンポーネントシステム

### フェーズ2: ゲームロジック実装（2週間）

#### Week 3: プレイヤーと基本機能
- [ ] プレイヤーキャラクター実装
- [ ] 基本移動・ジャンプ機能
- [ ] アニメーションシステム
- [ ] カメラ追従機能

#### Week 4: ゲーム要素
- [ ] 敵キャラクターAI
- [ ] アイテムシステム
- [ ] レベル・ステージ機能
- [ ] ゲームUI実装

### フェーズ3: 高度機能・完成（1週間）

#### Week 5: 仕上げ
- [ ] オーディオシステム
- [ ] エフェクト・パーティクル
- [ ] セーブ・ロード機能
- [ ] 最終バランス調整

## 🧪 テスト戦略

### 単体テスト例

```java
@Test
public void testPlayerMovement() {
    Player player = new Player(new Vector2D(0, 0));
    
    // 右移動テスト
    InputManager.getInstance().setKeyPressed(KeyEvent.VK_RIGHT, true);
    player.update();
    
    assertTrue(player.getVelocity().x > 0);
}

@Test
public void testCollisionDetection() {
    Player player = new Player(new Vector2D(0, 0));
    Platform platform = new Platform(new Vector2D(0, 50));
    
    CollisionSystem collisionSystem = new CollisionSystem();
    boolean collision = collisionSystem.checkCollision(
        player.getCollider(), platform.getCollider());
    
    assertTrue(collision);
}

@Test
public void testScoreSystem() {
    Player player = new Player(new Vector2D(0, 0));
    Item coin = new Item(new Vector2D(10, 0), ItemType.COIN);
    
    int initialScore = player.getScore();
    player.collectItem(coin);
    
    assertEquals(initialScore + coin.getValue(), player.getScore());
}
```

### パフォーマンステスト
```java
@Test
public void testFrameRateStability() {
    GameEngine engine = new GameEngine();
    PerformanceMonitor monitor = new PerformanceMonitor();
    
    engine.run(); // 10秒間実行
    
    double averageFPS = monitor.getAverageFPS();
    assertTrue("FPS should be stable above 55", averageFPS > 55.0);
}
```

## 🎨 グラフィクス・オーディオ

### スプライト管理
```java
public class SpriteManager {
    private Map<String, BufferedImage> sprites = new HashMap<>();
    
    public void loadSprite(String name, String path) {
        try {
            BufferedImage image = ImageIO.read(
                getClass().getResourceAsStream(path));
            sprites.put(name, image);
        } catch (IOException e) {
            System.err.println("Failed to load sprite: " + path);
        }
    }
    
    public BufferedImage getSprite(String name) {
        return sprites.get(name);
    }
    
    public BufferedImage[] loadSpriteSheet(String path, int tileWidth, int tileHeight) {
        // スプライトシートを個別フレームに分割
        BufferedImage sheet = getSprite(path);
        int rows = sheet.getHeight() / tileHeight;
        int cols = sheet.getWidth() / tileWidth;
        
        BufferedImage[] frames = new BufferedImage[rows * cols];
        int index = 0;
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                frames[index++] = sheet.getSubimage(
                    x * tileWidth, y * tileHeight, tileWidth, tileHeight);
            }
        }
        
        return frames;
    }
}
```

### オーディオシステム
```java
public class AudioManager {
    private Map<String, Clip> soundEffects = new HashMap<>();
    private Clip backgroundMusic;
    
    public void loadSound(String name, String path) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                getClass().getResourceAsStream(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundEffects.put(name, clip);
        } catch (Exception e) {
            System.err.println("Failed to load sound: " + path);
        }
    }
    
    public void playSound(String name) {
        Clip clip = soundEffects.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void playBackgroundMusic(String name, boolean loop) {
        stopBackgroundMusic();
        backgroundMusic = soundEffects.get(name);
        if (backgroundMusic != null) {
            backgroundMusic.setFramePosition(0);
            if (loop) {
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                backgroundMusic.start();
            }
        }
    }
}
```

## 🎮 ゲーム設計のベストプラクティス

### ゲームループ最適化
```java
public class OptimizedGameLoop {
    private static final int TARGET_UPS = 60; // Updates per second
    private static final int TARGET_FPS = 60; // Frames per second
    
    public void run() {
        long lastUpdateTime = System.nanoTime();
        long lastRenderTime = System.nanoTime();
        final double UPDATE_TIME_NS = 1000000000.0 / TARGET_UPS;
        final double RENDER_TIME_NS = 1000000000.0 / TARGET_FPS;
        
        while (running) {
            long now = System.nanoTime();
            
            // 固定タイムステップでゲームロジック更新
            if (now - lastUpdateTime >= UPDATE_TIME_NS) {
                update();
                lastUpdateTime = now;
            }
            
            // 可変フレームレートで描画
            if (now - lastRenderTime >= RENDER_TIME_NS) {
                render();
                lastRenderTime = now;
            }
            
            // CPU使用率を下げるための短時間スリープ
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
```

### 状態マシン設計
```java
public class GameStateManager {
    private Stack<GameState> states = new Stack<>();
    
    public void pushState(GameState state) {
        if (!states.isEmpty()) {
            states.peek().pause();
        }
        states.push(state);
        state.enter();
    }
    
    public void popState() {
        if (!states.isEmpty()) {
            states.pop().exit();
            if (!states.isEmpty()) {
                states.peek().resume();
            }
        }
    }
    
    public void update() {
        if (!states.isEmpty()) {
            states.peek().update();
        }
    }
    
    public void render(Renderer renderer) {
        if (!states.isEmpty()) {
            states.peek().render(renderer);
        }
    }
}
```

## 🚀 高度機能（オプション）

### パーティクルシステム
- 爆発エフェクト
- 魔法効果
- 環境パーティクル

### AI システム
- 敵キャラクターの行動AI
- パスファインディング
- 状態マシンベースのAI

### ネットワーク対応
- マルチプレイヤー機能
- リアルタイム同期
- サーバー・クライアント構成

## 📝 学習ポイント

### ゲーム開発特有のスキル
- **リアルタイム処理**: 60FPSでの安定動作
- **物理演算**: ゲーム世界の物理シミュレーション
- **ユーザーエクスペリエンス**: 楽しさの創造
- **パフォーマンス最適化**: 限られたリソースでの効率化

### 汎用的な技術スキル
- **イベント駆動プログラミング**: リアクティブシステム
- **状態管理**: 複雑な状態遷移の管理
- **デザインパターン**: Observer、State、Component等
- **マルチメディア処理**: グラフィクス・オーディオ技術

この課題を通じて、エンターテインメント分野での開発スキルと、高度なプログラミング技術を総合的に習得できます。