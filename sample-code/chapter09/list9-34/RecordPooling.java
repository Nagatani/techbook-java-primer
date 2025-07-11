/**
 * リスト9-34
 * RecordPoolingクラス
 * 
 * 元ファイル: chapter09-records.md (1632行目)
 */

// 高頻度で作成されるRecordのプーリング
public class RecordPooling {
    
    public record Coordinate(int x, int y) {}
    
    static class CoordinatePool {
        private static final int POOL_SIZE = 1000;
        private static final Map<Long, Coordinate> POOL = new ConcurrentHashMap<>();
        
        public static Coordinate get(int x, int y) {
            long key = ((long) x << 32) | (y & 0xFFFFFFFFL);
            return POOL.computeIfAbsent(key, k -> new Coordinate(x, y));
        }
        
        // よく使われる座標の事前作成
        static {
            for (int x = -100; x <= 100; x++) {
                for (int y = -100; y <= 100; y++) {
                    get(x, y);
                }
            }
        }
    }
    
    // 使用例
    public void gameLoop() {
        // プールから取得（新規作成なし）
        Coordinate playerPos = CoordinatePool.get(10, 20);
        Coordinate enemyPos = CoordinatePool.get(-5, 15);
        
        // ゲームロジック...
    }
}