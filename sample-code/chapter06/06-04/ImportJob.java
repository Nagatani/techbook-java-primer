import java.time.LocalDateTime;
import java.util.List;

// データの取り込み作業を表すクラス
public class ImportJob {
    // クラス定数（static final）
    public static final int MAX_LINES = 10000;
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    // インスタンス定数（finalフィールド）
    private final String id;
    private final LocalDateTime createdAt;
    
    public ImportJob(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();  // コンストラクターで初期化
    }
    
    // finalローカル変数
    public void process(List<String> data) {
        final int size = data.size();
        // size = 10;  // コンパイルエラー： final変数は変更不可
        
        for (String item : data) {
            IO.println(item + " (total: " + size + ")");
        }
    }
}
