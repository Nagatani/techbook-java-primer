import java.util.ArrayList;
import java.util.List;

/**
 * 第10章 チャレンジ課題: サークルの備品貸出管理（解答例）
 *
 * 番号の型が違う会員（String）と備品（Integer）を、同じジェネリッククラスRepositoryで管理します。
 *
 * 学習ポイント:
 * - ジェネリックインターフェイス Identifiable<K> と、それを実装するクラス・レコード
 * - 複数の型パラメーターと境界 <K extends Comparable<K>, V extends Identifiable<K>>
 * - ? extends（取り出す側）と ? super（入れる側）のワイルドカード、Identifiable<?>
 * - 右辺から型がわかる場面でのvar
 */
public class LendingApp {
    public static void main(String[] args) {
        var desk = new LendingDesk();
        Repository<String, Member> members = desk.getMembers();
        Repository<Integer, Equipment> equipment = desk.getEquipment();

        IO.println("=== 登録 ===");
        members.add(new Member("S003", "鈴木"));
        members.add(new Member("S001", "山田"));
        members.add(new Member("S002", "佐藤"));
        IO.println("S001をもう一度登録: " + members.add(new Member("S001", "田中")));
        var newItems = List.of(
                new Equipment(201, "三脚"),
                new Equipment(101, "デジタルカメラ"),
                new Equipment(301, "プロジェクター"),
                new Equipment(102, "デジタルカメラ"),
                new Equipment(202, "三脚"));
        IO.println("備品を" + equipment.addAll(newItems) + "点登録しました");
        Report.printAll("会員", members.all());
        Report.printAll("備品", equipment.all());
        Report.printAll("200番台の備品", equipment.findRange(200, 299));

        IO.println();
        IO.println("=== 貸出 ===");
        IO.println(desk.lend("S001", 101, 1));
        IO.println(desk.lend("S001", 201, 1));
        IO.println(desk.lend("S001", 301, 2));
        IO.println(desk.lend("S002", 101, 2));
        IO.println(desk.lend("S002", 301, 3));
        IO.println(desk.lend("S003", 999, 3));
        IO.println(desk.lend("S009", 102, 3));

        IO.println();
        Report.printAll("10日の時点で期限を過ぎている備品", desk.overdue(10));

        IO.println();
        IO.println("=== 返却 ===");
        IO.println(desk.giveBack(101, 10));
        IO.println(desk.giveBack(301, 10));
        IO.println(desk.giveBack(102, 10));
        Report.printAll("山田さんが借りている備品", desk.loansOf("S001"));

        IO.println();
        IO.println("=== すべての登録データ ===");
        // 会員も備品もIdentifiable<?>なので、1つのリストにまとめられる
        List<Identifiable<?>> everything = new ArrayList<>();
        members.exportTo(everything);
        equipment.exportTo(everything);
        Report.printAll("登録データ", everything);
    }
}
