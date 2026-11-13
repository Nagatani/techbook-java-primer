package chapter12.solutions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 解答例の動作確認（課題1〜3）
 *
 * 期待する値と実際の値を比べ、「OK」か「NG」を表示します。
 * 解答例を書き換えたら実行し、すべてOKになることを確かめます。
 * 第21章で学ぶJUnitを使うと、同じ確認をテストとして書けます。
 */
public class SolutionCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== 第12章 解答例の動作確認 ===");

        List<Student> students = List.of(
                new Student("Alice", 20, 3.8, "Computer Science"),
                new Student("Bob", 22, 3.5, "Mathematics"),
                new Student("Charlie", 21, 3.9, "Computer Science"));
        StudentAnalyzer studentAnalyzer = new StudentAnalyzer(students);
        check("全員の名前", List.of("Alice", "Bob", "Charlie"), studentAnalyzer.getAllNames());
        check("GPAが3.8以上の学生の数", 2, studentAnalyzer.filterByGPA(3.8).size());
        Map<String, List<Student>> groups = studentAnalyzer.groupByDepartment();
        check("学部の並び", List.of("Computer Science", "Mathematics"), List.copyOf(groups.keySet()));
        check("Computer Scienceの人数", 2, groups.get("Computer Science").size());
        check("平均GPA（小数第3位まで）", "3.733", "%.3f".formatted(studentAnalyzer.calculateAverageGPA()));
        check("学生がいなければ平均は0", 0.0, new StudentAnalyzer(List.of()).calculateAverageGPA());
        List<Student> top = studentAnalyzer.getTopStudents(2);
        check("GPAの上位2人", "Charlie,Alice", top.get(0).getName() + "," + top.get(1).getName());

        List<Product> products = List.of(
                new Product("Laptop", "Electronics", 1200.0, 5),
                new Product("Mouse", "Electronics", 25.0, 50),
                new Product("Book", "Education", 30.0, 0));
        SalesAnalyzer salesAnalyzer = new SalesAnalyzer(products);
        check("カテゴリごとの売上", Map.of("Electronics", 7250.0, "Education", 0.0),
                salesAnalyzer.calculateSalesByCategory());
        check("在庫切れの商品", "Book", salesAnalyzer.getOutOfStockProducts().get(0).getName());
        check("20〜100の価格の商品", List.of("Mouse", "Book"),
                salesAnalyzer.filterByPriceRange(20, 100).stream().map(Product::getName).toList());
        check("売上が最も多い商品", "Laptop", salesAnalyzer.getTopSellingProducts(1).get(0).getName());
        check("商品の説明", "Laptop (Electronics)", salesAnalyzer.getProductDescriptions().get(0));

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        check("偶数の平方を文字列に", List.of("4", "16", "36"), NumberPipeline.evenSquares(numbers));
        check("奇数のグループ", List.of(1, 3, 5), NumberPipeline.partitionByEven(numbers).get(false));
        check("合計", 21, NumberPipeline.sum(numbers));
        check("入れ子のリストを平らにする", List.of(1, 2, 3),
                NumberPipeline.flatten(List.of(List.of(1), List.of(), List.of(2, 3))));
        check("4より大きい最初の数", Optional.of(5), NumberPipeline.firstGreaterThan(numbers, 4));
        check("10より大きい数はない", true, NumberPipeline.firstGreaterThan(numbers, 10).isEmpty());

        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, Object expected, Object actual) {
        total++;
        if (Objects.equals(expected, actual)) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
