package chapter12.solutions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 学生と商品の分析のデモ（課題1・2の解答例）
 */
public class StreamDemo {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 20, 3.8, "Computer Science"),
            new Student("Bob", 22, 3.5, "Mathematics"),
            new Student("Charlie", 21, 3.9, "Computer Science"),
            new Student("David", 23, 3.2, "Physics"),
            new Student("Eve", 20, 3.7, "Mathematics")
        );

        StudentAnalyzer analyzer = new StudentAnalyzer(students);

        IO.println("=== 学生データ分析 ===");
        IO.println("名前: " + analyzer.getAllNames());

        IO.println("GPA 3.7以上:");
        analyzer.filterByGPA(3.7).forEach(s -> IO.println("  " + s));

        IO.println("学部ごと:");
        for (Map.Entry<String, List<Student>> entry : analyzer.groupByDepartment().entrySet()) {
            List<String> names = entry.getValue().stream().map(Student::getName).toList();
            IO.println("  " + entry.getKey() + ": " + names);
        }

        IO.println("平均GPA: " + "%.2f".formatted(analyzer.calculateAverageGPA()));

        IO.println("GPA上位3人:");
        analyzer.getTopStudents(3).forEach(s -> IO.println("  " + s.getName() + " " + s.getGpa()));

        List<Product> products = Arrays.asList(
            new Product("Laptop", "Electronics", 1200.0, 5),
            new Product("Mouse", "Electronics", 25.0, 50),
            new Product("Book", "Education", 30.0, 0),
            new Product("Desk", "Furniture", 350.0, 10),
            new Product("Chair", "Furniture", 150.0, 15)
        );

        SalesAnalyzer salesAnalyzer = new SalesAnalyzer(products);

        IO.println("\n=== 売上分析 ===");
        IO.println("カテゴリごとの売上:");
        salesAnalyzer.calculateSalesByCategory()
                .forEach((category, total) -> IO.println("  " + category + ": " + total));

        IO.println("価格が100〜500の商品:");
        salesAnalyzer.filterByPriceRange(100, 500).forEach(p -> IO.println("  " + p));

        IO.println("在庫切れ: " + salesAnalyzer.getOutOfStockProducts());

        IO.println("売上の上位2件:");
        salesAnalyzer.getTopSellingProducts(2)
                .forEach(p -> IO.println("  " + p.getName() + " " + p.getTotalSales()));

        IO.println("商品の説明: " + salesAnalyzer.getProductDescriptions());
    }
}
