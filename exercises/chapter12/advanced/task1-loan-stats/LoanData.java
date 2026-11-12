import java.util.List;

/**
 * 図書館の貸出記録の例
 */
public class LoanData {
    public static List<Loan> sample() {
        return List.of(
                new Loan("佐藤", "坊っちゃん", "小説", 4),
                new Loan("鈴木", "Java入門", "技術書", 4),
                new Loan("高橋", "坊っちゃん", "小説", 4),
                new Loan("佐藤", "日本の歴史", "歴史", 4),
                new Loan("田中", "Java入門", "技術書", 5),
                new Loan("鈴木", "こころ", "小説", 5),
                new Loan("佐藤", "Java入門", "技術書", 5),
                new Loan("高橋", "世界の歴史", "歴史", 5),
                new Loan("鈴木", "アルゴリズム図鑑", "技術書", 5),
                new Loan("伊藤", "坊っちゃん", "小説", 6),
                new Loan("佐藤", "こころ", "小説", 6),
                new Loan("佐藤", "世界の歴史", "歴史", 6),
                new Loan("鈴木", "Java入門", "技術書", 6));
    }
}
