package chapter22.solutions;

/**
 * 本が貸出可能でない場合にスローされる例外。
 * <p>
 * この例外は、すでに貸出中の本を借りようとした場合にスローされます。
 * </p>
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 * @since 2024-01-01
 * @see Library#borrowBook(String, String)
 */
public class BookNotAvailableException extends Exception {
    
    /** シリアライゼーションバージョンUID */
    private static final long serialVersionUID = 1L;
    
    /** 貸出できなかった本のISBN */
    private final String isbn;
    
    /**
     * 指定されたメッセージとISBNを持つ例外を構築します。
     * 
     * @param message エラーメッセージ
     * @param isbn 貸出できなかった本のISBN
     */
    public BookNotAvailableException(String message, String isbn) {
        super(message);
        this.isbn = isbn;
    }
    
    /**
     * 指定されたメッセージ、ISBN、および原因となった例外を持つ例外を構築します。
     * 
     * @param message エラーメッセージ
     * @param isbn 貸出できなかった本のISBN
     * @param cause 原因となった例外
     */
    public BookNotAvailableException(String message, String isbn, Throwable cause) {
        super(message, cause);
        this.isbn = isbn;
    }
    
    /**
     * 貸出できなかった本のISBNを取得します。
     * 
     * @return 本のISBN
     */
    public String getIsbn() {
        return isbn;
    }
}