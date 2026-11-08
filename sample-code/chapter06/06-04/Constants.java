import java.time.LocalDateTime;
import java.util.List;

public class Constants {
    // クラス定数（static final）
    public static final double PI = 3.14159265359;
    public static final int MAX_CONNECTIONS = 100;
    
    // インスタンス定数（finalフィールド）
    private final String id;
    private final LocalDateTime createdAt;
    
    public Constants(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();  // コンストラクタで初期化
    }
    
    // finalローカル変数
    public void processData(List<String> data) {
        final int size = data.size();
        // size = 10;  // コンパイルエラー： final変数は変更不可
        
        data.forEach(item -> {
            // ラムダ式内でローカル変数を使う場合、実質的にfinalである必要がある
            System.out.println(item + " (total: " + size + ")");
        });
    }
}
