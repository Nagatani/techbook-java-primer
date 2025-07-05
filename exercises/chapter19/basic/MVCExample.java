/**
 * 第19章 課題4: MVCパターンの実装例
 * 
 * MVCパターンを使った書籍管理システムを作成してください。
 * Model、View、Controllerの役割を明確に分離してください。
 * 
 * 要求仕様:
 * - 書籍情報の管理（追加、編集、削除、検索）
 * - MVCパターンの完全な実装
 * - オブザーバーパターンによる通知機能
 * - データバリデーション
 * - 複数のビューへの対応
 * 
 * 学習ポイント:
 * - MVCパターンの設計原則
 * - 疎結合な設計の実現
 * - オブザーバーパターンの活用
 * - イベント駆動アーキテクチャ
 * - 単一責任の原則
 */

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MVCExample extends JFrame {
    
    // ここに実装してください
    
    // Model（データとビジネスロジック）
    public static class BookModel {
        private List<Book> books;
        private List<ModelObserver> observers;
        
        public BookModel() {
            this.books = new ArrayList<>();
            this.observers = new CopyOnWriteArrayList<>();
            initializeSampleData();
        }
        
        // オブザーバーパターンの実装
        public void addObserver(ModelObserver observer) {
            observers.add(observer);
        }
        
        public void removeObserver(ModelObserver observer) {
            observers.remove(observer);
        }
        
        private void notifyObservers(ModelEvent event) {
            for (ModelObserver observer : observers) {
                observer.modelChanged(event);
            }
        }
        
        // 書籍操作メソッド
        public void addBook(Book book) {
            if (validateBook(book)) {
                books.add(book);
                notifyObservers(new ModelEvent(ModelEvent.Type.BOOK_ADDED, book));
            }
        }
        
        public void updateBook(int index, Book book) {
            if (index >= 0 && index < books.size() && validateBook(book)) {
                Book oldBook = books.get(index);
                books.set(index, book);
                notifyObservers(new ModelEvent(ModelEvent.Type.BOOK_UPDATED, book, oldBook));
            }
        }
        
        public void removeBook(int index) {
            if (index >= 0 && index < books.size()) {
                Book removedBook = books.remove(index);
                notifyObservers(new ModelEvent(ModelEvent.Type.BOOK_REMOVED, removedBook));
            }
        }
        
        public List<Book> getBooks() {
            return new ArrayList<>(books);
        }
        
        public List<Book> searchBooks(String keyword) {
            // TODO: 検索処理を実装
            return new ArrayList<>();
        }
        
        private boolean validateBook(Book book) {
            // TODO: データバリデーションを実装
            return book != null && 
                   book.getTitle() != null && !book.getTitle().trim().isEmpty() &&
                   book.getAuthor() != null && !book.getAuthor().trim().isEmpty();
        }
        
        private void initializeSampleData() {
            // TODO: サンプルデータの初期化を実装
        }
    }
    
    // View（表示）
    public static class BookView extends JPanel implements ModelObserver {
        private BookController controller;
        private JTable bookTable;
        private BookTableModel tableModel;
        private JTextField searchField;
        private JButton addButton, editButton, deleteButton, refreshButton;
        
        public BookView() {
            initializeComponents();
            setupLayout();
            setupEventListeners();
        }
        
        public void setController(BookController controller) {
            this.controller = controller;
        }
        
        private void initializeComponents() {
            // TODO: コンポーネント初期化を実装
        }
        
        private void setupLayout() {
            // TODO: レイアウト設定を実装
        }
        
        private void setupEventListeners() {
            // TODO: イベントリスナー設定を実装
        }
        
        @Override
        public void modelChanged(ModelEvent event) {
            SwingUtilities.invokeLater(() -> {
                switch (event.getType()) {
                    case BOOK_ADDED:
                    case BOOK_UPDATED:
                    case BOOK_REMOVED:
                        refreshTable();
                        break;
                }
            });
        }
        
        private void refreshTable() {
            if (controller != null) {
                tableModel.setBooks(controller.getBooks());
            }
        }
        
        private void showAddBookDialog() {
            // TODO: 書籍追加ダイアログを実装
        }
        
        private void showEditBookDialog() {
            // TODO: 書籍編集ダイアログを実装
        }
        
        private void deleteSelectedBook() {
            // TODO: 選択した書籍の削除を実装
        }
        
        private void performSearch() {
            // TODO: 検索処理を実装
        }
        
        // テーブルモデル
        private class BookTableModel extends AbstractTableModel {
            private final String[] columnNames = {"タイトル", "著者", "出版年", "価格", "在庫"};
            private List<Book> books;
            
            public BookTableModel() {
                this.books = new ArrayList<>();
            }
            
            public void setBooks(List<Book> books) {
                this.books = new ArrayList<>(books);
                fireTableDataChanged();
            }
            
            @Override
            public int getRowCount() {
                return books.size();
            }
            
            @Override
            public int getColumnCount() {
                return columnNames.length;
            }
            
            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }
            
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Book book = books.get(rowIndex);
                switch (columnIndex) {
                    case 0: return book.getTitle();
                    case 1: return book.getAuthor();
                    case 2: return book.getPublicationYear();
                    case 3: return String.format("¥%,d", book.getPrice());
                    case 4: return book.getStock();
                    default: return "";
                }
            }
            
            public Book getBookAt(int index) {
                return books.get(index);
            }
        }
    }
    
    // Controller（制御）
    public static class BookController {
        private BookModel model;
        private BookView view;
        
        public BookController() {
            this.model = new BookModel();
        }
        
        public void setView(BookView view) {
            this.view = view;
            view.setController(this);
            model.addObserver(view);
        }
        
        // 書籍操作の制御
        public void addBook(String title, String author, int publicationYear, int price, int stock) {
            Book book = new Book(title, author, publicationYear, price, stock);
            model.addBook(book);
        }
        
        public void updateBook(int index, String title, String author, int publicationYear, int price, int stock) {
            Book book = new Book(title, author, publicationYear, price, stock);
            model.updateBook(index, book);
        }
        
        public void removeBook(int index) {
            model.removeBook(index);
        }
        
        public List<Book> getBooks() {
            return model.getBooks();
        }
        
        public List<Book> searchBooks(String keyword) {
            return model.searchBooks(keyword);
        }
        
        // ビジネスロジック
        public boolean canDeleteBook(int index) {
            // TODO: 削除可能かどうかの判定を実装
            return true;
        }
        
        public int getTotalValue() {
            // TODO: 総在庫価値の計算を実装
            return 0;
        }
        
        public int getTotalStock() {
            // TODO: 総在庫数の計算を実装
            return 0;
        }
    }
    
    // データモデル
    public static class Book {
        private String title;
        private String author;
        private int publicationYear;
        private int price;
        private int stock;
        
        public Book(String title, String author, int publicationYear, int price, int stock) {
            this.title = title;
            this.author = author;
            this.publicationYear = publicationYear;
            this.price = price;
            this.stock = stock;
        }
        
        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public int getPublicationYear() { return publicationYear; }
        public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
        
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
        
        @Override
        public String toString() {
            return String.format("%s - %s (%d年)", title, author, publicationYear);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            
            Book book = (Book) obj;
            return Objects.equals(title, book.title) && 
                   Objects.equals(author, book.author) &&
                   publicationYear == book.publicationYear;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(title, author, publicationYear);
        }
    }
    
    // オブザーバーパターンのインターフェース
    public interface ModelObserver {
        void modelChanged(ModelEvent event);
    }
    
    // モデルイベント
    public static class ModelEvent {
        public enum Type {
            BOOK_ADDED, BOOK_UPDATED, BOOK_REMOVED
        }
        
        private final Type type;
        private final Book newBook;
        private final Book oldBook;
        
        public ModelEvent(Type type, Book newBook) {
            this(type, newBook, null);
        }
        
        public ModelEvent(Type type, Book newBook, Book oldBook) {
            this.type = type;
            this.newBook = newBook;
            this.oldBook = oldBook;
        }
        
        public Type getType() { return type; }
        public Book getNewBook() { return newBook; }
        public Book getOldBook() { return oldBook; }
    }
    
    public MVCExample() {
        // メインアプリケーションの初期化
        BookController controller = new BookController();
        BookView view = new BookView();
        
        controller.setView(view);
        
        setTitle("書籍管理システム - MVCパターン実装例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        add(view);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MVCExample().setVisible(true);
        });
    }
}

/*
 * 実装のヒント:
 * 
 * 1. MVCパターンの設計原則
 *    - Model: データとビジネスロジックのみ
 *    - View: 表示とユーザー入力のみ
 *    - Controller: ModelとViewの橋渡し
 *    - 依存関係の方向性に注意
 * 
 * 2. オブザーバーパターン
 *    - ModelからViewへの通知
 *    - 複数のViewへの対応
 *    - ConcurrentModificationException回避
 *    - 弱参照の活用
 * 
 * 3. イベント駆動アーキテクチャ
 *    - イベントの種類を明確に定義
 *    - 非同期処理への対応
 *    - イベントの伝播制御
 *    - デッドロックの回避
 * 
 * 4. データバリデーション
 *    - 入力値検証
 *    - ビジネスルール検証
 *    - エラーメッセージの管理
 *    - 国際化対応
 * 
 * 5. テスタビリティ
 *    - 各層の独立性
 *    - モック化の容易さ
 *    - 単体テストの書きやすさ
 *    - 結合テストの実装
 * 
 * よくある間違い:
 * - ViewからModelへの直接アクセス
 * - ControllerにUI処理を含める
 * - Modelにプレゼンテーション処理を含める
 * - オブザーバーパターンの循環参照
 * 
 * 発展課題:
 * - 複数のViewの実装
 * - Command パターンの導入
 * - 状態管理の改善
 * - 非同期処理の導入
 * - プラグインアーキテクチャ
 */