import java.util.List;

/**
 * 第8章 レコード - 基本演習
 * ネストされたRecordを含むRecord
 */
// TODO: Customerをrecordとして定義
// フィールド: String name, String email, Address address, List<String> phoneNumbers
public record Customer(/* TODO: フィールドを定義 */) {
    
    // TODO: コンパクトコンストラクターで入力検証を追加
    // - phoneNumbersは不変のリストに変換
    // - 電話番号は少なくとも1つ必要
    
    /**
     * プライマリ電話番号を取得
     */
    public String getPrimaryPhone() {
        // TODO: 最初の電話番号を返す
        return "";
    }
    
    /**
     * 顧客情報の要約を返す
     */
    public String getSummary() {
        // TODO: 「名前 (メール) - 都市名, 国名」形式で返す
        return "";
    }
    
    /**
     * スタティックファクトリメソッド: ビルダーパターン
     */
    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }
    
    /**
     * ビルダークラス
     */
    public static class CustomerBuilder {
        private String name;
        private String email;
        private Address address;
        private List<String> phoneNumbers;
        
        // TODO: 各フィールドのセッターメソッドを実装
        // メソッドチェーンをサポート
        
        public Customer build() {
            // TODO: Customerを作成して返す
            return null;
        }
    }
}