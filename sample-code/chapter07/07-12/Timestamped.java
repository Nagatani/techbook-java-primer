import java.time.Duration;

// 複数の機能を提供するMixin
interface Timestamped {
    long getTimestamp();
    
    default boolean isExpired(long expirationTime) {
        return System.currentTimeMillis() - getTimestamp() > expirationTime;
    }
    
    default Duration getAge() {
        return Duration.ofMillis(System.currentTimeMillis() - getTimestamp());
    }
}

interface Identifiable {
    String getId();
    
    default String getFullIdentifier() {
        return getClass().getSimpleName() + "#" + getId();
    }
}

interface Versioned {
    int getVersion();
    
    default boolean isNewerThan(Versioned other) {
        return this.getVersion() > other.getVersion();
    }
    
    default boolean isOlderThan(Versioned other) {
        return this.getVersion() < other.getVersion();
    }
}

// 複数のMixinを組み合わせ
class Memo implements Timestamped, Identifiable, Versioned {
    private final String id;
    private final long timestamp;
    private int version;
    private String content;
    
    public Memo(String id, String content) {
        this.id = id;
        this.timestamp = System.currentTimeMillis();
        this.version = 1;
        this.content = content;
    }
    
    @Override
    public String getId() { return id; }
    
    @Override
    public long getTimestamp() { return timestamp; }
    
    @Override
    public int getVersion() { return version; }
    
    public void update(String newContent) {
        this.content = newContent;
        this.version++;
        
        // defaultメソッドを活用
        if (isExpired(3600000)) {  // 1時間
            IO.println("Memo " + getFullIdentifier() + " has expired");
        }
    }
}

class MixinDemo {
    public static void main(String[] args) {
        Memo memo1 = new Memo("M001", "買い物リスト");
        Memo memo2 = new Memo("M002", "会議メモ");
        memo1.update("買い物リスト（更新）");

        IO.println("=== Identifiableの機能 ===");
        IO.println("memo1: " + memo1.getFullIdentifier());
        IO.println("memo2: " + memo2.getFullIdentifier());

        IO.println();
        IO.println("=== Timestampedの機能 ===");
        // 経過時間は実行するたびに変わるので、1秒未満かどうかだけを表示する
        IO.println("memo1の経過時間は1秒未満？: " + (memo1.getAge().toMillis() < 1000));
        IO.println("memo1は期限切れ？: " + memo1.isExpired(3600000));

        IO.println();
        IO.println("=== Versionedの機能 ===");
        IO.println("memo1のバージョン: " + memo1.getVersion());
        IO.println("memo2のバージョン: " + memo2.getVersion());
        IO.println("memo1はmemo2より新しい？: " + memo1.isNewerThan(memo2));
    }
}
