/**
 * 第12章 レコード - 基本演習
 * ネストされたRecord
 */
// TODO: Addressをrecordとして定義
// フィールド: String street, String city, String postalCode, String country
public record Address(/* TODO: フィールドを定義 */) {
    
    // TODO: コンパクトコンストラクタで入力検証を追加
    // - すべてのフィールドはnullまたは空文字列でない
    // - postalCodeは数字とハイフンのみを含む
    
    /**
     * 住所をフォーマットして表示
     */
    public String getFormattedAddress() {
        // TODO: 「郵便番号 都市名\n住所\n国名」形式で返す
        return "";
    }
    
    /**
     * 日本の住所かどうかを判定
     */
    public boolean isJapanese() {
        // TODO: countryが"Japan"または"日本"の場合true
        return false;
    }
}