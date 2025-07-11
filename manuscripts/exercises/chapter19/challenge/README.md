# 第19章 GUIのイベント処理 - チャレンジレベル課題

## 概要
チャレンジレベル課題では、商用アプリケーションレベルの高度なイベント処理システムを実装します。リアルタイムコラボレーション、ゲームエンジン、複雑なユーザーインタラクションなど、最先端のGUI技術に挑戦します。

## 課題一覧

### 課題1: リアルタイムコラボレーティブホワイトボード
**CollaborativeWhiteboard.java**

複数ユーザーが同時に描画できる共有ホワイトボードアプリケーションを実装します。

**要求仕様：**
- 複数クライアントのリアルタイム同期
- 描画操作の競合解決
- ユーザーごとのカーソル表示
- 操作履歴とタイムライン再生
- チャット機能統合

**技術要件：**
```java
public class CollaborativeWhiteboard extends JFrame {
    private DrawingCanvas canvas;
    private NetworkManager networkManager;
    private OperationHistory history;
    private Map<String, UserCursor> remoteCursors;
    
    // 操作の抽象化（CRDTベース）
    public abstract class DrawOperation {
        private final String userId;
        private final long timestamp;
        private final String operationId;
        
        public abstract void apply(DrawingCanvas canvas);
        public abstract void merge(DrawOperation other);
        public abstract DrawOperation transform(DrawOperation other);
    }
    
    // イベント処理の最適化
    private class OptimizedEventHandler {
        private final Timer batchTimer;
        private final Queue<DrawEvent> eventQueue;
        private final int BATCH_SIZE = 10;
        private final int BATCH_DELAY = 16; // 60fps
        
        public OptimizedEventHandler() {
            eventQueue = new ConcurrentLinkedQueue<>();
            batchTimer = new Timer(BATCH_DELAY, e -> processBatch());
            batchTimer.start();
        }
        
        public void handleDrawEvent(DrawEvent event) {
            eventQueue.offer(event);
            
            // 重要なイベントは即座に処理
            if (event.isPriority() || eventQueue.size() > BATCH_SIZE) {
                processBatch();
            }
        }
        
        private void processBatch() {
            List<DrawEvent> batch = new ArrayList<>();
            DrawEvent event;
            
            while ((event = eventQueue.poll()) != null 
                   && batch.size() < BATCH_SIZE) {
                batch.add(event);
            }
            
            if (!batch.isEmpty()) {
                // バッチ処理で効率化
                SwingUtilities.invokeLater(() -> {
                    canvas.beginBatchUpdate();
                    batch.forEach(e -> e.apply(canvas));
                    canvas.endBatchUpdate();
                    
                    // ネットワーク送信も最適化
                    networkManager.sendBatch(batch);
                });
            }
        }
    }
    
    // 競合解決アルゴリズム
    private class ConflictResolver {
        public List<DrawOperation> resolve(
                List<DrawOperation> localOps,
                List<DrawOperation> remoteOps) {
            // Operational Transformation (OT)の実装
            List<DrawOperation> transformed = new ArrayList<>();
            
            for (DrawOperation remote : remoteOps) {
                DrawOperation transformedOp = remote;
                for (DrawOperation local : localOps) {
                    if (conflict(local, transformedOp)) {
                        transformedOp = transformedOp.transform(local);
                    }
                }
                transformed.add(transformedOp);
            }
            
            return transformed;
        }
    }
}
```

**実装課題：**
1. **ネットワーク同期**
   - WebSocketによるリアルタイム通信
   - デルタ同期による帯域幅最適化
   - 切断/再接続の処理

2. **パフォーマンス最適化**
   - 描画操作のバッチ処理
   - 差分レンダリング
   - オフスクリーンバッファリング

3. **協調編集機能**
   - 同時編集の視覚化
   - ロック機構
   - 権限管理

### 課題2: 2Dゲームエンジン
**GameEngine2D.java**

イベント駆動型の2Dゲームエンジンを実装します。

**要求仕様：**
- ゲームループとイベントシステム
- 物理エンジン（簡易版）
- 衝突検出システム
- パーティクルシステム
- サウンドイベント統合

**技術要件：**
```java
public class GameEngine2D {
    private final int TARGET_FPS = 60;
    private final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
    
    private GameLoop gameLoop;
    private EventDispatcher eventDispatcher;
    private PhysicsEngine physics;
    private RenderingEngine renderer;
    private List<GameObject> gameObjects;
    
    // 高精度ゲームループ
    private class GameLoop implements Runnable {
        private volatile boolean running = true;
        private long lastLoopTime = System.nanoTime();
        
        @Override
        public void run() {
            while (running) {
                long now = System.nanoTime();
                long updateLength = now - lastLoopTime;
                lastLoopTime = now;
                double delta = updateLength / ((double) OPTIMAL_TIME);
                
                // 入力処理
                processInput();
                
                // 物理演算とゲームロジック更新
                update(delta);
                
                // レンダリング
                render();
                
                // フレームレート制御
                long sleepTime = (lastLoopTime - System.nanoTime() 
                                + OPTIMAL_TIME) / 1000000;
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        private void update(double delta) {
            // イベントディスパッチ
            eventDispatcher.dispatchQueuedEvents();
            
            // 物理演算
            physics.step(delta);
            
            // ゲームオブジェクト更新
            for (GameObject obj : gameObjects) {
                obj.update(delta);
            }
            
            // 衝突検出
            checkCollisions();
        }
    }
    
    // イベントシステム
    public class EventDispatcher {
        private final Map<Class<?>, List<EventHandler<?>>> handlers;
        private final Queue<GameEvent> eventQueue;
        
        public <T extends GameEvent> void addEventListener(
                Class<T> eventType, EventHandler<T> handler) {
            handlers.computeIfAbsent(eventType, k -> new ArrayList<>())
                   .add(handler);
        }
        
        public void fireEvent(GameEvent event) {
            // 即座に処理するかキューに追加
            if (event.isImmediate()) {
                dispatchEvent(event);
            } else {
                eventQueue.offer(event);
            }
        }
        
        @SuppressWarnings("unchecked")
        private void dispatchEvent(GameEvent event) {
            List<EventHandler<?>> eventHandlers = 
                handlers.get(event.getClass());
            if (eventHandlers != null) {
                for (EventHandler handler : eventHandlers) {
                    handler.handle(event);
                }
            }
        }
    }
    
    // 衝突検出システム
    private class CollisionDetector {
        private QuadTree quadTree;
        
        public List<CollisionEvent> detectCollisions() {
            List<CollisionEvent> collisions = new ArrayList<>();
            
            // 空間分割による効率化
            quadTree.clear();
            gameObjects.forEach(quadTree::insert);
            
            for (GameObject obj : gameObjects) {
                List<GameObject> nearby = quadTree.query(
                    obj.getBoundingBox());
                
                for (GameObject other : nearby) {
                    if (obj != other && obj.collidesWith(other)) {
                        collisions.add(new CollisionEvent(obj, other));
                    }
                }
            }
            
            return collisions;
        }
    }
}
```

**実装課題：**
1. **高度な入力処理**
   - コンボ入力の認識
   - ジェスチャー検出
   - 複数入力デバイス対応

2. **パフォーマンス最適化**
   - オブジェクトプーリング
   - 空間分割アルゴリズム
   - マルチスレッド処理

3. **拡張機能**
   - スクリプトエンジン統合
   - レベルエディタ
   - デバッグビジュアライザー

## 評価基準

### 技術的革新性（40%）
- 先進的な技術の活用
- 独自アルゴリズムの実装
- パフォーマンスの最適化
- スケーラビリティ

### 完成度（30%）
- 機能の網羅性
- バグの少なさ
- エラーハンドリング
- ドキュメントの充実

### ユーザー体験（20%）
- 操作の快適性
- レスポンスの速さ
- 視覚的フィードバック
- 直感的なインターフェース

### コード品質（10%）
- アーキテクチャ設計
- 再利用性
- テストカバレッジ
- 保守性

## 提出物

1. **完全なプロジェクト**
   - ソースコード（テスト含む）
   - ビルド設定
   - 実行可能ファイル

2. **技術文書**
   - システムアーキテクチャ
   - アルゴリズム解説
   - パフォーマンス分析
   - APIリファレンス

3. **デモンストレーション**
   - 機能紹介動画
   - ライブデモ準備
   - ベンチマーク結果

## 発展的な研究テーマ

- **機械学習統合**: ユーザー行動予測
- **VR/AR対応**: 3D空間でのイベント処理
- **分散システム**: P2P型協調システム
- **新しい入力デバイス**: 音声、視線、脳波

## 参考リソース

- [Game Programming Patterns](https://gameprogrammingpatterns.com/)
- [Real-Time Rendering](https://www.realtimerendering.com/)
- [Collaborative Editing](https://arxiv.org/abs/1310.1187)
- [High Performance JavaScript](https://www.oreilly.com/library/view/high-performance-javascript/9781449382308/)