// 状態を持つのに無理にインターフェイスを使用
interface Service {
    // インターフェイスでは実装できない
    // private String serviceId;  // コンパイルエラー
    
    void initialize();
    void shutdown();
    
    default String getServiceId() {
        // 状態を持てないため、この実装は不完全
        return "unknown";
    }
}
