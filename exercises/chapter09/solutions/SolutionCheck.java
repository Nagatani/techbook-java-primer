package chapter09.solutions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * 解答例の動作確認（課題1〜4）
 *
 * 期待する値と実際の値を比べ、「OK」か「NG」を表示します。
 * 解答例を書き換えたら実行し、すべてOKになることを確かめます。
 * 第21章で学ぶJUnitを使うと、同じ確認をテストとして書けます。
 */
public class SolutionCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== 第9章 解答例の動作確認 ===");
        checkStudent();
        checkStudentManager();
        checkShoppingCart();
        checkWordCounter();
        checkSetOperations();
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static StudentManager createManager() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new Student("S001", "山田太郎", 2, 85.5));
        manager.addStudent(new Student("S002", "鈴木花子", 1, 92.0));
        manager.addStudent(new Student("S003", "佐藤次郎", 2, 78.0));
        manager.addStudent(new Student("S004", "高橋美咲", 3, 88.5));
        return manager;
    }

    private static void checkStudent() {
        Student a = new Student("S001", "山田太郎", 2, 85.5);
        Student b = new Student("S001", "別の名前", 1, 0.0);
        check("学生IDが同じならequalsがtrue", true, a.equals(b));
        check("学生IDが同じならhashCodeが同じ", a.hashCode(), b.hashCode());
        check("学生IDが違えばequalsがfalse", false, a.equals(new Student("S002", "山田太郎", 2, 85.5)));
        check("nullとのequalsはfalse", false, a.equals(null));
        check("toStringの形", "学生ID: S001, 名前: 山田太郎, 学年: 2, 成績: 85.5", a.toString());
    }

    private static void checkStudentManager() {
        StudentManager manager = createManager();
        manager.addStudent(new Student("S001", "田中一郎", 1, 70.0));
        check("登録済みのIDは追加しない", 4, manager.getStudentCount());
        check("先に登録した学生が残る", "山田太郎", manager.findStudentById("S001").getName());
        check("見つからないIDはnull", null, manager.findStudentById("S009"));

        List<Student> grade2 = manager.getStudentsByGrade(2);
        check("2年生は2人", 2, grade2.size());
        check("絞り込みは登録順", "S001,S003",
                grade2.get(0).getStudentId() + "," + grade2.get(1).getStudentId());
        check("いない学年は空のリスト", true, manager.getStudentsByGrade(4).isEmpty());

        check("平均成績", 86.0, manager.calculateAverageScore());
        check("学生がいなければ平均は0", 0.0, new StudentManager().calculateAverageScore());

        check("削除できる", true, manager.removeStudent("S003"));
        check("2回目の削除はfalse", false, manager.removeStudent("S003"));
        check("削除した学生は検索できない", null, manager.findStudentById("S003"));
        check("削除した学生は学年の一覧にも出ない", 1, manager.getStudentsByGrade(2).size());
    }

    private static void checkShoppingCart() {
        Product laptop = new Product("P001", "ノートパソコン", 80000, 5);
        check("在庫を減らせる", true, laptop.reduceStock(2));
        check("在庫を超えては減らせない", false, laptop.reduceStock(4));
        check("減らせなかったときは在庫が変わらない", 3, laptop.getStock());

        Product mouse = new Product("P002", "マウス", 2000, 10);
        check("小計は単価と数量の積", 6000.0, new CartItem(mouse, 3).getSubtotal());

        ShoppingCart cart = new ShoppingCart("山田太郎");
        cart.addItem(mouse, 2);
        cart.addItem(mouse, 3);
        check("同じ商品を追加すると数量が増える", 5, cart.getTotalItemCount());
        check("合計金額", 10000.0, cart.getTotalAmount());

        Product monitor = new Product("P003", "モニター", 30000, 5);
        check("在庫の範囲なら追加できる", true, cart.addItem(monitor, 4));
        check("カートの数量と合わせて在庫を超える追加は断る", false, cart.addItem(monitor, 2));

        check("数量を0にできる", true, cart.updateQuantity("P002", 0));
        check("数量を0にした商品は除かれる", false, cart.containsProduct("P002"));
        check("カートにない商品の数量は変えられない", false, cart.updateQuantity("P009", 1));

        check("商品を削除できる", true, cart.removeItem("P003"));
        check("2回目の削除はfalse", false, cart.removeItem("P003"));
        cart.addItem(mouse, 1);
        cart.clearCart();
        check("クリアすると空になる", true, cart.isEmpty());
    }

    private static void checkWordCounter() {
        Map<String, Integer> counts = WordCounter.countWords("Java is fun. Java is powerful.");
        check("単語の種類", 4, counts.size());
        check("javaの回数", 2, counts.get("java"));
        check("最も多い単語", List.of("is", "java"), WordCounter.mostFrequentWords(counts));
    }

    private static void checkSetOperations() {
        Set<String> a = new TreeSet<>(List.of("Java", "Python", "C"));
        Set<String> b = new TreeSet<>(List.of("Python", "JavaScript", "Java"));
        check("和集合", Set.of("C", "Java", "JavaScript", "Python"), SetOperations.union(a, b));
        check("積集合", Set.of("Java", "Python"), SetOperations.intersection(a, b));
        check("差集合 A - B", Set.of("C"), SetOperations.difference(a, b));
        check("差集合 B - A", Set.of("JavaScript"), SetOperations.difference(b, a));
        check("元の集合は変わらない", "3,3", a.size() + "," + b.size());
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
