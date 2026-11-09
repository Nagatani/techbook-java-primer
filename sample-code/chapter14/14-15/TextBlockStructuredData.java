import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextBlockStructuredData {
    public static void main(String[] args) throws IOException {
        // 1. JSON データの生成
        String userName = "田中太郎";
        int userAge = 30;
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        
        String jsonData = """
                {
                  "user": {
                    "name": "%s",
                    "age": %d,
                    "timestamp": "%s",
                    "preferences": {
                      "language": "Japanese",
                      "theme": "dark",
                      "notifications": true
                    }
                  },
                  "metadata": {
                    "version": "1.0",
                    "created": "%s"
                  }
                }
                """.formatted(userName, userAge, timestamp, timestamp);
        
        // JSONファイルとして保存
        Path jsonFile = Paths.get("user_data.json");
        Files.writeString(jsonFile, jsonData);
        System.out.println("=== JSON データを保存しました ===");
        System.out.println(jsonData);
        
        // 2. SQL クエリの生成
        String tableName = "users";
        String condition = "2024-01-01";
        
        String sqlQuery = """
                SELECT
                    u.id,
                    u.name,
                    u.email,
                    u.created_at,
                    p.title AS latest_post,
                    COUNT(c.id) AS comment_count
                FROM %s u
                LEFT JOIN posts p ON u.id = p.user_id 
                    AND p.created_at = (
                        SELECT MAX(created_at) 
                        FROM posts 
                        WHERE user_id = u.id
                    )
                LEFT JOIN comments c ON p.id = c.post_id
                WHERE u.created_at > '%s'
                GROUP BY u.id, u.name, u.email, u.created_at, p.title
                ORDER BY u.created_at DESC
                LIMIT 100;
                """.formatted(tableName, condition);
        
        Path sqlFile = Paths.get("user_query.sql");
        Files.writeString(sqlFile, sqlQuery);
        System.out.println("\n=== SQL クエリを保存しました ===");
        System.out.println(sqlQuery);
        
        // 3. HTML テンプレートの生成
        String pageTitle = "ユーザープロフィール";
        
        String htmlTemplate = """
                <!DOCTYPE html>
                <html lang="ja">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        body {
                            font-family: 'Helvetica Neue', Arial, sans-serif;
                            max-width: 800px;
                            margin: 0 auto;
                            padding: 20px;
                            background-color: #f5f5f5;
                        }
                        .profile-card {
                            background: white;
                            border-radius: 8px;
                            padding: 24px;
                            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        }
                        .user-name {
                            color: #333;
                            font-size: 24px;
                            margin-bottom: 8px;
                        }
                        .user-details {
                            color: #666;
                            line-height: 1.6;
                        }
                    </style>
                </head>
                <body>
                    <div class="profile-card">
                        <h1 class="user-name">%s</h1>
                        <div class="user-details">
                            <p><strong>年齢:</strong> %d歳</p>
                            <p><strong>最終更新:</strong> %s</p>
                            <p><strong>ステータス:</strong> アクティブ</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(pageTitle, userName, userAge, timestamp);
        
        Path htmlFile = Paths.get("user_profile.html");
        Files.writeString(htmlFile, htmlTemplate);
        System.out.println("\n=== HTML テンプレートを保存しました ===");
        System.out.println("ファイル: " + htmlFile.toAbsolutePath());
        
        // 4. XML 設定ファイルの生成
        String appName = "UserManager";
        String version = "2.1.0";
        
        String xmlConfig = """
                <?xml version="1.0" encoding="UTF-8"?>
                <configuration>
                    <application>
                        <name>%s</name>
                        <version>%s</version>
                        <environment>production</environment>
                    </application>
                    <database>
                        <url>jdbc:postgresql://localhost:5432/userdb</url>
                        <username>app_user</username>
                        <pool>
                            <min-connections>5</min-connections>
                            <max-connections>20</max-connections>
                            <timeout>30000</timeout>
                        </pool>
                    </database>
                    <logging>
                        <level>INFO</level>
                        <file>logs/application.log</file>
                        <pattern>%%d{yyyy-MM-dd HH:mm:ss} [%%thread] %%-5level %%logger{36} - %%msg%%n</pattern>
                    </logging>
                </configuration>
                """.formatted(appName, version);
        
        Path xmlFile = Paths.get("app_config.xml");
        Files.writeString(xmlFile, xmlConfig);
        System.out.println("\n=== XML 設定ファイルを保存しました ===");
        
        // 生成されたファイルの一覧表示
        System.out.println("\n=== 生成されたファイル一覧 ===");
        Path currentDir = Paths.get(".");
        Files.list(currentDir)
                .filter(p -> p.toString().matches(".*\\.(json|sql|html|xml)$"))
                .forEach(p -> {
                    try {
                        long size = Files.size(p);
                        System.out.printf("%-20s (%d bytes)%n", 
                                p.getFileName(), size);
                    } catch (IOException e) {
                        System.out.println(p.getFileName() + " (サイズ取得エラー)");
                    }
                });
    }
}
