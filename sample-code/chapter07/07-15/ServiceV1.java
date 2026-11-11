// API進化の管理
interface ServiceV1 {
    String process(String input);
}

interface ServiceV2 extends ServiceV1 {
    // 新機能をdefaultメソッドで追加（後方互換性維持）
    default String processWithConfig(String input, ServiceConfig config) {
        // デフォルトでは設定を無視して従来の処理
        return process(input);
    }
}

interface ServiceV3 extends ServiceV2 {
    // さらなる拡張：まとめて処理する（バッチ処理）
    default String[] processBatch(String[] inputs) {
        String[] results = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            results[i] = process(inputs[i]);
        }
        return results;
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
    public String processWithConfig(String input, ServiceConfig config) {
        return "Processed with timeout " + config.getTimeout() + ": " + input.toUpperCase();
    }
}
