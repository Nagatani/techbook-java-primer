/**
 * 第2章 課題2: Bookクラステスト - 解答例
 * 
 * Bookクラスの動作を確認するテストクラス
 * 
 * 学習ポイント:
 * - 複数オブジェクトの作成と管理
 * - 計算メソッドの呼び出し
 * - オブジェクト間の比較処理
 */
public class BookTest {
    public static void main(String[] args) {
        System.out.println("=== 図書管理システム ===");
        
        // 解答例 1: 基本的な使用方法
        Book book1 = new Book();
        book1.title = "Java入門";
        book1.author = "山田花子";
        book1.price = 2500;
        
        book1.displayInfo();
        book1.displayTaxIncludedPrice();
        
        System.out.println(); // 空行
        
        Book book2 = new Book();
        book2.title = "Python基礎";
        book2.author = "佐藤次郎";
        book2.price = 3000;
        
        book2.displayInfo();
        book2.displayTaxIncludedPrice();
        
        System.out.println(); // 空行
        
        // 解答例 2: 価格比較
        System.out.println("=== 価格比較 ===");
        int priceDifference = book1.comparePriceWith(book2);
        if (priceDifference > 0) {
            System.out.println(book1.title + "の方が" + priceDifference + "円高いです");
        } else if (priceDifference < 0) {
            System.out.println(book1.title + "の方が" + Math.abs(priceDifference) + "円安いです");
        } else {
            System.out.println("両方とも同じ価格です");
        }
        
        System.out.println(); // 空行
        
        // 解答例 3: より詳細な機能テスト
        System.out.println("=== 詳細機能テスト ===");
        
        // 3冊目の本を作成
        Book book3 = new Book();
        book3.setBookInfo("データ構造とアルゴリズム", "田中一郎", 4500);
        
        // 詳細情報表示
        book3.displayDetailedInfo();
        System.out.println();
        
        // 割引計算テスト
        System.out.println("=== 割引計算テスト ===");
        int originalPrice = book3.price;
        int discounted20 = book3.calculateDiscountedPrice(0.2);  // 20%割引
        int discounted50 = book3.calculateDiscountedPrice(0.5);  // 50%割引
        
        System.out.println("元の価格: " + String.format("%,d", originalPrice) + "円");
        System.out.println("20%割引: " + String.format("%,d", discounted20) + "円");
        System.out.println("50%割引: " + String.format("%,d", discounted50) + "円");
        
        // 無効な割引率のテスト
        book3.calculateDiscountedPrice(-0.1);  // 無効な割引率
        book3.calculateDiscountedPrice(1.5);   // 無効な割引率
        
        System.out.println(); // 空行
        
        // 解答例 4: 複数の本の管理
        System.out.println("=== 書籍コレクション管理 ===");
        
        // 同じ著者の本を追加
        Book book4 = new Book();
        book4.setBookInfo("Java応用", "山田花子", 3200);
        
        Book[] books = {book1, book2, book3, book4};
        
        // 全書籍の情報表示
        System.out.println("全書籍一覧:");
        for (int i = 0; i < books.length; i++) {
            System.out.println((i + 1) + ". " + books[i].toShortString());
        }
        System.out.println();
        
        // 最高価格と最低価格の本を見つける
        Book mostExpensive = books[0];
        Book cheapest = books[0];
        
        for (Book book : books) {
            if (book.isMoreExpensive(mostExpensive)) {
                mostExpensive = book;
            }
            if (cheapest.isMoreExpensive(book)) {
                cheapest = book;
            }
        }
        
        System.out.println("最高価格の本: " + mostExpensive.toShortString());
        System.out.println("最低価格の本: " + cheapest.toShortString());
        System.out.println();
        
        // 同じ著者の本を検索
        System.out.println("=== 著者別検索 ===");
        System.out.println(book1.author + " の著作:");
        for (Book book : books) {
            if (book1.isSameAuthor(book)) {
                System.out.println("- " + book.title);
            }
        }
        System.out.println();
        
        // 解答例 5: 統計情報の計算
        System.out.println("=== 統計情報 ===");
        
        // 合計価格と平均価格
        int totalPrice = 0;
        int totalTaxIncluded = 0;
        
        for (Book book : books) {
            totalPrice += book.price;
            totalTaxIncluded += book.calculateTaxIncludedPrice();
        }
        
        double averagePrice = (double) totalPrice / books.length;
        double averageTaxIncluded = (double) totalTaxIncluded / books.length;
        
        System.out.println("総書籍数: " + books.length + "冊");
        System.out.println("合計価格: " + String.format("%,d", totalPrice) + "円");
        System.out.println("合計税込価格: " + String.format("%,d", totalTaxIncluded) + "円");
        System.out.println("平均価格: " + String.format("%.0f", averagePrice) + "円");
        System.out.println("平均税込価格: " + String.format("%.0f", averageTaxIncluded) + "円");
        
        // 価格帯別分類
        System.out.println();
        System.out.println("価格帯別分類:");
        int lowCount = 0, midCount = 0, highCount = 0;
        
        for (Book book : books) {
            String category = book.getPriceCategory();
            switch (category) {
                case "低価格帯":
                    lowCount++;
                    break;
                case "中価格帯":
                    midCount++;
                    break;
                case "高価格帯":
                    highCount++;
                    break;
            }
        }
        
        System.out.println("低価格帯（1000円未満）: " + lowCount + "冊");
        System.out.println("中価格帯（1000-3000円）: " + midCount + "冊");
        System.out.println("高価格帯（3000円以上）: " + highCount + "冊");
        
        // 解答例 6: ISBN生成テスト
        System.out.println();
        System.out.println("=== ISBN生成テスト ===");
        for (Book book : books) {
            System.out.println(book.title + ": " + book.generateSimpleISBN());
        }
        
        // 解答例 7: エラーケースのテスト
        System.out.println();
        System.out.println("=== エラーケーステスト ===");
        
        Book invalidBook = new Book();
        invalidBook.setBookInfo("", "", -100);     // 無効な情報
        invalidBook.setBookInfo(null, null, 0);    // null情報
        invalidBook.setBookInfo("有効なタイトル", "有効な著者", 1000);  // 有効な情報
        
        // 未初期化の本との比較
        Book uninitializedBook = new Book();
        System.out.println("未初期化の本の情報:");
        uninitializedBook.displayInfo();
        
        boolean sameAuthor = book1.isSameAuthor(uninitializedBook);
        System.out.println("著者比較結果: " + sameAuthor);
    }
}

/*
 * テストクラスの学習ポイント:
 * 
 * 1. 複数オブジェクトの作成と管理
 *    - 各Bookオブジェクトは独立したデータを持つ
 *    - 配列を使って複数のオブジェクトを効率的に管理
 * 
 * 2. メソッドの様々な使い方
 *    - 計算メソッド: calculateTaxIncludedPrice()
 *    - 判定メソッド: isMoreExpensive(), isSameAuthor()
 *    - 比較メソッド: comparePriceWith()
 * 
 * 3. データの集計処理
 *    - for-eachループを使った集計
 *    - 統計情報の計算（合計、平均）
 *    - 分類・カウント処理
 * 
 * 4. エラーハンドリングの確認
 *    - 無効なデータでの動作確認
 *    - null安全性のテスト
 *    - 境界値での動作確認
 * 
 * テスト設計の観点:
 * 
 * 1. 正常ケース
 *    - 期待通りの計算結果が得られるか
 *    - 比較処理が正しく動作するか
 * 
 * 2. 境界ケース
 *    - 価格が0円の場合
 *    - 非常に高価な本の場合
 *    - 同じ価格の本同士の比較
 * 
 * 3. 異常ケース
 *    - 未初期化のオブジェクト
 *    - null値を含むデータ
 *    - 無効な入力値
 * 
 * 実世界での応用:
 * - 書店の在庫管理システム
 * - オンラインショッピングの価格比較
 * - 図書館の蔵書管理
 * - 出版社の価格設定システム
 * 
 * プログラミングのベストプラクティス:
 * - メソッドの戻り値を適切に活用
 * - 配列・ループを使った効率的なデータ処理
 * - エラーケースも含めた包括的なテスト
 */