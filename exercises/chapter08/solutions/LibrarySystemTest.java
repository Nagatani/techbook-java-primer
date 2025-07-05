package chapter08.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.*;

/**
 * LibrarySystemクラスのテストクラス
 */
class LibrarySystemTest {
    
    private LibrarySystem library;
    private LibrarySystem.Book book1;
    private LibrarySystem.Book book2;
    private LibrarySystem.Book book3;
    private LibrarySystem.User student;
    private LibrarySystem.User faculty;
    private LibrarySystem.User general;
    
    @BeforeEach
    void setUp() {
        library = new LibrarySystem();
        
        book1 = new LibrarySystem.Book("978-4-12345-678-9", "Java入門", "田中太郎", "プログラミング", LocalDate.of(2020, 1, 1));
        book2 = new LibrarySystem.Book("978-4-12345-679-6", "Python基礎", "佐藤花子", "プログラミング", LocalDate.of(2021, 3, 15));
        book3 = new LibrarySystem.Book("978-4-12345-680-2", "データベース設計", "鈴木次郎", "データベース", LocalDate.of(2019, 6, 30));
        
        student = new LibrarySystem.User("U001", "学生太郎", "student@example.com", LibrarySystem.User.UserType.STUDENT);
        faculty = new LibrarySystem.User("U002", "教員花子", "faculty@example.com", LibrarySystem.User.UserType.FACULTY);
        general = new LibrarySystem.User("U003", "一般次郎", "general@example.com", LibrarySystem.User.UserType.GENERAL);
    }
    
    @Test
    void testBookBasicProperties() {
        assertEquals("978-4-12345-678-9", book1.getIsbn());
        assertEquals("Java入門", book1.getTitle());
        assertEquals("田中太郎", book1.getAuthor());
        assertEquals("プログラミング", book1.getCategory());
        assertTrue(book1.isAvailable());
        assertEquals(LocalDate.of(2020, 1, 1), book1.getPublishDate());
    }
    
    @Test
    void testUserBasicProperties() {
        assertEquals("U001", student.getUserId());
        assertEquals("学生太郎", student.getName());
        assertEquals("student@example.com", student.getEmail());
        assertEquals(LibrarySystem.User.UserType.STUDENT, student.getUserType());
    }
    
    @Test
    void testUserTypeLoanDays() {
        assertEquals(14, LibrarySystem.User.UserType.STUDENT.getMaxLoanDays());
        assertEquals(30, LibrarySystem.User.UserType.FACULTY.getMaxLoanDays());
        assertEquals(7, LibrarySystem.User.UserType.GENERAL.getMaxLoanDays());
    }
    
    @Test
    void testAddBook() {
        assertTrue(library.addBook(book1));
        assertEquals(book1, library.getBook("978-4-12345-678-9"));
        
        // 同じISBNの本は追加できない
        assertFalse(library.addBook(book1));
        
        // nullは追加できない
        assertFalse(library.addBook(null));
    }
    
    @Test
    void testAddUser() {
        assertTrue(library.addUser(student));
        assertEquals(student, library.getUser("U001"));
        
        // 同じIDの利用者は追加できない
        assertFalse(library.addUser(student));
        
        // nullは追加できない
        assertFalse(library.addUser(null));
    }
    
    @Test
    void testLoanBook() {
        library.addBook(book1);
        library.addUser(student);
        
        assertTrue(library.loanBook("U001", "978-4-12345-678-9"));
        assertFalse(book1.isAvailable());
        
        List<LibrarySystem.LoanRecord> loans = library.getCurrentLoans("U001");
        assertEquals(1, loans.size());
        assertEquals("978-4-12345-678-9", loans.get(0).getIsbn());
        
        // 既に貸出中の本は貸し出せない
        assertFalse(library.loanBook("U002", "978-4-12345-678-9"));
    }
    
    @Test
    void testReturnBook() {
        library.addBook(book1);
        library.addUser(student);
        
        // 貸出
        library.loanBook("U001", "978-4-12345-678-9");
        assertFalse(book1.isAvailable());
        
        // 返却
        assertTrue(library.returnBook("U001", "978-4-12345-678-9"));
        assertTrue(book1.isAvailable());
        
        List<LibrarySystem.LoanRecord> loans = library.getCurrentLoans("U001");
        assertEquals(0, loans.size());
    }
    
    @Test
    void testReserveBook() {
        library.addBook(book1);
        library.addUser(student);
        library.addUser(faculty);
        
        // 本を貸出
        library.loanBook("U001", "978-4-12345-678-9");
        
        // 予約
        assertTrue(library.reserveBook("U002", "978-4-12345-678-9"));
        
        // 利用可能な本は予約できない
        library.addBook(book2);
        assertFalse(library.reserveBook("U002", "978-4-12345-679-6"));
        
        // 同じ本を重複予約はできない
        assertFalse(library.reserveBook("U002", "978-4-12345-678-9"));
    }
    
    @Test
    void testSearchBooksByTitle() {
        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        
        List<LibrarySystem.Book> results = library.searchBooksByTitle("Java");
        assertEquals(1, results.size());
        assertEquals("Java入門", results.get(0).getTitle());
        
        results = library.searchBooksByTitle("基礎");
        assertEquals(1, results.size());
        assertEquals("Python基礎", results.get(0).getTitle());
        
        results = library.searchBooksByTitle("存在しない");
        assertEquals(0, results.size());
    }
    
    @Test
    void testSearchBooksByAuthor() {
        library.addBook(book1);
        library.addBook(book2);
        
        List<LibrarySystem.Book> results = library.searchBooksByAuthor("田中太郎");
        assertEquals(1, results.size());
        assertEquals("Java入門", results.get(0).getTitle());
        
        results = library.searchBooksByAuthor("存在しない著者");
        assertEquals(0, results.size());
    }
    
    @Test
    void testSearchBooksByCategory() {
        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        
        List<LibrarySystem.Book> results = library.searchBooksByCategory("プログラミング");
        assertEquals(2, results.size());
        
        results = library.searchBooksByCategory("データベース");
        assertEquals(1, results.size());
        assertEquals("データベース設計", results.get(0).getTitle());
        
        results = library.searchBooksByCategory("存在しないカテゴリ");
        assertEquals(0, results.size());
    }
    
    @Test
    void testGetCurrentLoans() {
        library.addBook(book1);
        library.addBook(book2);
        library.addUser(student);
        
        library.loanBook("U001", "978-4-12345-678-9");
        library.loanBook("U001", "978-4-12345-679-6");
        
        List<LibrarySystem.LoanRecord> loans = library.getCurrentLoans("U001");
        assertEquals(2, loans.size());
        
        List<LibrarySystem.LoanRecord> noLoans = library.getCurrentLoans("U999");
        assertEquals(0, noLoans.size());
    }
    
    @Test
    void testLoanRecordOverdue() {
        LibrarySystem.LoanRecord record = new LibrarySystem.LoanRecord(
            "U001", "978-4-12345-678-9", 
            LocalDate.now().minusDays(10), 
            LocalDate.now().minusDays(1)
        );
        
        assertTrue(record.isOverdue());
        assertFalse(record.isReturned());
        assertEquals(1, record.getOverdueDays());
        
        record.setReturnDate(LocalDate.now());
        assertFalse(record.isOverdue());
        assertTrue(record.isReturned());
    }
    
    @Test
    void testGetOverdueLoans() {
        library.addBook(book1);
        library.addUser(student);
        
        // 貸出期限を過ぎた記録を手動で作成
        LibrarySystem.LoanRecord overdueRecord = new LibrarySystem.LoanRecord(
            "U001", "978-4-12345-678-9",
            LocalDate.now().minusDays(20),
            LocalDate.now().minusDays(1)
        );
        
        // 現在の貸出状況に追加（テスト用）
        library.getCurrentLoans("U001").add(overdueRecord);
        
        List<LibrarySystem.LoanRecord> overdueLoans = library.getOverdueLoans();
        assertEquals(1, overdueLoans.size());
    }
    
    @Test
    void testGetStatistics() {
        library.addBook(book1);
        library.addBook(book2);
        library.addUser(student);
        library.addUser(faculty);
        
        library.loanBook("U001", "978-4-12345-678-9");
        
        Map<String, Object> stats = library.getStatistics();
        assertEquals(2, stats.get("totalBooks"));
        assertEquals(2, stats.get("totalUsers"));
        assertEquals(1, stats.get("currentLoans"));
        assertEquals(1, stats.get("totalCategories"));
        assertEquals(2, stats.get("totalAuthors"));
    }
    
    @Test
    void testGetUserLoanHistory() {
        library.addBook(book1);
        library.addBook(book2);
        library.addUser(student);
        
        library.loanBook("U001", "978-4-12345-678-9");
        library.returnBook("U001", "978-4-12345-678-9");
        library.loanBook("U001", "978-4-12345-679-6");
        
        List<LibrarySystem.LoanRecord> history = library.getUserLoanHistory("U001");
        assertEquals(2, history.size());
        // 新しい貸出記録が最初に来る
        assertEquals("978-4-12345-679-6", history.get(0).getIsbn());
    }
    
    @Test
    void testGetBookLoanHistory() {
        library.addBook(book1);
        library.addUser(student);
        library.addUser(faculty);
        
        library.loanBook("U001", "978-4-12345-678-9");
        library.returnBook("U001", "978-4-12345-678-9");
        library.loanBook("U002", "978-4-12345-678-9");
        
        List<LibrarySystem.LoanRecord> history = library.getBookLoanHistory("978-4-12345-678-9");
        assertEquals(2, history.size());
    }
    
    @Test
    void testGetAllMethods() {
        library.addBook(book1);
        library.addBook(book2);
        library.addUser(student);
        
        assertEquals(2, library.getAllBooks().size());
        assertEquals(1, library.getAllUsers().size());
        assertEquals(1, library.getAllCategories().size());
        assertEquals(2, library.getAllAuthors().size());
    }
    
    @Test
    void testBookEquality() {
        LibrarySystem.Book book1Copy = new LibrarySystem.Book("978-4-12345-678-9", "Java入門", "田中太郎", "プログラミング", LocalDate.of(2020, 1, 1));
        LibrarySystem.Book differentBook = new LibrarySystem.Book("978-4-12345-000-0", "Java入門", "田中太郎", "プログラミング", LocalDate.of(2020, 1, 1));
        
        assertEquals(book1, book1Copy);
        assertNotEquals(book1, differentBook);
        assertEquals(book1.hashCode(), book1Copy.hashCode());
    }
    
    @Test
    void testUserEquality() {
        LibrarySystem.User studentCopy = new LibrarySystem.User("U001", "学生太郎", "student@example.com", LibrarySystem.User.UserType.STUDENT);
        LibrarySystem.User differentUser = new LibrarySystem.User("U002", "学生太郎", "student@example.com", LibrarySystem.User.UserType.STUDENT);
        
        assertEquals(student, studentCopy);
        assertNotEquals(student, differentUser);
        assertEquals(student.hashCode(), studentCopy.hashCode());
    }
    
    @Test
    void testToStringMethods() {
        String bookStr = book1.toString();
        assertTrue(bookStr.contains("978-4-12345-678-9"));
        assertTrue(bookStr.contains("Java入門"));
        assertTrue(bookStr.contains("田中太郎"));
        
        String userStr = student.toString();
        assertTrue(userStr.contains("U001"));
        assertTrue(userStr.contains("学生太郎"));
        assertTrue(userStr.contains("STUDENT"));
        
        LibrarySystem.LoanRecord record = new LibrarySystem.LoanRecord(
            "U001", "978-4-12345-678-9", 
            LocalDate.now(), 
            LocalDate.now().plusDays(14)
        );
        String recordStr = record.toString();
        assertTrue(recordStr.contains("U001"));
        assertTrue(recordStr.contains("978-4-12345-678-9"));
    }
    
    @Test
    void testInvalidOperations() {
        // 存在しない利用者での貸出
        library.addBook(book1);
        assertFalse(library.loanBook("U999", "978-4-12345-678-9"));
        
        // 存在しない本での貸出
        library.addUser(student);
        assertFalse(library.loanBook("U001", "978-4-99999-999-9"));
        
        // 存在しない貸出での返却
        assertFalse(library.returnBook("U001", "978-4-12345-678-9"));
        
        // 存在しない利用者での予約
        assertFalse(library.reserveBook("U999", "978-4-12345-678-9"));
    }
}