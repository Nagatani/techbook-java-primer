/**
 * リスト9-33
 * MemoryOptimizationクラス
 * 
 * 元ファイル: chapter09-records.md (1549行目)
 */

public class MemoryOptimization {
    
    // メモリ効率を考慮したRecord設計
    public record CompactUser(
        // 必要最小限のフィールド
        String id,           // 8 bytes (reference)
        short age,           // 2 bytes (short instead of int)
        byte status          // 1 byte (enum ordinalを使用)
    ) {
        public CompactUser {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("Invalid age");
            }
        }
        
        public UserStatus getStatus() {
            return UserStatus.values()[status];
        }
        
        public static CompactUser of(String id, int age, UserStatus status) {
            return new CompactUser(id, (short) age, (byte) status.ordinal());
        }
    }
    
    enum UserStatus { ACTIVE, INACTIVE, SUSPENDED }
    
    // 大量データでのメモリ効率測定
    public static void memoryUsageComparison() {
        int count = 1_000_000;
        
        // メモリ使用量測定用
        Runtime runtime = Runtime.getRuntime();
        
        // 従来のクラス
        runtime.gc();
        long beforeTraditional = runtime.totalMemory() - runtime.freeMemory();
        
        List<TraditionalUser> traditionalUsers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            traditionalUsers.add(new TraditionalUser("user" + i, 25, UserStatus.ACTIVE));
        }
        
        long afterTraditional = runtime.totalMemory() - runtime.freeMemory();
        long traditionalMemory = afterTraditional - beforeTraditional;
        
        // Record版
        runtime.gc();
        long beforeRecord = runtime.totalMemory() - runtime.freeMemory();
        
        List<CompactUser> recordUsers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            recordUsers.add(CompactUser.of("user" + i, 25, UserStatus.ACTIVE));
        }
        
        long afterRecord = runtime.totalMemory() - runtime.freeMemory();
        long recordMemory = afterRecord - beforeRecord;
        
        System.out.printf("Traditional: %d MB%n", traditionalMemory / (1024 * 1024));
        System.out.printf("Record: %d MB%n", recordMemory / (1024 * 1024));
        System.out.printf("Memory reduction: %.1f%%%n", 
            (1.0 - (double) recordMemory / traditionalMemory) * 100);
    }
    
    static class TraditionalUser {
        private final String id;
        private final int age;
        private final UserStatus status;
        
        TraditionalUser(String id, int age, UserStatus status) {
            this.id = id;
            this.age = age;
            this.status = status;
        }
        
        // getter、equals、hashCodeなど
    }
}