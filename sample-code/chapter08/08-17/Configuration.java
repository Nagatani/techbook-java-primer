import java.time.Duration;

public record Configuration(
    String host,
    int port,
    boolean useSsl,
    Duration timeout
) {
    // withメソッド群
    public Configuration withHost(String newHost) {
        return new Configuration(newHost, port, useSsl, timeout);
    }
    
    public Configuration withPort(int newPort) {
        return new Configuration(host, newPort, useSsl, timeout);
    }
    
    public Configuration withSsl(boolean newUseSsl) {
        return new Configuration(host, port, newUseSsl, timeout);
    }
    
    public Configuration withTimeout(Duration newTimeout) {
        return new Configuration(host, port, useSsl, newTimeout);
    }
}
