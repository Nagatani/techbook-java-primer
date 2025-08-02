<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 7/11
行範囲: 1108 - 1290
作成日時: 2025-08-02 21:08:55

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### 高度なコンストラクタチェーン

実践的なクラス設計では、バリデーションロジックを1つのコンストラクタに集約し、他のコンストラクタはそれを呼び出します。

<span class="listing-number">**サンプルコード4-20**</span>

```java
public class DatabaseConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int maxConnections;
    private final int timeoutSeconds;
    
    // マスターコンストラクタ（すべての検証をここに集約）
    public DatabaseConfig(String host, int port, String database, 
                         String username, String password, 
                         int maxConnections, int timeoutSeconds) {
        // 詳細な検証ロジック
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("ホスト名は必須です");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("ポート番号は1-65535の範囲で指定してください");
        }
        if (maxConnections < 1) {
            throw new IllegalArgumentException("最大接続数は1以上である必要があります");
        }
        
        this.host = host.trim();
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.maxConnections = maxConnections;
        this.timeoutSeconds = timeoutSeconds;
    }
    
    // 開発環境用のコンストラクタ
    public DatabaseConfig(String host, String database) {
        this(host, 5432, database, "dev_user", "dev_password", 10, 30);
    }
    
    // 本番環境用のコンストラクタ
    public DatabaseConfig(String host, int port, String database, 
                         String username, String password) {
        this(host, port, database, username, password, 100, 60);
    }
}
```

これは複数のコンストラクタで検証ロジックを集約する例です。

#### コールバックでのthis渡し

イベント処理やコールバック関数で、現在のオブジェクトを別のオブジェクトへ渡す場合に使用します。

<span class="listing-number">**サンプルコード4-21**</span>

```java
import java.util.List;
import java.util.ArrayList;

public class EventProcessor {
    private String name;
    
    public EventProcessor(String name) {
        this.name = name;
    }
    
    public void startProcessing() {
        // 自分自身をイベントハンドラーに登録
        EventManager.register(this);
        System.out.println(name + " が処理を開始しました");
    }
    
    public void handleEvent(String event) {
        System.out.println(name + " がイベントを処理: " + event);
    }
}
```

これはコールバックでthisを渡す例です。

class EventManager {
    private static List<EventProcessor> processors = new ArrayList<>();
    
    public static void register(EventProcessor processor) {
        processors.add(processor);
    }
    
    public static void fireEvent(String event) {
        for (EventProcessor processor : processors) {
            processor.handleEvent(event);
        }
    }
}
```

### 高度なメソッドオーバーロード設計

第3章でメソッドオーバーロードの基本を学びました。ここでは、実用的なAPIデザインにおけるオーバーロードの活用パターンを学習します。

#### デフォルト値を提供するオーバーロード

オーバーロードを使って、使いやすいAPIを設計できます。

<span class="listing-number">**サンプルコード4-22**</span>

```java
import java.util.Map;
import java.util.HashMap;

public class HttpClient {
    // フルスペックのメソッド
    public String get(String url, Map<String, String> headers, int timeoutMs) {
        // HTTP GET実装
        return "Response from " + url;
    }
    
    // ヘッダーのデフォルト値を提供
    public String get(String url, int timeoutMs) {
        return get(url, new HashMap<>(), timeoutMs);
    }
    
    // タイムアウトのデフォルト値も提供
    public String get(String url) {
        return get(url, 5000);  // 5秒のデフォルトタイムアウト
    }
}
```

これはテレスコーピングコンストラクタの例です。
```

#### 型安全性を高めるオーバーロード

異なるデータ型に対応しながら、型安全性を維持します。

<span class="listing-number">**サンプルコード4-23**</span>

```java
public class Logger {
    // 文字列メッセージ
    public void log(String message) {
        System.out.println("[INFO] " + message);
    }
    
    // 例外情報
    public void log(String message, Exception e) {
        System.out.println("[ERROR] " + message + ": " + e.getMessage());
    }
    
    // レベル付きログ
    public void log(String level, String message) {
        System.out.println("[" + level + "] " + message);
    }
    
    // フォーマット付きメッセージ
    public void log(String format, Object... args) {
        System.out.println("[INFO] " + String.format(format, args));
    }
    
    // ログレベルを表す定数
    public static final String LOG_DEBUG = "DEBUG";
    public static final String LOG_INFO = "INFO";
    public static final String LOG_WARN = "WARN";
    public static final String LOG_ERROR = "ERROR";
}
```

実践的なオーバーロード設計原則。
- もっとも多機能なメソッドを1つ定義し、他はそれを呼び出す
- デフォルト値は意味のある値を選ぶ
- 引数の順序を統一する（URL → オプション → タイムアウトなど）
- null安全性を考慮する

## まとめ

本章では、オブジェクト指向プログラミングの中核となるカプセル化とクラス設計について学習しました。



<!-- 
================
チャンク 7/11 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
