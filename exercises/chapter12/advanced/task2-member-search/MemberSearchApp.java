import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 第12章 発展課題2: 会員情報の検索（解答例）
 *
 * 見つからないかもしれない値をOptionalで受け取り、map・flatMap・filter・orElse・
 * ifPresentOrElseで、nullの確認を書かずに処理します。
 */
public class MemberSearchApp {
    public static void main(String[] args) {
        MemberDirectory directory = new MemberDirectory(
                List.of(new Member("M001", "佐藤花子", "hanako@example.com", 1200),
                        new Member("M002", "鈴木一郎", null, 300),
                        new Member("M003", "高橋健太", "kenta@example.com", 2500),
                        new Member("M004", "田中花子", null, 800)),
                Map.of("SPRING", 100, "WELCOME", 300));

        IO.println("=== 会員番号で探す ===");
        for (String id : List.of("M001", "M009")) {
            String name = directory.findById(id)
                    .map(Member::getName)
                    .orElse("該当する会員はいません");
            IO.println("  " + id + ": " + name);
        }

        IO.println();
        IO.println("=== メールアドレス ===");
        for (String id : List.of("M001", "M002", "M009")) {
            String email = directory.emailOf(id).orElse("（なし）");
            IO.println("  " + id + ": " + email);
        }

        IO.println();
        IO.println("=== 名前で探す ===");
        for (String part : List.of("花子", "次郎")) {
            directory.findByName(part).ifPresentOrElse(
                    m -> IO.println("  「" + part + "」: " + m.getId() + " " + m.getName()),
                    () -> IO.println("  「" + part + "」: 見つかりません"));
        }

        IO.println();
        IO.println("=== ゴールド会員（1000ポイント以上）か ===");
        for (String id : List.of("M001", "M002", "M009")) {
            boolean gold = directory.findById(id)
                    .filter(m -> m.getPoints() >= 1000)
                    .isPresent();
            IO.println("  " + id + ": " + (gold ? "ゴールド会員" : "一般または該当なし"));
        }

        IO.println();
        IO.println("=== クーポン ===");
        int price = 2000;
        for (String code : List.of("WELCOME", "SUMMER")) {
            int discount = directory.couponDiscount(code).orElse(0);
            IO.println("  " + code + ": " + discount + "円引き → " + (price - discount) + "円");
        }

        IO.println();
        IO.println("=== まとめて探す（見つかった会員だけ） ===");
        List<String> names = List.of("M003", "M008", "M001").stream()
                .map(directory::findById)
                .flatMap(Optional::stream)
                .map(Member::getName)
                .toList();
        IO.println("  " + names);

        IO.println();
        IO.println("=== メールを送れる会員 ===");
        List<String> emails = List.of("M001", "M002", "M003", "M004").stream()
                .map(directory::emailOf)
                .flatMap(Optional::stream)
                .toList();
        IO.println("  " + emails);

        IO.println();
        IO.println("=== ポイントが最も多い会員 ===");
        IO.println("  " + directory.topMember()
                .map(m -> m.getName() + "（" + m.getPoints() + "ポイント）")
                .orElse("会員がいません"));
    }
}
