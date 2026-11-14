package com.example.design;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * デザインパターンのデモンストレーション
 * GoFパターンの主要なものを実践的に学習
 */
public class DesignPatternsDemo {
    
    // ===== 生成パターン (Creational Patterns) =====
    
    /**
     * シングルトンパターン - スレッドセーフな実装
     */
    static class DatabaseConnection {
        private static volatile DatabaseConnection instance;
        private static final Object lock = new Object();
        private String connectionString;
        
        private DatabaseConnection() {
            // 初期化処理
            this.connectionString = "jdbc:mysql://localhost:3306/mydb";
            IO.println("Database connection created: " + connectionString);
        }
        
        public static DatabaseConnection getInstance() {
            if (instance == null) {
                synchronized (lock) {
                    if (instance == null) {
                        instance = new DatabaseConnection();
                    }
                }
            }
            return instance;
        }
        
        public void executeQuery(String query) {
            IO.println("Executing query: " + query);
        }
        
        public String getConnectionString() {
            return connectionString;
        }
    }
    
    /**
     * より良いシングルトン実装 - Enum Singleton
     */
    enum ConfigurationManager {
        INSTANCE;
        
        private Map<String, String> config = new HashMap<>();
        
        ConfigurationManager() {
            // デフォルト設定を読み込み
            config.put("app.name", "MyApplication");
            config.put("app.version", "1.0.0");
            config.put("debug.enabled", "true");
            IO.println("Configuration loaded");
        }
        
        public String getProperty(String key) {
            return config.get(key);
        }
        
        public void setProperty(String key, String value) {
            config.put(key, value);
        }
    }
    
    /**
     * ファクトリパターン
     */
    
    // 製品の抽象クラス
    abstract static class Document {
        protected String content;
        
        public abstract void create();
        public abstract void save();
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    // 具体的な製品
    static class WordDocument extends Document {
        @Override
        public void create() {
            IO.println("Creating Word document");
            this.content = "Word Document Content";
        }
        
        @Override
        public void save() {
            IO.println("Saving Word document: " + content);
        }
    }
    
    static class PDFDocument extends Document {
        @Override
        public void create() {
            IO.println("Creating PDF document");
            this.content = "PDF Document Content";
        }
        
        @Override
        public void save() {
            IO.println("Saving PDF document: " + content);
        }
    }
    
    // ファクトリ
    static class DocumentFactory {
        public static Document createDocument(String type) {
            switch (type.toLowerCase()) {
                case "word":
                    return new WordDocument();
                case "pdf":
                    return new PDFDocument();
                default:
                    throw new IllegalArgumentException("Unknown document type: " + type);
            }
        }
    }
    
    /**
     * 抽象ファクトリパターン
     */
    
    // 抽象製品
    interface Button {
        void render();
        void onClick();
    }
    
    interface TextField {
        void render();
        String getText();
    }
    
    // Windows用の具体的な製品
    static class WindowsButton implements Button {
        @Override
        public void render() {
            IO.println("Rendering Windows-style button");
        }
        
        @Override
        public void onClick() {
            IO.println("Windows button clicked");
        }
    }
    
    static class WindowsTextField implements TextField {
        private String text = "";
        
        @Override
        public void render() {
            IO.println("Rendering Windows-style text field");
        }
        
        @Override
        public String getText() {
            return text;
        }
    }
    
    // Mac用の具体的な製品
    static class MacButton implements Button {
        @Override
        public void render() {
            IO.println("Rendering Mac-style button");
        }
        
        @Override
        public void onClick() {
            IO.println("Mac button clicked");
        }
    }
    
    static class MacTextField implements TextField {
        private String text = "";
        
        @Override
        public void render() {
            IO.println("Rendering Mac-style text field");
        }
        
        @Override
        public String getText() {
            return text;
        }
    }
    
    // 抽象ファクトリ
    interface UIFactory {
        Button createButton();
        TextField createTextField();
    }
    
    // 具体的なファクトリ
    static class WindowsUIFactory implements UIFactory {
        @Override
        public Button createButton() {
            return new WindowsButton();
        }
        
        @Override
        public TextField createTextField() {
            return new WindowsTextField();
        }
    }
    
    static class MacUIFactory implements UIFactory {
        @Override
        public Button createButton() {
            return new MacButton();
        }
        
        @Override
        public TextField createTextField() {
            return new MacTextField();
        }
    }
    
    // クライアントコード
    static class Application {
        private UIFactory factory;
        private Button button;
        private TextField textField;
        
        public Application(UIFactory factory) {
            this.factory = factory;
        }
        
        public void createUI() {
            button = factory.createButton();
            textField = factory.createTextField();
        }
        
        public void renderUI() {
            button.render();
            textField.render();
        }
    }
    
    // ===== 構造パターン (Structural Patterns) =====
    
    /**
     * アダプターパターン
     */
    
    // 既存のクラス（変更不可）
    static class LegacyPrinter {
        public void printOldFormat(String text) {
            IO.println("Legacy Printer: " + text.toUpperCase());
        }
    }
    
    // 新しいインターフェイス
    interface ModernPrinter {
        void print(String text, String format);
    }
    
    // アダプター
    static class PrinterAdapter implements ModernPrinter {
        private LegacyPrinter legacyPrinter;
        
        public PrinterAdapter(LegacyPrinter legacyPrinter) {
            this.legacyPrinter = legacyPrinter;
        }
        
        @Override
        public void print(String text, String format) {
            if ("legacy".equals(format)) {
                legacyPrinter.printOldFormat(text);
            } else {
                IO.println("Modern format: " + text);
            }
        }
    }
    
    /**
     * デコレーターパターン
     */
    
    // 基本インターフェイス
    interface Coffee {
        double cost();
        String description();
    }
    
    // 基本実装
    static class SimpleCoffee implements Coffee {
        @Override
        public double cost() {
            return 2.0;
        }
        
        @Override
        public String description() {
            return "Simple Coffee";
        }
    }
    
    // デコレーター基底クラス
    static abstract class CoffeeDecorator implements Coffee {
        protected Coffee coffee;
        
        public CoffeeDecorator(Coffee coffee) {
            this.coffee = coffee;
        }
    }
    
    // 具体的なデコレーター
    static class MilkDecorator extends CoffeeDecorator {
        public MilkDecorator(Coffee coffee) {
            super(coffee);
        }
        
        @Override
        public double cost() {
            return coffee.cost() + 0.5;
        }
        
        @Override
        public String description() {
            return coffee.description() + " + Milk";
        }
    }
    
    static class SugarDecorator extends CoffeeDecorator {
        public SugarDecorator(Coffee coffee) {
            super(coffee);
        }
        
        @Override
        public double cost() {
            return coffee.cost() + 0.2;
        }
        
        @Override
        public String description() {
            return coffee.description() + " + Sugar";
        }
    }
    
    static class WhippedCreamDecorator extends CoffeeDecorator {
        public WhippedCreamDecorator(Coffee coffee) {
            super(coffee);
        }
        
        @Override
        public double cost() {
            return coffee.cost() + 0.8;
        }
        
        @Override
        public String description() {
            return coffee.description() + " + Whipped Cream";
        }
    }
    
    // ===== 振る舞いパターン (Behavioral Patterns) =====
    
    /**
     * オブザーバーパターン
     */
    
    // オブザーバーインターフェイス
    interface Observer {
        void update(String message);
    }
    
    // 被観察者インターフェイス
    interface Observable {
        void addObserver(Observer observer);
        void removeObserver(Observer observer);
        void notifyObservers(String message);
    }
    
    // 具体的な被観察者
    static class NewsAgency implements Observable {
        private List<Observer> observers = new ArrayList<>();
        private String latestNews;
        
        @Override
        public void addObserver(Observer observer) {
            observers.add(observer);
        }
        
        @Override
        public void removeObserver(Observer observer) {
            observers.remove(observer);
        }
        
        @Override
        public void notifyObservers(String message) {
            for (Observer observer : observers) {
                observer.update(message);
            }
        }
        
        public void setNews(String news) {
            this.latestNews = news;
            notifyObservers("Breaking News: " + news);
        }
        
        public String getLatestNews() {
            return latestNews;
        }
    }
    
    // 具体的なオブザーバー
    static class NewsChannel implements Observer {
        private String name;
        
        public NewsChannel(String name) {
            this.name = name;
        }
        
        @Override
        public void update(String message) {
            IO.println(name + " received: " + message);
        }
    }
    
    /**
     * ストラテジーパターン
     */
    
    // 戦略インターフェイス
    interface PaymentStrategy {
        void pay(double amount);
        String getPaymentType();
    }
    
    // 具体的な戦略
    static class CreditCardPayment implements PaymentStrategy {
        private String cardNumber;
        private String holderName;
        
        public CreditCardPayment(String cardNumber, String holderName) {
            this.cardNumber = cardNumber;
            this.holderName = holderName;
        }
        
        @Override
        public void pay(double amount) {
            IO.println("Paid $" + amount + " using Credit Card");
            IO.println("Card: " + maskCardNumber(cardNumber) + " (" + holderName + ")");
        }
        
        @Override
        public String getPaymentType() {
            return "Credit Card";
        }
        
        private String maskCardNumber(String cardNumber) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        }
    }
    
    static class PayPalPayment implements PaymentStrategy {
        private String email;
        
        public PayPalPayment(String email) {
            this.email = email;
        }
        
        @Override
        public void pay(double amount) {
            IO.println("Paid $" + amount + " using PayPal");
            IO.println("Account: " + email);
        }
        
        @Override
        public String getPaymentType() {
            return "PayPal";
        }
    }
    
    static class BankTransferPayment implements PaymentStrategy {
        private String accountNumber;
        
        public BankTransferPayment(String accountNumber) {
            this.accountNumber = accountNumber;
        }
        
        @Override
        public void pay(double amount) {
            IO.println("Paid $" + amount + " using Bank Transfer");
            IO.println("Account: " + maskAccountNumber(accountNumber));
        }
        
        @Override
        public String getPaymentType() {
            return "Bank Transfer";
        }
        
        private String maskAccountNumber(String accountNumber) {
            return "****" + accountNumber.substring(accountNumber.length() - 4);
        }
    }
    
    // コンテキスト
    static class ShoppingCart {
        private PaymentStrategy paymentStrategy;
        private List<String> items = new ArrayList<>();
        private double totalAmount = 0.0;
        
        public void addItem(String item, double price) {
            items.add(item);
            totalAmount += price;
            IO.println("Added " + item + " ($" + price + ") to cart");
        }
        
        public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
            this.paymentStrategy = paymentStrategy;
            IO.println("Payment method set to: " + paymentStrategy.getPaymentType());
        }
        
        public void checkout() {
            if (paymentStrategy == null) {
                IO.println("Please select a payment method");
                return;
            }
            
            IO.println("\n--- Checkout Summary ---");
            IO.println("Items: " + items);
            IO.println("Total: $" + totalAmount);
            paymentStrategy.pay(totalAmount);
            
            // カートをクリア
            items.clear();
            totalAmount = 0.0;
            IO.println("Order completed successfully!\n");
        }
    }
    
    /**
     * コマンドパターン
     */
    
    // コマンドインターフェイス
    interface Command {
        void execute();
        void undo();
        String getDescription();
    }
    
    // 受信者（実際の処理を行う）
    static class TextEditor {
        private StringBuilder content = new StringBuilder();
        
        public void addText(String text) {
            content.append(text);
        }
        
        public void removeText(int length) {
            if (length > content.length()) {
                content.setLength(0);
            } else {
                content.setLength(content.length() - length);
            }
        }
        
        public String getContent() {
            return content.toString();
        }
        
        public void clear() {
            content.setLength(0);
        }
    }
    
    // 具体的なコマンド
    static class AddTextCommand implements Command {
        private TextEditor editor;
        private String textToAdd;
        
        public AddTextCommand(TextEditor editor, String text) {
            this.editor = editor;
            this.textToAdd = text;
        }
        
        @Override
        public void execute() {
            editor.addText(textToAdd);
        }
        
        @Override
        public void undo() {
            editor.removeText(textToAdd.length());
        }
        
        @Override
        public String getDescription() {
            return "Add text: '" + textToAdd + "'";
        }
    }
    
    static class ClearCommand implements Command {
        private TextEditor editor;
        private String previousContent;
        
        public ClearCommand(TextEditor editor) {
            this.editor = editor;
        }
        
        @Override
        public void execute() {
            previousContent = editor.getContent();
            editor.clear();
        }
        
        @Override
        public void undo() {
            editor.clear();
            editor.addText(previousContent);
        }
        
        @Override
        public String getDescription() {
            return "Clear all text";
        }
    }
    
    // 呼び出し者
    static class EditorInvoker {
        private List<Command> history = new ArrayList<>();
        private int currentPosition = -1;
        
        public void execute(Command command) {
            // 現在位置以降の履歴を削除（新しいコマンドが実行されたため）
            while (history.size() > currentPosition + 1) {
                history.remove(history.size() - 1);
            }
            
            command.execute();
            history.add(command);
            currentPosition++;
            
            IO.println("Executed: " + command.getDescription());
        }
        
        public void undo() {
            if (currentPosition >= 0) {
                Command command = history.get(currentPosition);
                command.undo();
                currentPosition--;
                IO.println("Undone: " + command.getDescription());
            } else {
                IO.println("Nothing to undo");
            }
        }
        
        public void redo() {
            if (currentPosition < history.size() - 1) {
                currentPosition++;
                Command command = history.get(currentPosition);
                command.execute();
                IO.println("Redone: " + command.getDescription());
            } else {
                IO.println("Nothing to redo");
            }
        }
        
        public void showHistory() {
            IO.println("Command History:");
            for (int i = 0; i < history.size(); i++) {
                String prefix = (i == currentPosition) ? "-> " : "   ";
                IO.println(prefix + (i + 1) + ". " + history.get(i).getDescription());
            }
        }
    }
    
    // ===== デモンストレーションメソッド =====
    
    /**
     * 生成パターンのデモ
     */
    public static void demonstrateCreationalPatterns() {
        IO.println("=== Creational Patterns Demo ===");
        
        // シングルトンパターン
        IO.println("\n--- Singleton Pattern ---");
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        IO.println("Same instance: " + (db1 == db2));
        db1.executeQuery("SELECT * FROM users");
        
        // Enum Singleton
        ConfigurationManager config = ConfigurationManager.INSTANCE;
        IO.println("App name: " + config.getProperty("app.name"));
        config.setProperty("debug.enabled", "false");
        
        // ファクトリパターン
        IO.println("\n--- Factory Pattern ---");
        Document wordDoc = DocumentFactory.createDocument("word");
        Document pdfDoc = DocumentFactory.createDocument("pdf");
        
        wordDoc.create();
        wordDoc.save();
        pdfDoc.create();
        pdfDoc.save();
        
        // 抽象ファクトリパターン
        IO.println("\n--- Abstract Factory Pattern ---");
        UIFactory windowsFactory = new WindowsUIFactory();
        Application winApp = new Application(windowsFactory);
        winApp.createUI();
        winApp.renderUI();
        
        UIFactory macFactory = new MacUIFactory();
        Application macApp = new Application(macFactory);
        macApp.createUI();
        macApp.renderUI();
    }
    
    /**
     * 構造パターンのデモ
     */
    public static void demonstrateStructuralPatterns() {
        IO.println("\n=== Structural Patterns Demo ===");
        
        // アダプターパターン
        IO.println("\n--- Adapter Pattern ---");
        LegacyPrinter legacyPrinter = new LegacyPrinter();
        ModernPrinter adapter = new PrinterAdapter(legacyPrinter);
        
        adapter.print("Hello World", "legacy");
        adapter.print("Hello World", "modern");
        
        // デコレーターパターン
        IO.println("\n--- Decorator Pattern ---");
        Coffee coffee = new SimpleCoffee();
        IO.println(coffee.description() + " costs $" + coffee.cost());
        
        coffee = new MilkDecorator(coffee);
        IO.println(coffee.description() + " costs $" + coffee.cost());
        
        coffee = new SugarDecorator(coffee);
        IO.println(coffee.description() + " costs $" + coffee.cost());
        
        coffee = new WhippedCreamDecorator(coffee);
        IO.println(coffee.description() + " costs $" + coffee.cost());
    }
    
    /**
     * 振る舞いパターンのデモ
     */
    public static void demonstrateBehavioralPatterns() {
        IO.println("\n=== Behavioral Patterns Demo ===");
        
        // オブザーバーパターン
        IO.println("\n--- Observer Pattern ---");
        NewsAgency agency = new NewsAgency();
        NewsChannel cnn = new NewsChannel("CNN");
        NewsChannel bbc = new NewsChannel("BBC");
        NewsChannel reuters = new NewsChannel("Reuters");
        
        agency.addObserver(cnn);
        agency.addObserver(bbc);
        agency.addObserver(reuters);
        
        agency.setNews("New library opens downtown");
        agency.setNews("Local team wins the championship");
        
        // ストラテジーパターン
        IO.println("\n--- Strategy Pattern ---");
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Laptop", 999.99);
        cart.addItem("Mouse", 29.99);
        cart.addItem("Keyboard", 79.99);
        
        // クレジットカード決済
        cart.setPaymentStrategy(new CreditCardPayment("1234567812345678", "John Doe"));
        cart.checkout();
        
        cart.addItem("Monitor", 299.99);
        cart.addItem("Speakers", 89.99);
        
        // PayPal決済
        cart.setPaymentStrategy(new PayPalPayment("john.doe@email.com"));
        cart.checkout();
        
        // コマンドパターン
        IO.println("\n--- Command Pattern ---");
        TextEditor editor = new TextEditor();
        EditorInvoker invoker = new EditorInvoker();
        
        invoker.execute(new AddTextCommand(editor, "Hello "));
        invoker.execute(new AddTextCommand(editor, "World!"));
        IO.println("Content: '" + editor.getContent() + "'");
        
        invoker.execute(new AddTextCommand(editor, " How are you?"));
        IO.println("Content: '" + editor.getContent() + "'");
        
        invoker.undo();
        IO.println("After undo: '" + editor.getContent() + "'");
        
        invoker.redo();
        IO.println("After redo: '" + editor.getContent() + "'");
        
        invoker.execute(new ClearCommand(editor));
        IO.println("After clear: '" + editor.getContent() + "'");
        
        invoker.undo();
        IO.println("After undo clear: '" + editor.getContent() + "'");
        
        invoker.showHistory();
    }
    
    /**
     * デザインパターンの利点をまとめて表示
     */
    public static void summarizePatternBenefits() {
        IO.println("\n=== Design Patterns Benefits ===");
        IO.println("Creational Patterns:");
        IO.println("  - Singleton: Ensures single instance and global access");
        IO.println("  - Factory: Encapsulates object creation logic");
        IO.println("  - Abstract Factory: Creates families of related objects");
        
        IO.println("\nStructural Patterns:");
        IO.println("  - Adapter: Allows incompatible interfaces to work together");
        IO.println("  - Decorator: Adds behavior to objects dynamically");
        
        IO.println("\nBehavioral Patterns:");
        IO.println("  - Observer: Defines one-to-many dependency between objects");
        IO.println("  - Strategy: Encapsulates algorithms and makes them interchangeable");
        IO.println("  - Command: Encapsulates requests as objects");
        
        IO.println("\nOverall Benefits:");
        IO.println("  - Reusable solutions to common problems");
        IO.println("  - Improved code organization and maintainability");
        IO.println("  - Better communication among developers");
        IO.println("  - Tested and proven design approaches");
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateCreationalPatterns();
        demonstrateStructuralPatterns();
        demonstrateBehavioralPatterns();
        summarizePatternBenefits();
    }
}