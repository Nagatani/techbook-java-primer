/**
 * 第2章 課題2: Bookクラスの設計 - 解答例
 * 
 * 本を表すBookクラスの実装
 * 
 * 学習ポイント:
 * - 複数のインスタンス変数の管理
 * - 計算処理を含むメソッド
 * - オブジェクト間の比較処理
 */
public class Book {
    // インスタンス変数
    String title;
    String author;
    int price;
    
    // 解答例 1: 基本的な実装
    
    /**
     * 書籍情報を表示するメソッド
     */
    void displayInfo() {
        System.out.println("書籍情報:");
        System.out.println("タイトル: " + title);
        System.out.println("著者: " + author);
        System.out.println("価格: " + String.format("%,d", price) + "円");
    }
    
    /**
     * 税込価格を計算するメソッド
     * @return 税込価格
     */
    int calculateTaxIncludedPrice() {
        return (int)(price * 1.1);
    }
    
    /**
     * 税込価格を表示するメソッド
     */
    void displayTaxIncludedPrice() {
        int taxIncludedPrice = calculateTaxIncludedPrice();
        System.out.println("税込価格: " + String.format("%,d", taxIncludedPrice) + "円");
    }
    
    // 解答例 2: より詳細な実装
    
    /**
     * 詳細な書籍情報を表示するメソッド
     */
    void displayDetailedInfo() {
        System.out.println("=== 書籍詳細情報 ===");
        System.out.println("タイトル: " + (title != null ? title : "未設定"));
        System.out.println("著者: " + (author != null ? author : "未設定"));
        System.out.println("価格: " + String.format("%,d", price) + "円");
        
        int taxIncludedPrice = calculateTaxIncludedPrice();
        int tax = taxIncludedPrice - price;
        System.out.println("税額: " + String.format("%,d", tax) + "円");
        System.out.println("税込価格: " + String.format("%,d", taxIncludedPrice) + "円");
        
        // 価格帯による分類
        String priceCategory = getPriceCategory();
        System.out.println("価格帯: " + priceCategory);
    }
    
    /**
     * 価格帯を判定するメソッド
     * @return 価格帯の文字列
     */
    String getPriceCategory() {
        if (price < 1000) {
            return "低価格帯";
        } else if (price < 3000) {
            return "中価格帯";
        } else {
            return "高価格帯";
        }
    }
    
    /**
     * 割引価格を計算するメソッド
     * @param discountRate 割引率（0.0-1.0）
     * @return 割引後価格
     */
    int calculateDiscountedPrice(double discountRate) {
        if (discountRate < 0.0 || discountRate > 1.0) {
            System.out.println("無効な割引率です（0.0-1.0の範囲で指定してください）");
            return price;
        }
        return (int)(price * (1.0 - discountRate));
    }
    
    /**
     * 別の本と価格を比較するメソッド
     * @param other 比較対象の本
     * @return 価格差（この本の価格 - 相手の価格）
     */
    int comparePriceWith(Book other) {
        if (other != null) {
            return this.price - other.price;
        }
        return 0;
    }
    
    /**
     * より高価かどうかを判定するメソッド
     * @param other 比較対象の本
     * @return より高価な場合true
     */
    boolean isMoreExpensive(Book other) {
        return other != null && this.price > other.price;
    }
    
    /**
     * 同じ著者かどうかを判定するメソッド
     * @param other 比較対象の本
     * @return 同じ著者の場合true
     */
    boolean isSameAuthor(Book other) {
        if (other != null && this.author != null && other.author != null) {
            return this.author.equals(other.author);
        }
        return false;
    }
    
    /**
     * 書籍情報を設定するメソッド
     * @param title タイトル
     * @param author 著者
     * @param price 価格
     */
    void setBookInfo(String title, String author, int price) {
        if (title != null && !title.trim().isEmpty() && 
            author != null && !author.trim().isEmpty() && 
            price >= 0) {
            this.title = title.trim();
            this.author = author.trim();
            this.price = price;
            System.out.println("書籍情報を設定しました: " + this.title);
        } else {
            System.out.println("無効な書籍情報です。設定できませんでした。");
        }
    }
    
    /**
     * 書籍の簡潔な文字列表現を返すメソッド
     * @return 書籍の文字列表現
     */
    String toShortString() {
        return "「" + (title != null ? title : "無題") + "」 by " + 
               (author != null ? author : "著者不明") + 
               " (" + String.format("%,d", price) + "円)";
    }
    
    /**
     * 在庫管理用のISBNコード（簡易版）を生成
     * @return 簡易ISBNコード
     */
    String generateSimpleISBN() {
        if (title != null && author != null) {
            // タイトルと著者名からハッシュコードを生成（簡易版）
            int hashCode = (title + author).hashCode();
            return "ISBN-" + Math.abs(hashCode % 1000000);
        }
        return "ISBN-000000";
    }
}

/*
 * クラス設計の進歩ポイント:
 * 
 * 1. 計算メソッドの実装
 *    - calculateTaxIncludedPrice(): 税込価格計算
 *    - calculateDiscountedPrice(): 割引価格計算
 *    - 計算結果を返すメソッドと表示メソッドの分離
 * 
 * 2. 比較・判定メソッド
 *    - comparePriceWith(): 価格の比較
 *    - isMoreExpensive(): より高価かの判定
 *    - isSameAuthor(): 同じ著者かの判定
 * 
 * 3. データの妥当性チェック
 *    - setBookInfo(): 設定時のバリデーション
 *    - null安全な文字列比較
 *    - 範囲チェック（割引率など）
 * 
 * 4. ユーティリティメソッド
 *    - toShortString(): 書籍の簡潔な表現
 *    - getPriceCategory(): 価格帯の分類
 *    - generateSimpleISBN(): 一意識別子の生成
 * 
 * よくある実装上の注意点:
 * 
 * 1. 文字列の比較
 *    × author == other.author  // 参照比較（間違い）
 *    ○ author.equals(other.author)  // 内容比較（正解）
 * 
 * 2. null安全性
 *    - 文字列フィールドはnullの可能性がある
 *    - 使用前にnullチェックを実装
 * 
 * 3. 数値の妥当性
 *    - 価格は非負の値である必要
 *    - 割引率は0.0-1.0の範囲
 * 
 * 4. メソッドの責任分離
 *    - 計算と表示を分離
 *    - 一つのメソッドは一つの責任
 * 
 * 設計の改善余地:
 * - コンストラクタの追加
 * - より詳細なバリデーション
 * - 例外処理の実装
 * - 不変性の考慮
 */