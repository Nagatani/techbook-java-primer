package chapter12.solutions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class StreamTasksTest {
    private final List<Student> students = List.of(
            new Student("Alice", 20, 3.8, "Computer Science"),
            new Student("Bob", 22, 3.5, "Mathematics"),
            new Student("Charlie", 21, 3.9, "Computer Science"));
    private final List<Product> products = List.of(
            new Product("Laptop", "Electronics", 1200.0, 5),
            new Product("Mouse", "Electronics", 25.0, 50),
            new Product("Book", "Education", 30.0, 0));

    @Test
    void 学生の名前と絞り込み() {
        StudentAnalyzer analyzer = new StudentAnalyzer(students);
        assertEquals(List.of("Alice", "Bob", "Charlie"), analyzer.getAllNames());
        assertEquals(2, analyzer.filterByGPA(3.8).size());
    }

    @Test
    void 学部ごとのグループと平均() {
        StudentAnalyzer analyzer = new StudentAnalyzer(students);
        Map<String, List<Student>> groups = analyzer.groupByDepartment();
        assertEquals(List.of("Computer Science", "Mathematics"), List.copyOf(groups.keySet()));
        assertEquals(2, groups.get("Computer Science").size());
        assertEquals(3.733, analyzer.calculateAverageGPA(), 0.001);
        assertEquals(0.0, new StudentAnalyzer(List.of()).calculateAverageGPA(), 0.0);
    }

    @Test
    void GPAの上位() {
        List<Student> top = new StudentAnalyzer(students).getTopStudents(2);
        assertEquals("Charlie", top.get(0).getName());
        assertEquals("Alice", top.get(1).getName());
    }

    @Test
    void カテゴリごとの売上と在庫切れ() {
        SalesAnalyzer analyzer = new SalesAnalyzer(products);
        assertEquals(Map.of("Electronics", 7250.0, "Education", 0.0), analyzer.calculateSalesByCategory());
        assertEquals("Book", analyzer.getOutOfStockProducts().get(0).getName());
        assertEquals(List.of("Mouse", "Book"),
                analyzer.filterByPriceRange(20, 100).stream().map(Product::getName).toList());
    }

    @Test
    void 売上の上位と説明() {
        SalesAnalyzer analyzer = new SalesAnalyzer(products);
        assertEquals("Laptop", analyzer.getTopSellingProducts(1).get(0).getName());
        assertEquals("Laptop (Electronics)", analyzer.getProductDescriptions().get(0));
    }

    @Test
    void 数のパイプライン() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        assertEquals(List.of("4", "16", "36"), NumberPipeline.evenSquares(numbers));
        assertEquals(List.of(1, 3, 5), NumberPipeline.partitionByEven(numbers).get(false));
        assertEquals(21, NumberPipeline.sum(numbers));
        assertEquals(List.of(1, 2, 3), NumberPipeline.flatten(List.of(List.of(1), List.of(), List.of(2, 3))));
        assertEquals(Optional.of(5), NumberPipeline.firstGreaterThan(numbers, 4));
        assertTrue(NumberPipeline.firstGreaterThan(numbers, 10).isEmpty());
    }
}
