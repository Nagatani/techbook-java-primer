import java.time.LocalDate;
import java.time.Period;

/**
 * 第12章 レコード - 基本演習
 * ビジネスロジックを含むRecord
 */
// TODO: Personをrecordとして定義
// フィールド: String name, LocalDate birthDate, String email
public record Person(/* TODO: フィールドを定義 */) {
    
    // TODO: コンパクトコンストラクタで入力検証を追加
    // - nameはnullまたは空文字列でない
    // - birthDateはnullでなく、未来の日付でない
    // - emailは「@」を含む
    
    /**
     * 年齢を計算
     */
    public int getAge() {
        // TODO: Period.between()を使用して実装
        return 0;
    }
    
    /**
     * メールドメインを取得
     */
    public String getEmailDomain() {
        // TODO: emailからドメイン部分を抽出
        // 例: "user@example.com" -> "example.com"
        return "";
    }
    
    /**
     * フォーマットされた名前を返す
     */
    public String getFormattedName() {
        // TODO: 名前の最初の文字を大文字にする
        // 例: "john doe" -> "John Doe"
        return "";
    }
    
    /**
     * スタティックファクトリーメソッド: 名前とメールからPersonを作成
     * 誕生日はデフォルトで今日から20年前
     */
    public static Person of(String name, String email) {
        // TODO: デフォルトの誕生日でPersonを作成
        return null;
    }
}