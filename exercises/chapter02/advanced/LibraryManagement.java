/**
 * 第2章 応用課題1: 図書管理システム
 * 
 * 書籍とそれを管理する図書館クラスを設計し、基本的な図書管理機能を実装。
 * オブジェクト指向の基本概念である複数クラスの連携を学習します。
 * 
 * 学習ポイント:
 * - 複数クラスの設計と連携
 * - オブジェクトの状態管理
 * - カプセル化の実践
 * - ArrayListを使ったデータ管理
 * - 実用的なクラス設計
 */

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 書籍情報を管理するクラス
 */
class Book {
    private String title;
    private String author;
    private String isbn;
    private boolean isRented;
    private String renterName;
    private LocalDate rentDate;
    private String category;
    
    /**
     * 書籍のコンストラクタ
     */
    public Book(String title, String author, String isbn, String category) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.isRented = false;
        this.renterName = null;
        this.rentDate = null;
    }
    
    /**
     * 書籍を貸出します
     */
    public boolean rent(String renterName) {
        if (!isRented) {
            this.isRented = true;
            this.renterName = renterName;
            this.rentDate = LocalDate.now();
            return true;
        }
        return false;
    }
    
    /**
     * 書籍を返却します
     */
    public boolean returnBook() {
        if (isRented) {
            this.isRented = false;
            this.renterName = null;
            this.rentDate = null;
            return true;
        }
        return false;
    }
    
    /**
     * 書籍情報を表示します
     */
    public void displayInfo() {
        System.out.printf("[%s] %s - %s (ISBN: %s) [%s]%n", 
            isRented ? "貸出中" : "利用可能", 
            title, author, isbn, category);
        
        if (isRented) {
            System.out.printf("    貸出者: %s, 貸出日: %s%n", 
                renterName, rentDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        }
    }
    
    /**
     * 書籍の詳細情報を表示します
     */
    public void displayDetailedInfo() {
        System.out.println("=== 書籍詳細情報 ===");
        System.out.println("タイトル: " + title);
        System.out.println("著者: " + author);
        System.out.println("ISBN: " + isbn);
        System.out.println("カテゴリ: " + category);
        System.out.println("状態: " + (isRented ? "貸出中" : "利用可能"));
        
        if (isRented) {
            System.out.println("貸出者: " + renterName);
            System.out.println("貸出日: " + rentDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
            System.out.println("経過日数: " + java.time.Period.between(rentDate, LocalDate.now()).getDays() + "日");
        }
        System.out.println();
    }
    
    // ゲッターメソッド
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getCategory() { return category; }
    public boolean isRented() { return isRented; }
    public String getRenterName() { return renterName; }
    public LocalDate getRentDate() { return rentDate; }
}

/**
 * 図書館システムを管理するクラス
 */
class Library {
    private String libraryName;
    private ArrayList<Book> books;
    private int totalRents;
    private int totalReturns;
    
    /**
     * 図書館のコンストラクタ
     */
    public Library(String libraryName) {
        this.libraryName = libraryName;
        this.books = new ArrayList<>();
        this.totalRents = 0;
        this.totalReturns = 0;
    }
    
    /**
     * 書籍を追加します
     */
    public void addBook(Book book) {
        books.add(book);
        System.out.println("✓ 「" + book.getTitle() + "」- " + book.getAuthor() + 
                          " (ISBN: " + book.getIsbn() + ") を追加");
    }
    
    /**
     * 複数の書籍を一括追加します
     */
    public void addBooks(Book[] newBooks) {
        System.out.println("書籍を追加しています...");
        for (Book book : newBooks) {
            addBook(book);
        }
        System.out.println();
    }
    
    /**
     * 書籍を検索します（タイトルまたは著者名で部分一致）
     */
    public ArrayList<Book> searchBooks(String keyword) {
        ArrayList<Book> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(lowerKeyword) ||
                book.getAuthor().toLowerCase().contains(lowerKeyword)) {
                results.add(book);
            }
        }
        
        return results;
    }
    
    /**
     * カテゴリで書籍を検索します
     */
    public ArrayList<Book> searchByCategory(String category) {
        ArrayList<Book> results = new ArrayList<>();
        
        for (Book book : books) {
            if (book.getCategory().equalsIgnoreCase(category)) {
                results.add(book);
            }
        }
        
        return results;
    }
    
    /**
     * ISBNで書籍を検索します
     */
    public Book findBookByIsbn(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;
    }
    
    /**
     * 書籍を貸出します
     */
    public boolean rentBook(String isbn, String renterName) {
        Book book = findBookByIsbn(isbn);
        
        if (book == null) {
            System.out.println("✗ 指定されたISBNの書籍が見つかりません: " + isbn);
            return false;
        }
        
        if (book.rent(renterName)) {
            totalRents++;
            System.out.println("✓ 「" + book.getTitle() + "」を" + renterName + "さんに貸出完了");
            return true;
        } else {
            System.out.println("✗ 「" + book.getTitle() + "」は既に貸出中です");
            return false;
        }
    }
    
    /**
     * 書籍を返却します
     */
    public boolean returnBook(String isbn) {
        Book book = findBookByIsbn(isbn);
        
        if (book == null) {
            System.out.println("✗ 指定されたISBNの書籍が見つかりません: " + isbn);
            return false;
        }
        
        if (book.returnBook()) {
            totalReturns++;
            System.out.println("✓ 「" + book.getTitle() + "」の返却完了");
            return true;
        } else {
            System.out.println("✗ 「" + book.getTitle() + "」は貸出されていません");
            return false;
        }
    }
    
    /**
     * 図書館の現在状況を表示します
     */
    public void displayStatus() {
        int availableBooks = 0;
        int rentedBooks = 0;
        
        for (Book book : books) {
            if (book.isRented()) {
                rentedBooks++;
            } else {
                availableBooks++;
            }
        }
        
        System.out.println("=== " + libraryName + "の状況 ===");
        System.out.println("登録書籍数: " + books.size() + "冊");
        System.out.println("貸出中: " + rentedBooks + "冊");
        System.out.println("利用可能: " + availableBooks + "冊");
        System.out.println();
    }
    
    /**
     * 貸出中の書籍一覧を表示します
     */
    public void displayRentedBooks() {
        System.out.println("=== 貸出中の書籍 ===");
        boolean hasRentedBooks = false;
        
        for (Book book : books) {
            if (book.isRented()) {
                System.out.printf("- %s (貸出者: %s, 貸出日: %s)%n", 
                    book.getTitle(), 
                    book.getRenterName(),
                    book.getRentDate().format(DateTimeFormatter.ofPattern("MM/dd")));
                hasRentedBooks = true;
            }
        }
        
        if (!hasRentedBooks) {
            System.out.println("現在貸出中の書籍はありません。");
        }
        System.out.println();
    }
    
    /**
     * 利用可能な書籍一覧を表示します
     */
    public void displayAvailableBooks() {
        System.out.println("=== 利用可能な書籍 ===");
        boolean hasAvailableBooks = false;
        
        for (Book book : books) {
            if (!book.isRented()) {
                book.displayInfo();
                hasAvailableBooks = true;
            }
        }
        
        if (!hasAvailableBooks) {
            System.out.println("現在利用可能な書籍はありません。");
        }
        System.out.println();
    }
    
    /**
     * カテゴリ別統計を表示します
     */
    public void displayCategoryStatistics() {
        System.out.println("=== カテゴリ別統計 ===");
        
        // カテゴリごとの冊数を集計
        java.util.Map<String, Integer> categoryCount = new java.util.HashMap<>();
        java.util.Map<String, Integer> categoryRented = new java.util.HashMap<>();
        
        for (Book book : books) {
            String category = book.getCategory();
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            
            if (book.isRented()) {
                categoryRented.put(category, categoryRented.getOrDefault(category, 0) + 1);
            }
        }
        
        for (String category : categoryCount.keySet()) {
            int total = categoryCount.get(category);
            int rented = categoryRented.getOrDefault(category, 0);
            int available = total - rented;
            
            System.out.printf("%s: %d冊 (貸出中: %d, 利用可能: %d)%n", 
                category, total, rented, available);
        }
        System.out.println();
    }
    
    /**
     * 図書館の総合統計を表示します
     */
    public void displayStatistics() {
        displayStatus();
        
        System.out.println("=== 利用統計 ===");
        System.out.println("総貸出回数: " + totalRents + "回");
        System.out.println("総返却回数: " + totalReturns + "回");
        System.out.println("現在の貸出中書籍: " + (totalRents - totalReturns) + "冊");
        
        if (books.size() > 0) {
            double utilizationRate = (double) (totalRents - totalReturns) / books.size() * 100;
            System.out.printf("利用率: %.1f%%%n", utilizationRate);
        }
        
        System.out.println();
        displayCategoryStatistics();
    }
    
    // ゲッターメソッド
    public String getLibraryName() { return libraryName; }
    public int getTotalBooks() { return books.size(); }
    public int getTotalRents() { return totalRents; }
    public int getTotalReturns() { return totalReturns; }
}

/**
 * メインクラス - 図書管理システムのデモンストレーション
 */
public class LibraryManagement {
    public static void main(String[] args) {
        // 図書館を作成
        Library library = new Library("中央図書館");
        
        System.out.println("=== 図書管理システム ===");
        System.out.println();
        System.out.println(library.getLibraryName() + "にようこそ！");
        System.out.println();
        
        // 初期書籍データの準備
        Book[] initialBooks = {
            new Book("Java入門", "山田太郎", "978-1234567890", "プログラミング"),
            new Book("データ構造とアルゴリズム", "佐藤花子", "978-0987654321", "コンピュータサイエンス"),
            new Book("デザインパターン", "田中次郎", "978-1122334455", "ソフトウェア工学"),
            new Book("春の文学", "鈴木美咲", "978-2233445566", "文学"),
            new Book("世界史概論", "高橋研二", "978-3344556677", "歴史"),
            new Book("基礎物理学", "伊藤博士", "978-4455667788", "理科")
        };
        
        // 書籍を図書館に追加
        library.addBooks(initialBooks);
        
        // 現在の状況表示
        library.displayStatus();
        
        // 書籍検索のデモンストレーション
        System.out.println("=== 書籍検索 ===");
        System.out.println("「Java」で検索:");
        ArrayList<Book> searchResults = library.searchBooks("Java");
        for (Book book : searchResults) {
            book.displayInfo();
        }
        System.out.println();
        
        // カテゴリ検索のデモンストレーション
        System.out.println("カテゴリ「プログラミング」で検索:");
        ArrayList<Book> categoryResults = library.searchByCategory("プログラミング");
        for (Book book : categoryResults) {
            book.displayInfo();
        }
        System.out.println();
        
        // 貸出処理のデモンストレーション
        System.out.println("=== 貸出処理 ===");
        library.rentBook("978-1234567890", "田中さん");
        library.rentBook("978-0987654321", "佐藤さん");
        library.rentBook("978-2233445566", "山田さん");
        System.out.println();
        
        // 現在の状況表示
        library.displayStatus();
        library.displayRentedBooks();
        
        // 返却処理のデモンストレーション
        System.out.println("=== 返却処理 ===");
        library.returnBook("978-1234567890");
        System.out.println();
        
        // 利用可能書籍の表示
        library.displayAvailableBooks();
        
        // 統計情報の表示
        library.displayStatistics();
        
        // 書籍詳細情報の表示例
        System.out.println("=== 書籍詳細情報 ===");
        Book javaBook = library.findBookByIsbn("978-1234567890");
        if (javaBook != null) {
            javaBook.displayDetailedInfo();
        }
        
        System.out.println("図書管理システムのデモンストレーションを終了します。");
    }
}

/*
 * 実行結果例:
 * 
 * === 図書管理システム ===
 * 
 * 中央図書館にようこそ！
 * 
 * 書籍を追加しています...
 * ✓ 「Java入門」- 山田太郎 (ISBN: 978-1234567890) を追加
 * ✓ 「データ構造とアルゴリズム」- 佐藤花子 (ISBN: 978-0987654321) を追加
 * ✓ 「デザインパターン」- 田中次郎 (ISBN: 978-1122334455) を追加
 * ✓ 「春の文学」- 鈴木美咲 (ISBN: 978-2233445566) を追加
 * ✓ 「世界史概論」- 高橋研二 (ISBN: 978-3344556677) を追加
 * ✓ 「基礎物理学」- 伊藤博士 (ISBN: 978-4455667788) を追加
 * 
 * === 中央図書館の状況 ===
 * 登録書籍数: 6冊
 * 貸出中: 0冊
 * 利用可能: 6冊
 * 
 * === 書籍検索 ===
 * 「Java」で検索:
 * [利用可能] Java入門 - 山田太郎 (ISBN: 978-1234567890) [プログラミング]
 * 
 * カテゴリ「プログラミング」で検索:
 * [利用可能] Java入門 - 山田太郎 (ISBN: 978-1234567890) [プログラミング]
 * 
 * === 貸出処理 ===
 * ✓ 「Java入門」を田中さんに貸出完了
 * ✓ 「データ構造とアルゴリズム」を佐藤さんに貸出完了
 * ✓ 「春の文学」を山田さんに貸出完了
 * 
 * === 中央図書館の状況 ===
 * 登録書籍数: 6冊
 * 貸出中: 3冊
 * 利用可能: 3冊
 * 
 * === 貸出中の書籍 ===
 * - Java入門 (貸出者: 田中さん, 貸出日: 07/05)
 * - データ構造とアルゴリズム (貸出者: 佐藤さん, 貸出日: 07/05)
 * - 春の文学 (貸出者: 山田さん, 貸出日: 07/05)
 * 
 * 学習のポイント:
 * 1. クラス設計: Book と Library の役割分担
 * 2. カプセル化: private フィールドと public メソッド
 * 3. オブジェクトの状態: 貸出状況の管理
 * 4. ArrayList: 複数のオブジェクトの管理
 * 5. 検索機能: 様々な条件での書籍検索
 * 6. 実用性: 実際の図書館システムに近い機能
 */