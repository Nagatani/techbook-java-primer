package chapter22.solutions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 図書館システムの中核となるクラス。
 * <p>
 * このクラスは本の管理、貸出・返却処理、検索機能を提供します。
 * スレッドセーフな実装となっており、複数のユーザーが同時にアクセスしても
 * 安全に動作します。
 * </p>
 * 
 * <h2>主な機能:</h2>
 * <ul>
 *   <li>本の追加・削除</li>
 *   <li>ISBNや著者名による検索</li>
 *   <li>ユーザーごとの貸出・返却管理</li>
 *   <li>貸出履歴の追跡</li>
 * </ul>
 * 
 * <h2>使用例:</h2>
 * <pre>{@code
 * Library library = new Library();
 * 
 * // 本を追加
 * Book book = new Book("978-4-7741-9308-7", "Javaプログラミング入門", "山田太郎", 2023);
 * library.addBook(book);
 * 
 * // 本を借りる
 * try {
 *     library.borrowBook("user123", "978-4-7741-9308-7");
 *     System.out.println("本を借りました");
 * } catch (BookNotAvailableException e) {
 *     System.err.println("本は貸出中です: " + e.getMessage());
 * }
 * 
 * // 本を返す
 * library.returnBook("user123", "978-4-7741-9308-7");
 * }</pre>
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 * @since 2024-01-01
 * @see Book
 * @see BookNotAvailableException
 */
public class Library {
    
    /** ISBN をキーとして本を管理するマップ（スレッドセーフ） */
    private final Map<String, Book> books;
    
    /** ユーザーIDをキーとして、借りている本のリストを管理するマップ（スレッドセーフ） */
    private final Map<String, List<Book>> borrowedBooks;
    
    /**
     * 空の図書館インスタンスを作成します。
     * <p>
     * 内部のデータ構造はスレッドセーフな実装を使用します。
     * </p>
     */
    public Library() {
        this.books = new ConcurrentHashMap<>();
        this.borrowedBooks = new ConcurrentHashMap<>();
    }
    
    /**
     * 図書館に本を追加します。
     * <p>
     * 同じISBNの本がすでに存在する場合は、新しい本で上書きされます。
     * </p>
     * 
     * @param book 追加する本（null不可）
     * @throws NullPointerException bookがnullの場合
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * Book newBook = new Book("978-4-7741-9308-7", "Java入門", "著者名", 2023);
     * library.addBook(newBook);
     * }</pre>
     */
    public void addBook(Book book) {
        Objects.requireNonNull(book, "追加する本はnullにできません");
        books.put(book.getIsbn(), book);
    }
    
    /**
     * 指定されたISBNの本を図書館から削除します。
     * <p>
     * 本が貸出中の場合でも削除されます。削除された本は返却できなくなります。
     * </p>
     * 
     * @param isbn 削除する本のISBN
     * @return 削除された本、存在しない場合はnull
     */
    public Book removeBook(String isbn) {
        return books.remove(isbn);
    }
    
    /**
     * ISBNを使用して本を検索します。
     * 
     * @param isbn 検索する本のISBN（null不可）
     * @return 見つかった本、存在しない場合はnull
     * @throws NullPointerException isbnがnullの場合
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * Book book = library.findBookByIsbn("978-4-7741-9308-7");
     * if (book != null) {
     *     System.out.println("見つかりました: " + book.getTitle());
     * }
     * }</pre>
     */
    public Book findBookByIsbn(String isbn) {
        Objects.requireNonNull(isbn, "ISBNはnullにできません");
        return books.get(isbn);
    }
    
    /**
     * 著者名で本を検索します。
     * <p>
     * 大文字小文字を区別せず、部分一致で検索します。
     * </p>
     * 
     * @param author 検索する著者名（null不可）
     * @return 著者名が一致する本のリスト（見つからない場合は空のリスト）
     * @throws NullPointerException authorがnullの場合
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * List<Book> books = library.findBooksByAuthor("山田");
     * books.forEach(book -> System.out.println(book.getTitle()));
     * }</pre>
     */
    public List<Book> findBooksByAuthor(String author) {
        Objects.requireNonNull(author, "著者名はnullにできません");
        String lowerAuthor = author.toLowerCase();
        
        return books.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(lowerAuthor))
                .collect(Collectors.toList());
    }
    
    /**
     * タイトルで本を検索します。
     * <p>
     * 大文字小文字を区別せず、部分一致で検索します。
     * </p>
     * 
     * @param title 検索するタイトル（null不可）
     * @return タイトルが一致する本のリスト（見つからない場合は空のリスト）
     * @throws NullPointerException titleがnullの場合
     * @since 1.1
     */
    public List<Book> findBooksByTitle(String title) {
        Objects.requireNonNull(title, "タイトルはnullにできません");
        String lowerTitle = title.toLowerCase();
        
        return books.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerTitle))
                .collect(Collectors.toList());
    }
    
    /**
     * ユーザーが本を借りる処理を行います。
     * <p>
     * 本が貸出可能な場合のみ貸出処理を行います。
     * ユーザーIDは自動的に管理され、初めてのユーザーでも使用できます。
     * </p>
     * 
     * @param userId 借りるユーザーのID（null不可、空文字列不可）
     * @param isbn 借りる本のISBN（null不可）
     * @throws BookNotAvailableException 本が貸出中または存在しない場合
     * @throws IllegalArgumentException userIdが空文字列の場合
     * @throws NullPointerException 引数がnullの場合
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * try {
     *     library.borrowBook("user123", "978-4-7741-9308-7");
     *     System.out.println("貸出成功");
     * } catch (BookNotAvailableException e) {
     *     System.err.println("貸出失敗: " + e.getMessage());
     * }
     * }</pre>
     */
    public void borrowBook(String userId, String isbn) throws BookNotAvailableException {
        Objects.requireNonNull(userId, "ユーザーIDはnullにできません");
        Objects.requireNonNull(isbn, "ISBNはnullにできません");
        
        if (userId.trim().isEmpty()) {
            throw new IllegalArgumentException("ユーザーIDは空にできません");
        }
        
        Book book = books.get(isbn);
        if (book == null) {
            throw new BookNotAvailableException("指定された本が存在しません: " + isbn, isbn);
        }
        
        synchronized (book) {
            if (!book.isAvailable()) {
                throw new BookNotAvailableException(
                    "本「" + book.getTitle() + "」は貸出中です", isbn);
            }
            
            book.borrowBook();
            
            // ユーザーの借りている本のリストに追加
            borrowedBooks.computeIfAbsent(userId, k -> new ArrayList<>()).add(book);
        }
    }
    
    /**
     * ユーザーが本を返却する処理を行います。
     * <p>
     * ユーザーが実際に借りている本のみ返却できます。
     * 返却後、本は再び貸出可能になります。
     * </p>
     * 
     * @param userId 返却するユーザーのID（null不可）
     * @param isbn 返却する本のISBN（null不可）
     * @throws IllegalStateException ユーザーがその本を借りていない場合
     * @throws IllegalArgumentException 本が存在しない場合
     * @throws NullPointerException 引数がnullの場合
     * 
     * <h3>使用例:</h3>
     * <pre>{@code
     * library.returnBook("user123", "978-4-7741-9308-7");
     * System.out.println("返却完了");
     * }</pre>
     */
    public void returnBook(String userId, String isbn) {
        Objects.requireNonNull(userId, "ユーザーIDはnullにできません");
        Objects.requireNonNull(isbn, "ISBNはnullにできません");
        
        Book book = books.get(isbn);
        if (book == null) {
            throw new IllegalArgumentException("指定された本が存在しません: " + isbn);
        }
        
        List<Book> userBooks = borrowedBooks.get(userId);
        if (userBooks == null || !userBooks.remove(book)) {
            throw new IllegalStateException(
                "ユーザー " + userId + " は本「" + book.getTitle() + "」を借りていません");
        }
        
        synchronized (book) {
            book.returnBook();
        }
        
        // ユーザーが本を借りていない場合はエントリを削除
        if (userBooks.isEmpty()) {
            borrowedBooks.remove(userId);
        }
    }
    
    /**
     * 指定されたユーザーが借りている本のリストを取得します。
     * 
     * @param userId ユーザーID（null不可）
     * @return ユーザーが借りている本のリスト（変更不可）。借りていない場合は空のリスト
     * @throws NullPointerException userIdがnullの場合
     * @since 1.1
     */
    public List<Book> getBorrowedBooks(String userId) {
        Objects.requireNonNull(userId, "ユーザーIDはnullにできません");
        List<Book> userBooks = borrowedBooks.get(userId);
        return userBooks != null ? 
            Collections.unmodifiableList(new ArrayList<>(userBooks)) : 
            Collections.emptyList();
    }
    
    /**
     * 現在貸出可能な本のリストを取得します。
     * 
     * @return 貸出可能な本のリスト（変更不可）
     * @since 1.1
     */
    public List<Book> getAvailableBooks() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }
    
    /**
     * 図書館の統計情報を取得します。
     * 
     * @return 統計情報を含むマップ
     * @since 1.2
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", books.size());
        stats.put("availableBooks", getAvailableBooks().size());
        stats.put("borrowedBooks", books.size() - getAvailableBooks().size());
        stats.put("activeUsers", borrowedBooks.size());
        return Collections.unmodifiableMap(stats);
    }
    
    /**
     * 図書館の現在の状態を文字列で返します。
     * 
     * @return 図書館の状態を表す文字列
     */
    @Override
    public String toString() {
        Map<String, Object> stats = getStatistics();
        return String.format("Library[総蔵書数=%d, 貸出可能=%d, 貸出中=%d, アクティブユーザー=%d]",
                stats.get("totalBooks"),
                stats.get("availableBooks"),
                stats.get("borrowedBooks"),
                stats.get("activeUsers"));
    }
}