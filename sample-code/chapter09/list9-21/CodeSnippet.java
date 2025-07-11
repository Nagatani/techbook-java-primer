/**
 * リスト9-21
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (762行目)
 */

public record Configuration(
    String host,
    int port,
    boolean useSsl,
    Duration timeout,
    Map<String, String> properties
) {
    // コンパクトコンストラクタでバリデーション
    public Configuration {
        Objects.requireNonNull(host, "Host cannot be null");
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        Objects.requireNonNull(timeout, "Timeout cannot be null");
        if (timeout.isNegative()) {
            throw new IllegalArgumentException("Timeout cannot be negative");
        }
        // 防御的コピー
        properties = Map.copyOf(properties);
    }
    
    // withメソッド群
    public Configuration withHost(String newHost) {
        return new Configuration(newHost, port, useSsl, timeout, properties);
    }
    
    public Configuration withPort(int newPort) {
        return new Configuration(host, newPort, useSsl, timeout, properties);
    }
    
    public Configuration withSsl(boolean newUseSsl) {
        return new Configuration(host, port, newUseSsl, timeout, properties);
    }
    
    public Configuration withTimeout(Duration newTimeout) {
        return new Configuration(host, port, useSsl, newTimeout, properties);
    }
    
    public Configuration withProperty(String key, String value) {
        Map<String, String> newProperties = new HashMap<>(properties);
        newProperties.put(key, value);
        return new Configuration(host, port, useSsl, timeout, newProperties);
    }
    
    public Configuration withoutProperty(String key) {
        Map<String, String> newProperties = new HashMap<>(properties);
        newProperties.remove(key);
        return new Configuration(host, port, useSsl, timeout, newProperties);
    }
    
    // ファクトリメソッド
    public static Configuration createDefault() {
        return new Configuration(
            "localhost", 
            8080, 
            false, 
            Duration.ofSeconds(30),
            Map.of()
        );
    }
    
    public static Configuration forProduction(String host) {
        return new Configuration(
            host, 
            443, 
            true, 
            Duration.ofSeconds(10),
            Map.of("environment", "production")
        );
    }
}