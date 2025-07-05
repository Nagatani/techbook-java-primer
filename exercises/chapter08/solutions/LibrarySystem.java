package chapter08.solutions;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 図書館管理システムのクラス
 * 
 * 本の管理、貸出・返却処理、利用者管理などを行います。
 * 適切なコレクションクラスを使用して効率的な処理を実現します。
 */
public class LibrarySystem {
    
    /**
     * 本を表すクラス
     */
    public static class Book {
        private String isbn;
        private String title;
        private String author;
        private String category;
        private boolean isAvailable;
        private LocalDate publishDate;
        
        public Book(String isbn, String title, String author, String category, LocalDate publishDate) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.category = category;
            this.isAvailable = true;
            this.publishDate = publishDate;
        }
        
        // Getters and Setters
        public String getIsbn() { return isbn; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getCategory() { return category; }
        public boolean isAvailable() { return isAvailable; }
        public LocalDate getPublishDate() { return publishDate; }
        
        public void setAvailable(boolean available) { this.isAvailable = available; }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Book book = (Book) obj;
            return Objects.equals(isbn, book.isbn);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(isbn);
        }
        
        @Override
        public String toString() {
            return String.format("Book{isbn='%s', title='%s', author='%s', category='%s', available=%s}",
                               isbn, title, author, category, isAvailable);
        }
    }
    
    /**
     * 貸出記録を表すクラス
     */
    public static class LoanRecord {
        private String userId;
        private String isbn;
        private LocalDate loanDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        
        public LoanRecord(String userId, String isbn, LocalDate loanDate, LocalDate dueDate) {
            this.userId = userId;
            this.isbn = isbn;
            this.loanDate = loanDate;
            this.dueDate = dueDate;
        }
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public String getIsbn() { return isbn; }
        public LocalDate getLoanDate() { return loanDate; }
        public LocalDate getDueDate() { return dueDate; }
        public LocalDate getReturnDate() { return returnDate; }
        
        public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
        
        public boolean isReturned() { return returnDate != null; }
        
        public boolean isOverdue() {
            return !isReturned() && LocalDate.now().isAfter(dueDate);
        }
        
        public long getOverdueDays() {
            if (!isOverdue()) return 0;
            return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }
        
        @Override
        public String toString() {
            return String.format("LoanRecord{userId='%s', isbn='%s', loanDate=%s, dueDate=%s, returnDate=%s}",
                               userId, isbn, loanDate, dueDate, returnDate);
        }
    }
    
    /**
     * 利用者を表すクラス
     */
    public static class User {
        private String userId;
        private String name;
        private String email;
        private UserType userType;
        
        public enum UserType {
            STUDENT(14),    // 学生：14日間
            FACULTY(30),    // 教員：30日間
            GENERAL(7);     // 一般：7日間
            
            private final int maxLoanDays;
            
            UserType(int maxLoanDays) {
                this.maxLoanDays = maxLoanDays;
            }
            
            public int getMaxLoanDays() { return maxLoanDays; }
        }
        
        public User(String userId, String name, String email, UserType userType) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.userType = userType;
        }
        
        // Getters
        public String getUserId() { return userId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public UserType getUserType() { return userType; }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            User user = (User) obj;
            return Objects.equals(userId, user.userId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(userId);
        }
        
        @Override
        public String toString() {
            return String.format("User{userId='%s', name='%s', email='%s', userType=%s}",
                               userId, name, email, userType);
        }
    }
    
    // 蔵書管理（ISBN -> Book）
    private Map<String, Book> books;
    
    // 利用者管理（UserID -> User）
    private Map<String, User> users;
    
    // 貸出記録管理（UUID -> LoanRecord）
    private Map<String, LoanRecord> loanRecords;
    
    // 現在の貸出状況（UserID -> List<LoanRecord>）
    private Map<String, List<LoanRecord>> currentLoans;
    
    // カテゴリ別の本管理（Category -> List<Book>）
    private Map<String, List<Book>> booksByCategory;
    
    // 著者別の本管理（Author -> List<Book>）
    private Map<String, List<Book>> booksByAuthor;
    
    // 貸出待ちキュー（ISBN -> Queue<UserID>）
    private Map<String, Queue<String>> reservationQueues;
    
    /**
     * LibrarySystemのコンストラクタ
     */
    public LibrarySystem() {
        books = new HashMap<>();
        users = new HashMap<>();
        loanRecords = new HashMap<>();
        currentLoans = new HashMap<>();
        booksByCategory = new HashMap<>();
        booksByAuthor = new HashMap<>();
        reservationQueues = new HashMap<>();
    }
    
    /**
     * 本を追加する
     * 
     * @param book 追加する本
     * @return 追加に成功した場合true
     */
    public boolean addBook(Book book) {
        if (book == null || books.containsKey(book.getIsbn())) {
            return false;
        }
        
        books.put(book.getIsbn(), book);
        
        // カテゴリ別・著者別インデックスに追加
        booksByCategory.computeIfAbsent(book.getCategory(), k -> new ArrayList<>()).add(book);
        booksByAuthor.computeIfAbsent(book.getAuthor(), k -> new ArrayList<>()).add(book);
        
        return true;
    }
    
    /**
     * 利用者を追加する
     * 
     * @param user 追加する利用者
     * @return 追加に成功した場合true
     */
    public boolean addUser(User user) {
        if (user == null || users.containsKey(user.getUserId())) {
            return false;
        }
        
        users.put(user.getUserId(), user);
        return true;
    }
    
    /**
     * 本を貸し出す
     * 
     * @param userId 利用者ID
     * @param isbn 本のISBN
     * @return 貸出に成功した場合true
     */
    public boolean loanBook(String userId, String isbn) {
        User user = users.get(userId);
        Book book = books.get(isbn);
        
        if (user == null || book == null || !book.isAvailable()) {
            return false;
        }
        
        // 貸出期限を計算
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(user.getUserType().getMaxLoanDays());
        
        // 貸出記録を作成
        String recordId = UUID.randomUUID().toString();
        LoanRecord record = new LoanRecord(userId, isbn, loanDate, dueDate);
        loanRecords.put(recordId, record);
        
        // 現在の貸出状況を更新
        currentLoans.computeIfAbsent(userId, k -> new ArrayList<>()).add(record);
        
        // 本を貸出中に設定
        book.setAvailable(false);
        
        return true;
    }
    
    /**
     * 本を返却する
     * 
     * @param userId 利用者ID
     * @param isbn 本のISBN
     * @return 返却に成功した場合true
     */
    public boolean returnBook(String userId, String isbn) {
        List<LoanRecord> userLoans = currentLoans.get(userId);
        if (userLoans == null) {
            return false;
        }
        
        LoanRecord targetRecord = null;
        for (LoanRecord record : userLoans) {
            if (record.getIsbn().equals(isbn) && !record.isReturned()) {
                targetRecord = record;
                break;
            }
        }
        
        if (targetRecord == null) {
            return false;
        }
        
        // 返却日を設定
        targetRecord.setReturnDate(LocalDate.now());
        
        // 現在の貸出リストから削除
        userLoans.remove(targetRecord);
        
        // 本を利用可能に設定
        Book book = books.get(isbn);
        if (book != null) {
            book.setAvailable(true);
            
            // 予約待ちがある場合は自動的に次の人に貸出
            processReservationQueue(isbn);
        }
        
        return true;
    }
    
    /**
     * 本を予約する
     * 
     * @param userId 利用者ID
     * @param isbn 本のISBN
     * @return 予約に成功した場合true
     */
    public boolean reserveBook(String userId, String isbn) {
        User user = users.get(userId);
        Book book = books.get(isbn);
        
        if (user == null || book == null || book.isAvailable()) {
            return false;
        }
        
        Queue<String> queue = reservationQueues.computeIfAbsent(isbn, k -> new LinkedList<>());
        
        // 既に予約済みの場合は失敗
        if (queue.contains(userId)) {
            return false;
        }
        
        queue.offer(userId);
        return true;
    }
    
    /**
     * 予約待ちキューを処理する
     * 
     * @param isbn 本のISBN
     */
    private void processReservationQueue(String isbn) {
        Queue<String> queue = reservationQueues.get(isbn);
        if (queue != null && !queue.isEmpty()) {
            String nextUserId = queue.poll();
            loanBook(nextUserId, isbn);
        }
    }
    
    /**
     * 本を検索する（タイトル）
     * 
     * @param title タイトル（部分一致）
     * @return 見つかった本のリスト
     */
    public List<Book> searchBooksByTitle(String title) {
        return books.values().stream()
                   .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                   .collect(Collectors.toList());
    }
    
    /**
     * 本を検索する（著者）
     * 
     * @param author 著者名
     * @return 見つかった本のリスト
     */
    public List<Book> searchBooksByAuthor(String author) {
        return booksByAuthor.getOrDefault(author, new ArrayList<>());
    }
    
    /**
     * 本を検索する（カテゴリ）
     * 
     * @param category カテゴリ名
     * @return 見つかった本のリスト
     */
    public List<Book> searchBooksByCategory(String category) {
        return booksByCategory.getOrDefault(category, new ArrayList<>());
    }
    
    /**
     * 利用者の現在の貸出状況を取得する
     * 
     * @param userId 利用者ID
     * @return 現在の貸出記録のリスト
     */
    public List<LoanRecord> getCurrentLoans(String userId) {
        return currentLoans.getOrDefault(userId, new ArrayList<>());
    }
    
    /**
     * 延滞中の貸出記録を取得する
     * 
     * @return 延滞中の貸出記録のリスト
     */
    public List<LoanRecord> getOverdueLoans() {
        return currentLoans.values().stream()
                          .flatMap(List::stream)
                          .filter(LoanRecord::isOverdue)
                          .collect(Collectors.toList());
    }
    
    /**
     * 人気の本を取得する（貸出回数順）
     * 
     * @param limit 取得する本の数
     * @return 人気の本のリスト
     */
    public List<Book> getPopularBooks(int limit) {
        Map<String, Long> loanCounts = loanRecords.values().stream()
                                                 .collect(Collectors.groupingBy(
                                                     LoanRecord::getIsbn,
                                                     Collectors.counting()
                                                 ));
        
        return loanCounts.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(limit)
                        .map(entry -> books.get(entry.getKey()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }
    
    /**
     * 統計情報を取得する
     * 
     * @return 統計情報
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalBooks", books.size());
        stats.put("totalUsers", users.size());
        stats.put("currentLoans", currentLoans.values().stream().mapToInt(List::size).sum());
        stats.put("overdueLoans", getOverdueLoans().size());
        stats.put("totalCategories", booksByCategory.size());
        stats.put("totalAuthors", booksByAuthor.size());
        
        return stats;
    }
    
    /**
     * 利用者の貸出履歴を取得する
     * 
     * @param userId 利用者ID
     * @return 貸出履歴のリスト
     */
    public List<LoanRecord> getUserLoanHistory(String userId) {
        return loanRecords.values().stream()
                         .filter(record -> record.getUserId().equals(userId))
                         .sorted(Comparator.comparing(LoanRecord::getLoanDate).reversed())
                         .collect(Collectors.toList());
    }
    
    /**
     * 本の貸出履歴を取得する
     * 
     * @param isbn 本のISBN
     * @return 貸出履歴のリスト
     */
    public List<LoanRecord> getBookLoanHistory(String isbn) {
        return loanRecords.values().stream()
                         .filter(record -> record.getIsbn().equals(isbn))
                         .sorted(Comparator.comparing(LoanRecord::getLoanDate).reversed())
                         .collect(Collectors.toList());
    }
    
    // Getter methods
    public Book getBook(String isbn) { return books.get(isbn); }
    public User getUser(String userId) { return users.get(userId); }
    public Collection<Book> getAllBooks() { return books.values(); }
    public Collection<User> getAllUsers() { return users.values(); }
    public Set<String> getAllCategories() { return booksByCategory.keySet(); }
    public Set<String> getAllAuthors() { return booksByAuthor.keySet(); }
}