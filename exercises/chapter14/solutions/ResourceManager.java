import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * try-with-resources活用クラス
 * 自動リソース管理を実装
 */
public class ResourceManager {
    
    private static final Logger logger = Logger.getLogger(ResourceManager.class.getName());
    
    /**
     * カスタムリソースクラス
     */
    public static class CustomResource implements AutoCloseable {
        private final String name;
        private boolean closed = false;
        
        public CustomResource(String name) {
            this.name = name;
            logger.info("リソース '" + name + "' を開きました");
        }
        
        public void doWork() throws Exception {
            if (closed) {
                throw new IllegalStateException("リソースは既に閉じられています: " + name);
            }
            logger.info("リソース '" + name + "' で作業を実行中");
            // 模擬的な作業
            Thread.sleep(100);
        }
        
        @Override
        public void close() throws Exception {
            if (!closed) {
                closed = true;
                logger.info("リソース '" + name + "' を閉じました");
            }
        }
        
        public boolean isClosed() {
            return closed;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * 例外が発生するリソースクラス
     */
    public static class ProblematicResource implements AutoCloseable {
        private final String name;
        private final boolean failOnClose;
        
        public ProblematicResource(String name, boolean failOnClose) {
            this.name = name;
            this.failOnClose = failOnClose;
            logger.info("問題のあるリソース '" + name + "' を開きました");
        }
        
        public void doWork() throws Exception {
            logger.info("問題のあるリソース '" + name + "' で作業を実行中");
            throw new RuntimeException("作業中にエラーが発生しました: " + name);
        }
        
        @Override
        public void close() throws Exception {
            logger.info("問題のあるリソース '" + name + "' を閉じています");
            if (failOnClose) {
                throw new RuntimeException("クローズ中にエラーが発生しました: " + name);
            }
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * ファイルコピーを実行
     * @param source コピー元ファイル
     * @param target コピー先ファイル
     * @throws IOException ファイル操作エラー
     */
    public static void copyFile(String source, String target) throws IOException {
        try (InputStream in = Files.newInputStream(Paths.get(source));
             OutputStream out = Files.newOutputStream(Paths.get(target))) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            logger.info("ファイルコピー完了: " + source + " -> " + target);
        }
    }
    
    /**
     * テキストファイルを読み取り
     * @param filePath ファイルパス
     * @return ファイル内容
     * @throws IOException ファイル読み取りエラー
     */
    public static String readTextFile(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        }
    }
    
    /**
     * テキストファイルに書き込み
     * @param filePath ファイルパス
     * @param content 書き込み内容
     * @throws IOException ファイル書き込みエラー
     */
    public static void writeTextFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write(content);
            logger.info("ファイル書き込み完了: " + filePath);
        }
    }
    
    /**
     * データベース操作を実行
     * @param jdbcUrl データベースURL
     * @param query SQL クエリ
     * @return クエリ結果の行数
     * @throws SQLException データベースエラー
     */
    public static int executeQuery(String jdbcUrl, String query) throws SQLException {
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
            }
            logger.info("クエリ実行完了: " + count + " 行を取得");
            return count;
        }
    }
    
    /**
     * 複数のリソースを同時に使用
     * @param resourceNames リソース名の配列
     * @return 処理結果
     */
    public static String useMultipleResources(String... resourceNames) {
        StringBuilder result = new StringBuilder();
        
        try (CustomResource res1 = new CustomResource(resourceNames[0]);
             CustomResource res2 = new CustomResource(resourceNames[1])) {
            
            res1.doWork();
            res2.doWork();
            
            result.append("両方のリソースでの作業が完了しました");
            
        } catch (Exception e) {
            result.append("エラーが発生しました: ").append(e.getMessage());
            logger.log(Level.SEVERE, "複数リソース使用中にエラー", e);
        }
        
        return result.toString();
    }
    
    /**
     * 例外が発生するリソースの処理
     * @param resourceName リソース名
     * @param failOnWork 作業中に失敗するか
     * @param failOnClose クローズ時に失敗するか
     * @return 処理結果
     */
    public static String handleProblematicResource(String resourceName, boolean failOnWork, boolean failOnClose) {
        StringBuilder result = new StringBuilder();
        
        try (ProblematicResource resource = new ProblematicResource(resourceName, failOnClose)) {
            if (failOnWork) {
                resource.doWork();
            } else {
                result.append("作業を正常に完了しました");
            }
        } catch (Exception e) {
            result.append("メイン処理でエラー: ").append(e.getMessage());
            
            // 抑制された例外をチェック
            Throwable[] suppressed = e.getSuppressed();
            if (suppressed.length > 0) {
                result.append(" (抑制された例外: ");
                for (int i = 0; i < suppressed.length; i++) {
                    if (i > 0) result.append(", ");
                    result.append(suppressed[i].getMessage());
                }
                result.append(")");
            }
            
            logger.log(Level.SEVERE, "問題のあるリソース処理中にエラー", e);
        }
        
        return result.toString();
    }
    
    /**
     * ネストしたtry-with-resourcesの例
     * @param outerResourceName 外側のリソース名
     * @param innerResourceName 内側のリソース名
     * @return 処理結果
     */
    public static String nestedTryWithResources(String outerResourceName, String innerResourceName) {
        StringBuilder result = new StringBuilder();
        
        try (CustomResource outer = new CustomResource(outerResourceName)) {
            outer.doWork();
            
            try (CustomResource inner = new CustomResource(innerResourceName)) {
                inner.doWork();
                result.append("ネストしたリソースの処理が完了しました");
            }
            
        } catch (Exception e) {
            result.append("ネストしたリソース処理でエラー: ").append(e.getMessage());
            logger.log(Level.SEVERE, "ネストしたリソース処理中にエラー", e);
        }
        
        return result.toString();
    }
    
    /**
     * リソースの初期化に失敗する例
     * @param shouldFail 失敗させるか
     * @return 処理結果
     */
    public static String handleResourceInitializationFailure(boolean shouldFail) {
        StringBuilder result = new StringBuilder();
        
        try (CustomResource resource = shouldFail ? 
                new CustomResource("failing-resource") {
                    {
                        throw new RuntimeException("リソース初期化に失敗しました");
                    }
                } : 
                new CustomResource("normal-resource")) {
            
            resource.doWork();
            result.append("リソース処理が完了しました");
            
        } catch (Exception e) {
            result.append("リソース初期化または処理でエラー: ").append(e.getMessage());
            logger.log(Level.SEVERE, "リソース初期化失敗", e);
        }
        
        return result.toString();
    }
    
    /**
     * 例外チェーンによる根本原因追跡の例
     * @param causeChainDepth 例外チェーンの深さ
     * @return 処理結果
     */
    public static String demonstrateExceptionChain(int causeChainDepth) {
        StringBuilder result = new StringBuilder();
        
        try {
            throwNestedExceptions(causeChainDepth);
        } catch (Exception e) {
            result.append("例外をキャッチしました: ").append(e.getMessage());
            
            // 例外チェーンを辿る
            Throwable current = e;
            int depth = 0;
            while (current != null) {
                if (depth > 0) {
                    result.append("\n").append("  ".repeat(depth))
                          .append("原因: ").append(current.getClass().getSimpleName())
                          .append(": ").append(current.getMessage());
                }
                current = current.getCause();
                depth++;
            }
            
            logger.log(Level.SEVERE, "例外チェーンの例", e);
        }
        
        return result.toString();
    }
    
    private static void throwNestedExceptions(int depth) throws Exception {
        if (depth <= 0) {
            throw new IllegalArgumentException("根本原因: 無効な引数です");
        }
        
        try {
            throwNestedExceptions(depth - 1);
        } catch (Exception e) {
            throw new Exception("レベル " + depth + " の例外", e);
        }
    }
}