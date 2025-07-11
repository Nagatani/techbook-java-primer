/**
 * 第12章 レコード - 基本演習
 * DTO (Data Transfer Object) パターンの実装
 */
// TODO: ProductDTOをrecordとして定義
// フィールド: Long id, String name, double price, String category
public record ProductDTO(/* TODO: フィールドを定義 */) {
    
    // TODO: コンパクトコンストラクタで入力検証を追加
    // - priceは0以上
    // - nameとcategoryはnullまたは空文字列でない
    
    /**
     * 税込み価格を計算（税率10%）
     */
    public double getPriceWithTax() {
        // TODO: 税込み価格を計算
        return 0.0;
    }
    
    /**
     * 価格をフォーマットして表示
     */
    public String getFormattedPrice() {
        // TODO: 「¥#,###」形式で表示
        // ヒント: String.format()を使用
        return "";
    }
    
    /**
     * 商品情報をJSON風の文字列で返す
     */
    public String toJson() {
        // TODO: シンプルなJSON形式で返す
        // 例: {"id":1,"name":"Product","price":100.0,"category":"Electronics"}
        return "";
    }
    
    /**
     * スタティックファクトリーメソッド: エンティティからDTOを作成
     */
    public static ProductDTO fromEntity(ProductEntity entity) {
        // TODO: ProductEntityからProductDTOを作成
        return null;
    }
}