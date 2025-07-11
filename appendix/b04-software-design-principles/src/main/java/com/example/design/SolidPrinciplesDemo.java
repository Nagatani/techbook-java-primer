package com.example.design;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * SOLID原則のデモンストレーション
 * 悪い設計と良い設計を比較して学習
 */
public class SolidPrinciplesDemo {
    
    // ===== 単一責任原則 (Single Responsibility Principle) =====
    
    /**
     * 悪い例：複数の責任を持つクラス
     */
    static class BadUserManager {
        private Map<String, User> users = new HashMap<>();
        
        // ユーザー管理の責任
        public void addUser(User user) {
            users.put(user.getId(), user);
            System.out.println("User added to database: " + user.getName());
        }
        
        // レポート生成の責任
        public String generateUserReport(String userId) {
            User user = users.get(userId);
            if (user == null) return "User not found";
            
            return "User Report:\n" +
                   "ID: " + user.getId() + "\n" +
                   "Name: " + user.getName() + "\n" +
                   "Email: " + user.getEmail();
        }
        
        // メール送信の責任
        public void sendWelcomeEmail(String userId) {
            User user = users.get(userId);
            if (user != null) {
                System.out.println("Sending welcome email to: " + user.getEmail());
                System.out.println("Subject: Welcome!");
                System.out.println("Body: Welcome to our service, " + user.getName());
            }
        }
        
        // バリデーションの責任
        public boolean validateUser(User user) {
            return user.getName() != null && !user.getName().isEmpty() &&
                   user.getEmail() != null && user.getEmail().contains("@");
        }
    }
    
    /**
     * 良い例：責任を分離したクラス群
     */
    
    // ユーザーデータの管理のみに責任を集中
    static class UserRepository {
        private Map<String, User> users = new HashMap<>();
        
        public void save(User user) {
            users.put(user.getId(), user);
            System.out.println("User saved: " + user.getName());
        }
        
        public User findById(String id) {
            return users.get(id);
        }
        
        public List<User> findAll() {
            return new ArrayList<>(users.values());
        }
    }
    
    // レポート生成のみに責任を集中
    static class UserReportGenerator {
        private UserRepository repository;
        
        public UserReportGenerator(UserRepository repository) {
            this.repository = repository;
        }
        
        public String generateReport(String userId) {
            User user = repository.findById(userId);
            if (user == null) return "User not found";
            
            return "User Report:\n" +
                   "ID: " + user.getId() + "\n" +
                   "Name: " + user.getName() + "\n" +
                   "Email: " + user.getEmail();
        }
    }
    
    // メール送信のみに責任を集中
    static class EmailService {
        public void sendWelcomeEmail(User user) {
            if (user != null) {
                System.out.println("Sending welcome email to: " + user.getEmail());
                System.out.println("Subject: Welcome!");
                System.out.println("Body: Welcome to our service, " + user.getName());
            }
        }
        
        public void sendEmail(String to, String subject, String body) {
            System.out.println("Email sent to: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
        }
    }
    
    // バリデーションのみに責任を集中
    static class UserValidator {
        public boolean validate(User user) {
            return user.getName() != null && !user.getName().isEmpty() &&
                   user.getEmail() != null && user.getEmail().contains("@") &&
                   user.getId() != null && !user.getId().isEmpty();
        }
        
        public List<String> getValidationErrors(User user) {
            List<String> errors = new ArrayList<>();
            
            if (user.getId() == null || user.getId().isEmpty()) {
                errors.add("User ID is required");
            }
            if (user.getName() == null || user.getName().isEmpty()) {
                errors.add("User name is required");
            }
            if (user.getEmail() == null || !user.getEmail().contains("@")) {
                errors.add("Valid email is required");
            }
            
            return errors;
        }
    }
    
    // ===== 開放閉鎖原則 (Open/Closed Principle) =====
    
    /**
     * 悪い例：新しい図形を追加するたびに既存コードを変更
     */
    static class BadShapeCalculator {
        public double calculateArea(Object shape) {
            if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                return rect.getWidth() * rect.getHeight();
            } else if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                return Math.PI * circle.getRadius() * circle.getRadius();
            }
            // 新しい図形を追加するたびにif文を追加する必要がある
            return 0;
        }
    }
    
    /**
     * 良い例：拡張に開かれ、修正に閉じた設計
     */
    interface Shape {
        double calculateArea();
        String getShapeType();
    }
    
    static class Rectangle implements Shape {
        private double width, height;
        
        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public double calculateArea() {
            return width * height;
        }
        
        @Override
        public String getShapeType() {
            return "Rectangle";
        }
        
        public double getWidth() { return width; }
        public double getHeight() { return height; }
    }
    
    static class Circle implements Shape {
        private double radius;
        
        public Circle(double radius) {
            this.radius = radius;
        }
        
        @Override
        public double calculateArea() {
            return Math.PI * radius * radius;
        }
        
        @Override
        public String getShapeType() {
            return "Circle";
        }
        
        public double getRadius() { return radius; }
    }
    
    // 新しい図形も既存コードを変更せずに追加可能
    static class Triangle implements Shape {
        private double base, height;
        
        public Triangle(double base, double height) {
            this.base = base;
            this.height = height;
        }
        
        @Override
        public double calculateArea() {
            return 0.5 * base * height;
        }
        
        @Override
        public String getShapeType() {
            return "Triangle";
        }
    }
    
    static class GoodShapeCalculator {
        public double calculateTotalArea(List<Shape> shapes) {
            return shapes.stream()
                    .mapToDouble(Shape::calculateArea)
                    .sum();
        }
        
        public void printShapeInfo(Shape shape) {
            System.out.println(shape.getShapeType() + " area: " + shape.calculateArea());
        }
    }
    
    // ===== リスコフ置換原則 (Liskov Substitution Principle) =====
    
    /**
     * 悪い例：正方形が長方形を継承すると置換原則に違反
     */
    static class BadRectangle {
        protected double width, height;
        
        public void setWidth(double width) {
            this.width = width;
        }
        
        public void setHeight(double height) {
            this.height = height;
        }
        
        public double getArea() {
            return width * height;
        }
        
        public double getWidth() { return width; }
        public double getHeight() { return height; }
    }
    
    static class BadSquare extends BadRectangle {
        @Override
        public void setWidth(double width) {
            this.width = width;
            this.height = width; // 正方形なので高さも同じに
        }
        
        @Override
        public void setHeight(double height) {
            this.width = height; // 正方形なので幅も同じに
            this.height = height;
        }
    }
    
    /**
     * 良い例：適切な抽象化による設計
     */
    interface GoodShape {
        double getArea();
        double getPerimeter();
    }
    
    static class GoodRectangle implements GoodShape {
        private final double width, height;
        
        public GoodRectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public double getArea() {
            return width * height;
        }
        
        @Override
        public double getPerimeter() {
            return 2 * (width + height);
        }
        
        public double getWidth() { return width; }
        public double getHeight() { return height; }
    }
    
    static class GoodSquare implements GoodShape {
        private final double side;
        
        public GoodSquare(double side) {
            this.side = side;
        }
        
        @Override
        public double getArea() {
            return side * side;
        }
        
        @Override
        public double getPerimeter() {
            return 4 * side;
        }
        
        public double getSide() { return side; }
    }
    
    // ===== インターフェイス分離原則 (Interface Segregation Principle) =====
    
    /**
     * 悪い例：大きすぎるインターフェイス
     */
    interface BadWorker {
        void work();
        void eat();
        void sleep();
        void takeBreak();
    }
    
    // ロボットは食事や睡眠を必要としない
    static class Robot implements BadWorker {
        @Override
        public void work() {
            System.out.println("Robot is working");
        }
        
        @Override
        public void eat() {
            // ロボットは食事をしないが実装を強制される
            throw new UnsupportedOperationException("Robots don't eat");
        }
        
        @Override
        public void sleep() {
            // ロボットは睡眠をしないが実装を強制される
            throw new UnsupportedOperationException("Robots don't sleep");
        }
        
        @Override
        public void takeBreak() {
            // ロボットは休憩をしないが実装を強制される
            throw new UnsupportedOperationException("Robots don't take breaks");
        }
    }
    
    /**
     * 良い例：インターフェイスを適切に分離
     */
    interface Workable {
        void work();
    }
    
    interface Feedable {
        void eat();
    }
    
    interface Sleepable {
        void sleep();
    }
    
    interface Breakable {
        void takeBreak();
    }
    
    static class Human implements Workable, Feedable, Sleepable, Breakable {
        private String name;
        
        public Human(String name) {
            this.name = name;
        }
        
        @Override
        public void work() {
            System.out.println(name + " is working");
        }
        
        @Override
        public void eat() {
            System.out.println(name + " is eating");
        }
        
        @Override
        public void sleep() {
            System.out.println(name + " is sleeping");
        }
        
        @Override
        public void takeBreak() {
            System.out.println(name + " is taking a break");
        }
    }
    
    static class GoodRobot implements Workable {
        private String model;
        
        public GoodRobot(String model) {
            this.model = model;
        }
        
        @Override
        public void work() {
            System.out.println("Robot " + model + " is working");
        }
        
        // 不要なメソッドの実装は強制されない
    }
    
    // ===== 依存関係逆転原則 (Dependency Inversion Principle) =====
    
    /**
     * 悪い例：高レベルモジュールが低レベルモジュールに依存
     */
    static class MySQLDatabase {
        public void save(String data) {
            System.out.println("Saving to MySQL: " + data);
        }
    }
    
    static class BadOrderService {
        private MySQLDatabase database = new MySQLDatabase(); // 具象クラスに直接依存
        
        public void processOrder(String orderData) {
            // ビジネスロジック
            System.out.println("Processing order: " + orderData);
            
            // データ保存
            database.save(orderData); // MySQL に強く依存
        }
    }
    
    /**
     * 良い例：抽象に依存する設計
     */
    interface Database {
        void save(String data);
        String load(String id);
    }
    
    static class MySQLDatabaseImpl implements Database {
        @Override
        public void save(String data) {
            System.out.println("Saving to MySQL: " + data);
        }
        
        @Override
        public String load(String id) {
            return "Data from MySQL for ID: " + id;
        }
    }
    
    static class MongoDatabase implements Database {
        @Override
        public void save(String data) {
            System.out.println("Saving to MongoDB: " + data);
        }
        
        @Override
        public String load(String id) {
            return "Data from MongoDB for ID: " + id;
        }
    }
    
    static class GoodOrderService {
        private Database database; // 抽象に依存
        
        public GoodOrderService(Database database) {
            this.database = database; // 依存性注入
        }
        
        public void processOrder(String orderData) {
            // ビジネスロジック
            System.out.println("Processing order: " + orderData);
            
            // データ保存（どのデータベース実装でも動作）
            database.save(orderData);
        }
        
        public String getOrderData(String orderId) {
            return database.load(orderId);
        }
    }
    
    // ===== User クラスの定義 =====
    static class User {
        private String id;
        private String name;
        private String email;
        
        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        
        @Override
        public String toString() {
            return "User{id='" + id + "', name='" + name + "', email='" + email + "'}";
        }
    }
    
    // ===== デモンストレーション メソッド =====
    
    /**
     * 単一責任原則のデモ
     */
    public static void demonstrateSingleResponsibility() {
        System.out.println("=== Single Responsibility Principle Demo ===");
        
        User user = new User("1", "Alice", "alice@example.com");
        
        System.out.println("\n--- Bad Design (Multiple Responsibilities) ---");
        BadUserManager badManager = new BadUserManager();
        badManager.addUser(user);
        System.out.println(badManager.generateUserReport("1"));
        badManager.sendWelcomeEmail("1");
        System.out.println("Valid user: " + badManager.validateUser(user));
        
        System.out.println("\n--- Good Design (Separated Responsibilities) ---");
        UserRepository repository = new UserRepository();
        UserReportGenerator reportGenerator = new UserReportGenerator(repository);
        EmailService emailService = new EmailService();
        UserValidator validator = new UserValidator();
        
        // 各責任が分離されている
        if (validator.validate(user)) {
            repository.save(user);
            System.out.println(reportGenerator.generateReport("1"));
            emailService.sendWelcomeEmail(user);
        } else {
            System.out.println("Validation errors: " + validator.getValidationErrors(user));
        }
    }
    
    /**
     * 開放閉鎖原則のデモ
     */
    public static void demonstrateOpenClosed() {
        System.out.println("\n=== Open/Closed Principle Demo ===");
        
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Rectangle(5, 3));
        shapes.add(new Circle(4));
        shapes.add(new Triangle(6, 8));
        
        GoodShapeCalculator calculator = new GoodShapeCalculator();
        
        System.out.println("Shape information:");
        for (Shape shape : shapes) {
            calculator.printShapeInfo(shape);
        }
        
        System.out.println("Total area: " + calculator.calculateTotalArea(shapes));
        
        // 新しい図形を追加しても既存コードを変更する必要がない
        System.out.println("\nAdding new shapes without modifying existing code...");
    }
    
    /**
     * リスコフ置換原則のデモ
     */
    public static void demonstrateLiskovSubstitution() {
        System.out.println("\n=== Liskov Substitution Principle Demo ===");
        
        System.out.println("--- Bad Design (LSP Violation) ---");
        // 長方形として扱っているが正方形の振る舞いで期待が破られる
        BadRectangle rect = new BadSquare();
        rect.setWidth(5);
        rect.setHeight(3);
        System.out.println("Expected area: 15, Actual area: " + rect.getArea());
        // 期待される結果と異なる！
        
        System.out.println("\n--- Good Design (LSP Compliant) ---");
        List<GoodShape> shapes = new ArrayList<>();
        shapes.add(new GoodRectangle(5, 3));
        shapes.add(new GoodSquare(4));
        
        for (GoodShape shape : shapes) {
            System.out.println("Shape area: " + shape.getArea() + 
                             ", perimeter: " + shape.getPerimeter());
        }
        // どの実装でも期待通りに動作する
    }
    
    /**
     * インターフェイス分離原則のデモ
     */
    public static void demonstrateInterfaceSegregation() {
        System.out.println("\n=== Interface Segregation Principle Demo ===");
        
        System.out.println("--- Good Design (Segregated Interfaces) ---");
        Human human = new Human("Alice");
        GoodRobot robot = new GoodRobot("R2D2");
        
        // 人間は全ての活動が可能
        human.work();
        human.eat();
        human.sleep();
        human.takeBreak();
        
        // ロボットは必要な機能のみ実装
        robot.work();
        // robot.eat(); // コンパイルエラー - ロボットは食事インターフェイスを実装していない
        
        System.out.println("Robots don't need to implement unnecessary methods!");
    }
    
    /**
     * 依存関係逆転原則のデモ
     */
    public static void demonstrateDependencyInversion() {
        System.out.println("\n=== Dependency Inversion Principle Demo ===");
        
        System.out.println("--- Bad Design (Concrete Dependency) ---");
        BadOrderService badService = new BadOrderService();
        badService.processOrder("Order #123");
        // MySQL にロックインされている
        
        System.out.println("\n--- Good Design (Abstract Dependency) ---");
        
        // 異なるデータベース実装を注入可能
        Database mysqlDb = new MySQLDatabaseImpl();
        Database mongoDb = new MongoDatabase();
        
        GoodOrderService mysqlService = new GoodOrderService(mysqlDb);
        GoodOrderService mongoService = new GoodOrderService(mongoDb);
        
        System.out.println("Using MySQL:");
        mysqlService.processOrder("Order #456");
        
        System.out.println("Using MongoDB:");
        mongoService.processOrder("Order #789");
        
        // 同じビジネスロジックが異なる実装で動作
        System.out.println("Same business logic works with different implementations!");
    }
    
    /**
     * SOLID原則の利点をまとめて表示
     */
    public static void summarizeSOLIDBenefits() {
        System.out.println("\n=== SOLID Principles Benefits ===");
        System.out.println("1. Single Responsibility: Changes affect only one class");
        System.out.println("2. Open/Closed: Add new features without modifying existing code");
        System.out.println("3. Liskov Substitution: Subtypes can replace base types safely");
        System.out.println("4. Interface Segregation: Classes depend only on methods they use");
        System.out.println("5. Dependency Inversion: High-level modules don't depend on low-level details");
        System.out.println("\nResult: Maintainable, testable, and extensible code!");
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateSingleResponsibility();
        demonstrateOpenClosed();
        demonstrateLiskovSubstitution();
        demonstrateInterfaceSegregation();
        demonstrateDependencyInversion();
        summarizeSOLIDBenefits();
    }
}