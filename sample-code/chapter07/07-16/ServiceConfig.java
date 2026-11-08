// サービスの設定値（本文では省略している補助クラス）
public class ServiceConfig {
    private final int timeout;
    private final int retries;

    public ServiceConfig(int timeout, int retries) {
        this.timeout = timeout;
        this.retries = retries;
    }

    public int getTimeout() { return timeout; }
    public int getRetries() { return retries; }
}
