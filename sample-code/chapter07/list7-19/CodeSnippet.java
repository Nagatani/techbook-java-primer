/**
 * リスト7-19
 * コードスニペット
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (816行目)
 */

// API進化の管理
interface ServiceV1 {
    String process(String input);
}

interface ServiceV2 extends ServiceV1 {
    // 新機能をdefaultメソッドで追加（後方互換性維持）
    default String processWithOptions(String input, Map<String, String> options) {
        // デフォルトではオプションを無視して従来の処理
        return process(input);
    }
    
    // 型安全な設定オプション
    default String processWithConfig(String input, ServiceConfig config) {
        Map<String, String> options = new HashMap<>();
        options.put("timeout", String.valueOf(config.getTimeout()));
        options.put("retries", String.valueOf(config.getRetries()));
        return processWithOptions(input, options);
    }
}

interface ServiceV3 extends ServiceV2 {
    // さらなる拡張：非同期処理
    default CompletableFuture<String> processAsync(String input) {
        return CompletableFuture.supplyAsync(() -> process(input));
    }
    
    // バッチ処理のサポート
    default List<String> processBatch(List<String> inputs) {
        return inputs.stream()
            .map(this::process)
            .collect(Collectors.toList());
    }
    
    // 非推奨メソッドの管理
    @Deprecated(since = "3.0", forRemoval = true)
    default String oldProcess(String input) {
        System.err.println("警告: oldProcessは非推奨です。processを使用してください。");
        return process(input);
    }
}

// 実装クラスは最小限の変更で新バージョンに対応
class ServiceImpl implements ServiceV3 {
    @Override
    public String process(String input) {
        return "Processed: " + input.toUpperCase();
    }
    
    // オプションで新機能をオーバーライド
    @Override
    public String processWithOptions(String input, Map<String, String> options) {
        String timeout = options.getOrDefault("timeout", "1000");
        return "Processed with timeout " + timeout + ": " + input.toUpperCase();
    }
}