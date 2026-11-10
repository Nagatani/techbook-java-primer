import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

public class TextBlockFileIO {
    public static void main(String[] args) throws IOException {
        // 1. ログファイルテンプレートの生成
        generateLogTemplate();
        
        // 2. 設定ファイルの読み書き
        handleConfigurationFile();
        
        // 3. レポート生成
        generateReport();
        
        // 4. 複数ファイルの一括処理
        batchProcessFiles();
    }
    
    private static void generateLogTemplate() throws IOException {
        String logTemplate = """
                # アプリケーションログ設定
                # 生成日時: %s
                
                ## ログレベル設定
                root.level=INFO
                com.example.app=DEBUG
                com.example.db=WARN
                
                ## 出力先設定
                console.enabled=true
                file.enabled=true
                file.path=logs/app-%s.log
                file.maxSize=10MB
                file.maxHistory=30
                
                ## フォーマット設定
                pattern.console=%%d{HH:mm:ss} [%%thread] %%-5level %%logger{0} - %%msg%%n
                pattern.file=%%d{yyyy-MM-dd HH:mm:ss.SSS} [%%thread] %%-5level %%logger{36} - %%msg%%n
                """.formatted(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        Path logConfigPath = Paths.get("log_config.properties");
        Files.writeString(logConfigPath, logTemplate);
        IO.println("ログ設定ファイルを生成しました: " + logConfigPath);
    }
    
    private static void handleConfigurationFile() throws IOException {
        // デフォルト設定の作成
        String defaultConfig = """
                # アプリケーション設定ファイル
                app.name=Java Primer Sample
                app.version=1.0.0
                
                # データベース設定
                db.url=jdbc:h2:mem:testdb
                db.username=sa
                db.password=
                db.pool.size=10
                
                # キャッシュ設定
                cache.enabled=true
                cache.size=1000
                cache.ttl=300
                
                # ファイル設定
                files.upload.dir=uploads
                files.max.size=10485760
                files.allowed.types=jpg,png,gif,pdf,doc,docx
                """;
        
        Path configPath = Paths.get("application.properties");
        
        // 設定ファイルが存在しない場合のみ作成
        if (Files.notExists(configPath)) {
            Files.writeString(configPath, defaultConfig);
            IO.println("デフォルト設定ファイルを作成しました");
        } else {
            // 既存設定ファイルの読み込みと表示
            String existingConfig = Files.readString(configPath);
            IO.println("既存の設定ファイルを読み込みました:");
            IO.println(existingConfig.substring(0, 
                    Math.min(200, existingConfig.length())) + "...");
        }
    }
    
    private static void generateReport() throws IOException {
        // サンプルデータ
        String[][] userData = {
                {"田中太郎", "30", "東京", "エンジニア"},
                {"佐藤花子", "25", "大阪", "デザイナー"},
                {"鈴木一郎", "35", "名古屋", "マネージャー"},
                {"高橋美咲", "28", "福岡", "アナリスト"}
        };
        
        // HTML レポートの生成
        StringBuilder tableRows = new StringBuilder();
        for (String[] user : userData) {
            tableRows.append("""
                    <tr>
                        <td>%s</td>
                        <td>%s歳</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
                    """.formatted(user[0], user[1], user[2], user[3]));
        }
        
        String reportHtml = """
                <!DOCTYPE html>
                <html lang="ja">
                <head>
                    <meta charset="UTF-8">
                    <title>ユーザーレポート</title>
                    <style>
                        body { font-family: 'Yu Gothic', sans-serif; margin: 40px; }
                        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
                        table { border-collapse: collapse; width: 100%%; margin-top: 20px; }
                        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                        th { background-color: #f2f2f2; font-weight: bold; }
                        tr:nth-child(even) { background-color: #f9f9f9; }
                        .meta { color: #666; font-size: 14px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <h1>📊 ユーザー管理システム - 月次レポート</h1>
                    <p>レポート生成日時: %s</p>
                    
                    <table>
                        <thead>
                            <tr>
                                <th>名前</th>
                                <th>年齢</th>
                                <th>所在地</th>
                                <th>職業</th>
                            </tr>
                        </thead>
                        <tbody>
                %s
                        </tbody>
                    </table>
                    
                    <div class="meta">
                        <p>総ユーザー数: %d名</p>
                        <p>平均年齢: %.1f歳</p>
                    </div>
                </body>
                </html>
                """.formatted(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")),
                tableRows.toString(),
                userData.length,
                java.util.Arrays.stream(userData)
                        .mapToInt(user -> Integer.parseInt(user[1]))
                        .average().orElse(0.0));
        
        Path reportPath = Paths.get("user_report.html");
        Files.writeString(reportPath, reportHtml);
        IO.println("HTMLレポートを生成しました: " + reportPath);
    }
    
    private static void batchProcessFiles() throws IOException {
        // 複数のサンプルファイルを一括生成
        String[] fileContents = {
                """
                # README.md
                
                ## Java Primer Sample Project
                
                このプロジェクトはJava学習用のサンプルプロジェクトです。
                
                ### 機能
                - ファイルI/O操作
                - Text Blocks の活用
                - 設定ファイルの管理
                """,
                
                """
                # TODO.md
                
                ## 開発タスク
                
                ### 優先度: 高
                - ユーザー認証機能の実装
                - データベース接続の設定
                
                ### 優先度: 中
                - ログ機能の改善
                - エラーハンドリングの強化
                
                ### 優先度: 低
                - UI の改善
                - パフォーマンス最適化
                """,
                
                """
                {
                  "project": {
                    "name": "Java Primer Sample",
                    "version": "1.0.0",
                    "description": "Text Blocks機能のデモンストレーション",
                    "author": "Java学習者",
                    "dependencies": [
                      "java.base",
                      "java.desktop"
                    ],
                    "features": [
                      "ファイルI/O",
                      "Text Blocks",
                      "JSON処理",
                      "HTML生成"
                    ]
                  }
                }
                """
        };
        
        String[] fileNames = {"README.md", "TODO.md", "project.json"};
        
        for (int i = 0; i < fileNames.length; i++) {
            Path filePath = Paths.get(fileNames[i]);
            Files.writeString(filePath, fileContents[i]);
            IO.println("生成しました: " + filePath);
        }
        
        IO.println("\n=== 一括ファイル処理完了 ===");
    }
}
