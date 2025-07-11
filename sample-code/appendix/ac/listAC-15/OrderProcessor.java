/**
 * リストAC-15
 * OrderProcessorクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (400行目)
 */

// 悪い例：グローバル変数への依存
public class OrderProcessor {
    public void processOrder() {
        GlobalConfig.getInstance().getDatabase().save(order);
    }
}

// 良い例：依存性注入
public class OrderProcessor {
    private final Database database;
    
    public OrderProcessor(Database database) {
        this.database = database;
    }
    
    public void processOrder(Order order) {
        database.save(order);
    }
}