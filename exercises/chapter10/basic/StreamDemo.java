import java.util.*;

/**
 * 第10章 Stream API - 基本演習
 * デモプログラム
 */
public class StreamDemo {
    public static void main(String[] args) {
        // 学生データのサンプル
        List<Student> students = Arrays.asList(
            new Student("Alice", 20, 3.8, "Computer Science"),
            new Student("Bob", 22, 3.5, "Mathematics"),
            new Student("Charlie", 21, 3.9, "Computer Science"),
            new Student("David", 23, 3.2, "Physics"),
            new Student("Eve", 20, 3.7, "Mathematics")
        );
        
        StudentAnalyzer analyzer = new StudentAnalyzer(students);
        
        System.out.println("=== 学生データ分析 ===");
        
        // TODO: StudentAnalyzerの各メソッドをテスト
        
        // 商品データのサンプル
        List<Product> products = Arrays.asList(
            new Product("Laptop", "Electronics", 1200.0, 5),
            new Product("Mouse", "Electronics", 25.0, 50),
            new Product("Book", "Education", 30.0, 0),
            new Product("Desk", "Furniture", 350.0, 10),
            new Product("Chair", "Furniture", 150.0, 15)
        );
        
        SalesAnalyzer salesAnalyzer = new SalesAnalyzer(products);
        
        System.out.println("\n=== 売上分析 ===");
        
        // TODO: SalesAnalyzerの各メソッドをテスト
    }
}