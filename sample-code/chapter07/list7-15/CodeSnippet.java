/**
 * リスト7-15
 * コードスニペット
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (521行目)
 */

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
class Document implements Timestamped, Identifiable, Versioned {
    private final String id;
    private final long timestamp;
    private int version;
    private String content;
    
    public Document(String id, String content) {
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
        
        // デフォルトメソッドを活用
        if (isExpired(3600000)) {  // 1時間
            System.out.println("Document " + getFullIdentifier() + " has expired");
        }
    }
}