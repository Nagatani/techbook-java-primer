/**
 * 第20章 課題3: 書籍管理とMVC（解答例）
 *
 * Model（BookModel）は書籍のリストを持ち、変更をオブザーバー（ModelObserver）に知らせます。画面の部品は知りません。
 * View（BookView）はModelからの通知を受けて表を描き直し、ボタンの操作はControllerに伝えます。
 * Controller（BookController）は、Viewからの操作を受けてModelのメソッドを呼びます。
 */

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class MVCExample extends JFrame {

    // Model（データとビジネスロジック）
    public static class BookModel {
        private List<Book> books;
        private List<ModelObserver> observers;

        public BookModel() {
            this.books = new ArrayList<>();
            // 通知の途中でオブザーバーの登録・解除があっても例外にならないリスト
            this.observers = new CopyOnWriteArrayList<>();
            initializeSampleData();
        }

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

        /**
         * 書籍を加える。検査に通らなければ加えず、通知もしない（表は変わらない）
         */
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

        /**
         * タイトルか著者にキーワードを含む書籍を返す（大文字と小文字は区別しない）。
         * キーワードが空なら全部を返す
         */
        public List<Book> searchBooks(String keyword) {
            String key = keyword == null ? "" : keyword.trim().toLowerCase();
            List<Book> result = new ArrayList<>();
            for (Book book : books) {
                if (book.getTitle().toLowerCase().contains(key)
                        || book.getAuthor().toLowerCase().contains(key)) {
                    result.add(book);
                }
            }
            // 第12章のStream APIを使うと、次のように書ける
            // return books.stream()
            //         .filter(b -> b.getTitle().toLowerCase().contains(key) || b.getAuthor().toLowerCase().contains(key))
            //         .toList();
            return result;
        }

        // タイトルと著者が空でなく、出版年・価格・在庫が負でない書籍だけを受け付ける
        private boolean validateBook(Book book) {
            return book != null
                    && book.getTitle() != null && !book.getTitle().trim().isEmpty()
                    && book.getAuthor() != null && !book.getAuthor().trim().isEmpty()
                    && book.getPublicationYear() >= 0
                    && book.getPrice() >= 0
                    && book.getStock() >= 0;
        }

        // まだオブザーバーがいない段階なので、通知せずに直接リストに入れる
        private void initializeSampleData() {
            books.add(new Book("Java入門", "山田太郎", 2026, 3200, 5));
            books.add(new Book("オブジェクト指向設計", "鈴木花子", 2024, 2800, 3));
            books.add(new Book("Swingによる画面作り", "佐藤次郎", 2025, 3000, 0));
            books.add(new Book("テスト駆動開発", "高橋美咲", 2023, 2500, 2));
        }
    }

    // View（表示とユーザーの操作）
    public static class BookView extends JPanel implements ModelObserver {
        private BookController controller;
        private JTable bookTable;
        private BookTableModel tableModel;
        private JTextField searchField;
        private JButton addButton, editButton, deleteButton, refreshButton;
        private JLabel summaryLabel;

        public BookView() {
            initializeComponents();
            setupLayout();
            setupEventListeners();
        }

        public void setController(BookController controller) {
            this.controller = controller;
            refreshTable();
        }

        private void initializeComponents() {
            tableModel = new BookTableModel();
            bookTable = new JTable(tableModel);
            bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            searchField = new JTextField(15);
            addButton = new JButton("追加");
            editButton = new JButton("編集");
            deleteButton = new JButton("削除");
            refreshButton = new JButton("すべて表示");
            summaryLabel = new JLabel(" ");
        }

        private void setupLayout() {
            setLayout(new BorderLayout());
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(new JLabel("検索:"));
            topPanel.add(searchField);
            topPanel.add(addButton);
            topPanel.add(editButton);
            topPanel.add(deleteButton);
            topPanel.add(refreshButton);

            add(topPanel, BorderLayout.NORTH);
            add(new JScrollPane(bookTable), BorderLayout.CENTER);
            add(summaryLabel, BorderLayout.SOUTH);
        }

        // ボタンが押されたら、自分ではデータを変えず、Controllerに伝えるメソッドを呼ぶ
        private void setupEventListeners() {
            addButton.addActionListener(e -> showAddBookDialog());
            editButton.addActionListener(e -> showEditBookDialog());
            deleteButton.addActionListener(e -> deleteSelectedBook());
            refreshButton.addActionListener(e -> {
                searchField.setText("");
                refreshTable();
            });
            // 検索欄でEnterを押すと検索する
            searchField.addActionListener(e -> performSearch());
        }

        // Modelからの通知。どの変更でも表を描き直す
        @Override
        public void modelChanged(ModelEvent event) {
            SwingUtilities.invokeLater(() -> {
                switch (event.getType()) {
                    case BOOK_ADDED, BOOK_UPDATED, BOOK_REMOVED -> refreshTable();
                }
            });
        }

        private void refreshTable() {
            if (controller != null) {
                tableModel.setBooks(controller.getBooks());
                summaryLabel.setText("在庫の合計: " + controller.getTotalStock() + "冊、"
                        + String.format("在庫の金額: ¥%,d", controller.getTotalValue()));
            }
        }

        private void showAddBookDialog() {
            BookForm form = new BookForm(null);
            if (form.show(this, "書籍の追加")) {
                controller.addBook(form.title(), form.author(), form.year(), form.price(), form.stock());
            }
        }

        private void showEditBookDialog() {
            int index = selectedModelIndex();
            if (index < 0) {
                showError("編集する書籍を選んでください");
                return;
            }
            BookForm form = new BookForm(tableModel.getBookAt(bookTable.getSelectedRow()));
            if (form.show(this, "書籍の編集")) {
                controller.updateBook(index, form.title(), form.author(), form.year(), form.price(), form.stock());
            }
        }

        private void deleteSelectedBook() {
            int index = selectedModelIndex();
            if (index < 0) {
                showError("削除する書籍を選んでください");
                return;
            }
            if (!controller.canDeleteBook(index)) {
                // 在庫が残っている書籍は、確かめてから削除する
                Book book = tableModel.getBookAt(bookTable.getSelectedRow());
                int answer = JOptionPane.showConfirmDialog(this,
                        "「" + book.getTitle() + "」は在庫が" + book.getStock() + "冊残っています。削除しますか？",
                        "削除の確認", JOptionPane.YES_NO_OPTION);
                if (answer != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            controller.removeBook(index);
        }

        private void performSearch() {
            List<Book> found = controller.searchBooks(searchField.getText());
            tableModel.setBooks(found);
            summaryLabel.setText(found.size() + "冊が見つかりました");
        }

        // 表で選んだ書籍が、Modelのリストで何番目かを返す。検索で絞り込んでいると、表の行番号とは違う
        private int selectedModelIndex() {
            int row = bookTable.getSelectedRow();
            if (row < 0) {
                return -1;
            }
            return controller.getBooks().indexOf(tableModel.getBookAt(row));
        }

        private void showError(String message) {
            JOptionPane.showMessageDialog(this, message, "エラー", JOptionPane.ERROR_MESSAGE);
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
                return switch (columnIndex) {
                    case 0 -> book.getTitle();
                    case 1 -> book.getAuthor();
                    case 2 -> book.getPublicationYear();
                    case 3 -> String.format("¥%,d", book.getPrice());
                    case 4 -> book.getStock();
                    default -> "";
                };
            }

            public Book getBookAt(int index) {
                return books.get(index);
            }
        }
    }

    // 追加と編集の入力欄。数の欄が整数でなければ、showはfalseを返す
    private static class BookForm {
        private final JTextField titleField = new JTextField();
        private final JTextField authorField = new JTextField();
        private final JTextField yearField = new JTextField();
        private final JTextField priceField = new JTextField();
        private final JTextField stockField = new JTextField();
        private int year, price, stock;

        BookForm(Book book) {
            if (book != null) {
                titleField.setText(book.getTitle());
                authorField.setText(book.getAuthor());
                yearField.setText(String.valueOf(book.getPublicationYear()));
                priceField.setText(String.valueOf(book.getPrice()));
                stockField.setText(String.valueOf(book.getStock()));
            } else {
                yearField.setText("2026");
                priceField.setText("0");
                stockField.setText("0");
            }
        }

        boolean show(Component parent, String title) {
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("タイトル:"));
            panel.add(titleField);
            panel.add(new JLabel("著者:"));
            panel.add(authorField);
            panel.add(new JLabel("出版年:"));
            panel.add(yearField);
            panel.add(new JLabel("価格:"));
            panel.add(priceField);
            panel.add(new JLabel("在庫:"));
            panel.add(stockField);
            int result = JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return false;
            }
            try {
                year = Integer.parseInt(yearField.getText().trim());
                price = Integer.parseInt(priceField.getText().trim());
                stock = Integer.parseInt(stockField.getText().trim());
                return true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "出版年・価格・在庫は整数で入力してください",
                        "エラー", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        String title() { return titleField.getText(); }
        String author() { return authorField.getText(); }
        int year() { return year; }
        int price() { return price; }
        int stock() { return stock; }
    }

    // Controller（ViewとModelの橋渡し）
    public static class BookController {
        private BookModel model;
        private BookView view;

        public BookController() {
            this.model = new BookModel();
        }

        public void setView(BookView view) {
            this.view = view;
            model.addObserver(view);
            view.setController(this);
        }

        public void addBook(String title, String author, int publicationYear, int price, int stock) {
            model.addBook(new Book(title.trim(), author.trim(), publicationYear, price, stock));
        }

        public void updateBook(int index, String title, String author, int publicationYear, int price, int stock) {
            model.updateBook(index, new Book(title.trim(), author.trim(), publicationYear, price, stock));
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

        // 在庫が0の書籍は、確かめずに削除してよい、という決まり（在庫が残っていれば、Viewが確認を出す）
        public boolean canDeleteBook(int index) {
            List<Book> books = model.getBooks();
            return index >= 0 && index < books.size() && books.get(index).getStock() == 0;
        }

        // 在庫の金額の合計（価格×在庫）
        public int getTotalValue() {
            int total = 0;
            for (Book book : model.getBooks()) {
                total += book.getPrice() * book.getStock();
            }
            // 第12章のStream APIを使うと、次のように書ける
            // return model.getBooks().stream().mapToInt(b -> b.getPrice() * b.getStock()).sum();
            return total;
        }

        public int getTotalStock() {
            int total = 0;
            for (Book book : model.getBooks()) {
                total += book.getStock();
            }
            return total;
        }
    }

    // データモデル（雛形のまま）
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

    // オブザーバーのインターフェイス
    public interface ModelObserver {
        void modelChanged(ModelEvent event);
    }

    // Modelからの通知の内容
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
        SwingUtilities.invokeLater(() -> new MVCExample().setVisible(true));
    }
}
