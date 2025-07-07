import com.example.oss.api.HttpRequest;
import com.example.oss.api.HttpRequestBuilder;
import com.example.oss.core.Version;
import com.example.oss.plugins.Plugin;
import com.example.oss.plugins.PluginManager;
import com.example.oss.plugins.extension.ExtensionPoint;
import com.example.oss.plugins.samples.JsonDataTransformerPlugin;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * オープンソースライブラリの基本的な使用例
 */
public class BasicUsageExample {
    
    public static void main(String[] args) {
        // 1. Fluent APIの使用例
        demonstrateFluendAPI();
        
        // 2. バージョン管理の例
        demonstrateVersionManagement();
        
        // 3. プラグインシステムの例
        demonstratePluginSystem();
    }
    
    /**
     * Fluent APIを使用したHTTPリクエストの構築
     */
    private static void demonstrateFluendAPI() {
        System.out.println("=== Fluent API Example ===");
        
        // シンプルなGETリクエスト
        HttpRequest getRequest = HttpRequestBuilder.create()
            .get("https://api.github.com/users/octocat")
            .header("Accept", "application/json")
            .timeout(Duration.ofSeconds(10))
            .build();
        
        System.out.println("GET Request: " + getRequest);
        
        // POSTリクエストの例
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "John Doe");
        userData.put("email", "john@example.com");
        
        HttpRequest postRequest = HttpRequestBuilder.create()
            .post("https://api.example.com/users")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer your-token-here")
            .queryParam("include", "profile")
            .queryParam("fields", "id,name,email")
            .timeout(Duration.ofSeconds(30))
            .body(userData)
            .build();
        
        System.out.println("POST Request: " + postRequest);
        System.out.println("Full URL: " + postRequest.getFullUrl());
        System.out.println();
    }
    
    /**
     * セマンティックバージョニングの使用例
     */
    private static void demonstrateVersionManagement() {
        System.out.println("=== Version Management Example ===");
        
        // バージョンの解析
        Version current = Version.parse("1.2.3");
        Version latest = Version.parse("2.0.0-beta.1");
        Version patch = Version.parse("1.2.4");
        
        System.out.println("Current version: " + current);
        System.out.println("Latest version: " + latest);
        System.out.println("Patch version: " + patch);
        
        // 互換性チェック
        System.out.println("\nCompatibility checks:");
        System.out.println("Current compatible with patch? " + 
            current.isCompatibleWith(patch));
        System.out.println("Current compatible with latest? " + 
            current.isCompatibleWith(latest));
        
        // 破壊的変更の確認
        System.out.println("\nBreaking changes:");
        System.out.println("Latest has breaking changes from current? " + 
            latest.hasBreakingChanges(current));
        System.out.println("Patch has breaking changes from current? " + 
            patch.hasBreakingChanges(current));
        
        // バージョン比較
        System.out.println("\nVersion comparison:");
        System.out.println("Current < Latest? " + 
            (current.compareTo(latest) < 0));
        System.out.println("Is latest a pre-release? " + 
            latest.isPreRelease());
        System.out.println();
    }
    
    /**
     * プラグインシステムの使用例
     */
    private static void demonstratePluginSystem() {
        System.out.println("=== Plugin System Example ===");
        
        try {
            // プラグインマネージャーの作成
            PluginManager manager = new PluginManager();
            
            // プラグインを直接登録（通常はJARファイルから読み込む）
            Plugin jsonPlugin = new JsonDataTransformerPlugin();
            simulatePluginLoad(manager, jsonPlugin);
            
            // 拡張ポイントの取得
            ExtensionPoint<PluginManager.DataTransformer> transformers = 
                manager.getExtensionPoint("dataTransformers", 
                    PluginManager.DataTransformer.class);
            
            System.out.println("Available transformers: " + transformers.size());
            
            // データ変換の実行
            for (PluginManager.DataTransformer transformer : transformers.getExtensions()) {
                System.out.println("Transformer supports: " + 
                    transformer.getSupportedFormat());
                
                // サンプルオブジェクトの変換
                Map<String, String> sampleData = new HashMap<>();
                sampleData.put("message", "Hello, World!");
                sampleData.put("timestamp", "2024-01-01T00:00:00Z");
                
                if (transformer.canTransform("object", "json")) {
                    Object result = transformer.transform(sampleData, "object", "json");
                    System.out.println("Transformed to JSON: " + result);
                }
            }
            
            // プラグイン情報の表示
            System.out.println("\nLoaded plugins:");
            for (Plugin plugin : manager.getPlugins()) {
                System.out.println("- " + plugin.getName() + 
                    " v" + plugin.getVersion() +
                    " by " + plugin.getAuthor());
                System.out.println("  Description: " + plugin.getDescription());
            }
            
            // クリーンアップ
            manager.shutdown();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * プラグインのロードをシミュレート
     * （実際の実装ではJARファイルから読み込む）
     */
    private static void simulatePluginLoad(PluginManager manager, Plugin plugin) 
            throws Exception {
        // リフレクションを使用して内部的にプラグインを登録
        var pluginsField = PluginManager.class.getDeclaredField("plugins");
        pluginsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Plugin> plugins = (Map<String, Plugin>) pluginsField.get(manager);
        plugins.put(plugin.getName(), plugin);
        
        // プラグインの初期化と拡張の登録
        var registerMethod = PluginManager.class.getDeclaredMethod(
            "registerPluginExtensions", Plugin.class);
        registerMethod.setAccessible(true);
        registerMethod.invoke(manager, plugin);
        
        // プラグインの初期化
        plugin.initialize(new com.example.oss.plugins.DefaultPluginContext(manager));
    }
}