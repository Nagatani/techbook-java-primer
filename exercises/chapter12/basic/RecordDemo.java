import java.time.LocalDate;
import java.util.*;

/**
 * 第12章 レコード - 基本演習
 * デモプログラム
 */
public class RecordDemo {
    
    /**
     * Pointレコードのデモ
     */
    public static void demonstratePoint() {
        System.out.println("=== Pointレコードのデモ ===");
        
        // TODO: Pointのインスタンスを作成してテスト
        // - 原点からの距離
        // - 2点間の距離
        // - 移動
        // - equalsとhashCodeの動作確認
    }
    
    /**
     * Personレコードのデモ
     */
    public static void demonstratePerson() {
        System.out.println("\n=== Personレコードのデモ ===");
        
        // TODO: Personのインスタンスを作成してテスト
        // - 年齢計算
        // - メールドメイン取得
        // - フォーマットされた名前
        // - スタティックファクトリーメソッド
    }
    
    /**
     * DTOパターンのデモ
     */
    public static void demonstrateDTO() {
        System.out.println("\n=== DTOパターンのデモ ===");
        
        // エンティティからDTOへの変換
        ProductEntity entity = new ProductEntity(1L, "Laptop", 120000.0, "Electronics");
        
        // TODO: ProductDTO.fromEntity()を使用してDTOを作成
        // - 税込み価格の表示
        // - フォーマットされた価格
        // - JSON形式の出力
    }
    
    /**
     * ネストされたRecordのデモ
     */
    public static void demonstrateNestedRecords() {
        System.out.println("\n=== ネストされたRecordのデモ ===");
        
        // TODO: AddressとCustomerのインスタンスを作成
        // - ビルダーパターンの使用
        // - 顧客情報の要約表示
        // - プライマリ電話番号の取得
    }
    
    /**
     * Recordのコレクション操作のデモ
     */
    public static void demonstrateRecordCollections() {
        System.out.println("\n=== Recordのコレクション操作 ===");
        
        // TODO: 複数のPointを作成してコレクション操作
        // - 原点からの距離でソート
        // - 特定の範囲内の点をフィルタリング
        // - Mapのkeyとしての使用
    }
    
    public static void main(String[] args) {
        demonstratePoint();
        demonstratePerson();
        demonstrateDTO();
        demonstrateNestedRecords();
        demonstrateRecordCollections();
    }
}