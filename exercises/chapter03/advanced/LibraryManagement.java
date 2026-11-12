/**
 * 第3章 発展課題1: 図書管理システム（解答例）
 *
 * 書籍を表すBookクラスと、複数の書籍を管理するLibraryクラスを組み合わせて、
 * 追加・検索・貸出・返却を行います。
 *
 * 学習ポイント:
 * - 複数のクラスの連携（LibraryがBookのオブジェクトを持つ）
 * - privateフィールドとメソッドによる状態の管理
 * - オブジェクトの配列と、登録した数を数える変数による管理
 *
 * 書籍の数が変わるデータは、第9章で学ぶArrayListを使うと、配列の大きさを気にせずに扱えます。
 * この課題では、第3章で学んだオブジェクトの配列を使います。
 */
public class LibraryManagement {
    public static void main(String[] args) {
        IO.println("=== 図書管理システム ===");
        IO.println();

        Library library = new Library("中央図書館", 10);
        IO.println(library.getName() + "にようこそ！");
        IO.println();

        IO.println("書籍を追加しています...");
        library.addBook(new Book("Java入門", "山田太郎", "978-1234567890"));
        library.addBook(new Book("データ構造とアルゴリズム", "佐藤花子", "978-0987654321"));
        library.addBook(new Book("デザインパターン", "田中次郎", "978-1122334455"));
        IO.println();

        IO.println("=== 図書館の状況 ===");
        library.showStatus();
        IO.println();

        IO.println("=== 書籍検索 ===");
        IO.println("「Java」で検索:");
        library.search("Java");
        IO.println();

        IO.println("=== 貸出処理 ===");
        library.rentBook("Java入門", "田中さん");
        library.rentBook("Java入門", "鈴木さん");
        IO.println();

        IO.println("=== 現在の状況 ===");
        library.showStatus();
        IO.println();
        library.showRentedBooks();
        IO.println();

        IO.println("=== 返却処理 ===");
        library.returnBook("Java入門");
        library.showStatus();
    }
}

/**
 * 書籍を表すクラス
 */
class Book {
    private String title;
    private String author;
    private String isbn;
    private boolean rented;
    private String renterName;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.rented = false;
        this.renterName = null;
    }

    public String getTitle() {
        return title;
    }

    public boolean isRented() {
        return rented;
    }

    public String getRenterName() {
        return renterName;
    }

    /**
     * 貸し出す。すでに貸出中ならfalseを返す
     */
    public boolean rent(String name) {
        if (rented) {
            return false;
        }
        rented = true;
        renterName = name;
        return true;
    }

    /**
     * 返却する。貸出中でなければfalseを返す
     */
    public boolean giveBack() {
        if (!rented) {
            return false;
        }
        rented = false;
        renterName = null;
        return true;
    }

    public String toText() {
        return title + " - " + author + " (ISBN: " + isbn + ")";
    }
}

/**
 * 複数の書籍を管理するクラス
 */
class Library {
    private String name;
    private Book[] books;
    private int count; // 登録した書籍の数

    public Library(String name, int capacity) {
        this.name = name;
        this.books = new Book[capacity];
        this.count = 0;
    }

    public String getName() {
        return name;
    }

    public void addBook(Book book) {
        if (count >= books.length) {
            IO.println("これ以上登録できません: " + book.getTitle());
            return;
        }
        books[count] = book;
        count++;
        IO.println("✓ " + book.toText() + " を追加");
    }

    /**
     * タイトルが一致する書籍を探す。見つからなければnullを返す
     */
    private Book findByTitle(String title) {
        for (int i = 0; i < count; i++) {
            if (books[i].getTitle().equals(title)) {
                return books[i];
            }
        }
        return null;
    }

    public void search(String keyword) {
        int found = 0;
        for (int i = 0; i < count; i++) {
            if (books[i].getTitle().contains(keyword)) {
                String state = books[i].isRented() ? "[貸出中]" : "[利用可能]";
                IO.println(state + " " + books[i].toText());
                found++;
            }
        }
        if (found == 0) {
            IO.println("見つかりませんでした");
        }
    }

    public void rentBook(String title, String renterName) {
        IO.println("「" + title + "」を" + renterName + "に貸出");
        Book book = findByTitle(title);
        if (book == null) {
            IO.println("× その書籍は登録されていません");
        } else if (book.rent(renterName)) {
            IO.println("✓ 貸出完了");
        } else {
            IO.println("× 貸出中のため貸し出せません（貸出者: " + book.getRenterName() + "）");
        }
    }

    public void returnBook(String title) {
        Book book = findByTitle(title);
        if (book != null && book.giveBack()) {
            IO.println("✓ 「" + title + "」が返却されました");
        } else {
            IO.println("× 「" + title + "」は貸出中ではありません");
        }
    }

    public void showStatus() {
        int rentedCount = 0;
        for (int i = 0; i < count; i++) {
            if (books[i].isRented()) {
                rentedCount++;
            }
        }
        IO.println("登録書籍数: " + count + "冊");
        IO.println("貸出中: " + rentedCount + "冊");
        IO.println("利用可能: " + (count - rentedCount) + "冊");
    }

    public void showRentedBooks() {
        IO.println("貸出中の書籍:");
        for (int i = 0; i < count; i++) {
            if (books[i].isRented()) {
                IO.println("- " + books[i].getTitle() + " (貸出者: " + books[i].getRenterName() + ")");
            }
        }
    }
}
