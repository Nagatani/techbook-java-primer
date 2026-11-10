// インターフェイスでテンプレートメソッドパターン
public interface DataProcessor {
    // テンプレートメソッド
    default void process() {
        var data = loadData();
        var validated = validate(data);
        if (validated) {
            var processed = transform(data);
            save(processed);
            onSuccess();
        } else {
            onError("Validation failed");
        }
    }
    
    // 抽象メソッド（サブクラスで実装）
    Object loadData();
    boolean validate(Object data);
    Object transform(Object data);
    void save(Object data);
    
    // フックメソッド（オプション）
    default void onSuccess() {
        IO.println("Processing completed successfully");
    }
    
    default void onError(String message) {
        System.err.println("Error: " + message);
    }
}
