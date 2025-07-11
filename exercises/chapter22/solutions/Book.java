package chapter22.solutions;

import java.util.Objects;

/**
 * 図書館システムにおける本を表すクラス。
 * <p>
 * このクラスは図書館で管理される本の情報を保持し、
 * 貸出・返却の状態を管理します。
 * </p>
 * 
 * <h2>使用例:</h2>
 * <pre>{@code
 * Book book = new Book("978-4-7741-9308-7", "Javaプログラミング入門", "山田太郎", 2023);
 * if (book.isAvailable()) {
 *     book.borrowBook();
 *     System.out.println("本を借りました: " + book.getTitle());
 * }
 * }</pre>
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 * @since 2024-01-01
 */
public class Book {
    /** 本のISBN（国際標準図書番号） */
    private final String isbn;
    
    /** 本のタイトル */
    private String title;
    
    /** 著者名 */
    private String author;
    
    /** 出版年 */
    private int publicationYear;
    
    /** 貸出可能状態（true: 貸出可能、false: 貸出中） */
    private boolean available;
    
    /**
     * Bookオブジェクトを構築します。
     * <p>
     * 新しく作成された本は自動的に貸出可能状態になります。
     * </p>
     * 
     * @param isbn 本のISBN（null不可、形式: XXX-X-XXXX-XXXX-X）
     * @param title 本のタイトル（null不可、空文字列不可）
     * @param author 著者名（null不可、空文字列不可）
     * @param publicationYear 出版年（1900年以降の値）
     * @throws IllegalArgumentException 引数が不正な場合
     * @throws NullPointerException 引数がnullの場合
     */
    public Book(String isbn, String title, String author, int publicationYear) {
        Objects.requireNonNull(isbn, "ISBNはnullにできません");
        Objects.requireNonNull(title, "タイトルはnullにできません");
        Objects.requireNonNull(author, "著者名はnullにできません");
        
        if (isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBNは空にできません");
        }
        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは空にできません");
        }
        if (author.trim().isEmpty()) {
            throw new IllegalArgumentException("著者名は空にできません");
        }
        if (publicationYear < 1900) {
            throw new IllegalArgumentException("出版年は1900年以降である必要があります");
        }
        
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.available = true;
    }
    
    /**
     * 本を借りる処理を行います。
     * <p>
     * この本が貸出可能な場合、貸出中状態に変更します。
     * すでに貸出中の場合は{@link IllegalStateException}をスローします。
     * </p>
     * 
     * @throws IllegalStateException この本がすでに貸出中の場合
     * @see #returnBook()
     * @see #isAvailable()
     */
    public void borrowBook() {
        if (!available) {
            throw new IllegalStateException("この本はすでに貸出中です: " + title);
        }
        available = false;
    }
    
    /**
     * 本を返却する処理を行います。
     * <p>
     * この本が貸出中の場合、貸出可能状態に変更します。
     * すでに貸出可能な場合は{@link IllegalStateException}をスローします。
     * </p>
     * 
     * @throws IllegalStateException この本がすでに貸出可能な場合
     * @see #borrowBook()
     * @see #isAvailable()
     */
    public void returnBook() {
        if (available) {
            throw new IllegalStateException("この本はすでに返却されています: " + title);
        }
        available = true;
    }
    
    /**
     * 本のISBNを取得します。
     * 
     * @return この本のISBN（nullは返されません）
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * 本のタイトルを取得します。
     * 
     * @return この本のタイトル（nullは返されません）
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * 本のタイトルを設定します。
     * 
     * @param title 新しいタイトル（null不可、空文字列不可）
     * @throws IllegalArgumentException titleが空文字列の場合
     * @throws NullPointerException titleがnullの場合
     */
    public void setTitle(String title) {
        Objects.requireNonNull(title, "タイトルはnullにできません");
        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは空にできません");
        }
        this.title = title;
    }
    
    /**
     * 著者名を取得します。
     * 
     * @return この本の著者名（nullは返されません）
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * 著者名を設定します。
     * 
     * @param author 新しい著者名（null不可、空文字列不可）
     * @throws IllegalArgumentException authorが空文字列の場合
     * @throws NullPointerException authorがnullの場合
     */
    public void setAuthor(String author) {
        Objects.requireNonNull(author, "著者名はnullにできません");
        if (author.trim().isEmpty()) {
            throw new IllegalArgumentException("著者名は空にできません");
        }
        this.author = author;
    }
    
    /**
     * 出版年を取得します。
     * 
     * @return この本の出版年
     */
    public int getPublicationYear() {
        return publicationYear;
    }
    
    /**
     * 出版年を設定します。
     * 
     * @param publicationYear 新しい出版年（1900年以降）
     * @throws IllegalArgumentException publicationYearが1900年未満の場合
     */
    public void setPublicationYear(int publicationYear) {
        if (publicationYear < 1900) {
            throw new IllegalArgumentException("出版年は1900年以降である必要があります");
        }
        this.publicationYear = publicationYear;
    }
    
    /**
     * 本が貸出可能かどうかを確認します。
     * 
     * @return 貸出可能な場合true、貸出中の場合false
     */
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * この本の文字列表現を返します。
     * <p>
     * 形式: "Book[isbn=XXX, title=XXX, author=XXX, year=XXXX, available=true/false]"
     * </p>
     * 
     * @return この本の文字列表現
     */
    @Override
    public String toString() {
        return String.format("Book[isbn=%s, title=%s, author=%s, year=%d, available=%s]",
                isbn, title, author, publicationYear, available);
    }
    
    /**
     * この本と指定されたオブジェクトが等しいかどうかを判定します。
     * <p>
     * ISBNが同じ場合、本は等しいと見なされます。
     * </p>
     * 
     * @param obj 比較対象のオブジェクト
     * @return この本が指定されたオブジェクトと等しい場合はtrue
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return Objects.equals(isbn, book.isbn);
    }
    
    /**
     * この本のハッシュコードを返します。
     * <p>
     * ハッシュコードはISBNに基づいて計算されます。
     * </p>
     * 
     * @return この本のハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}