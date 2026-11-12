package chapter10.solutions;

/**
 * GenericBoxのデモ（課題1の解答例）
 */
public class GenericBoxDemo {
    public static void main(String[] args) {
        IO.println("=== GenericBoxデモ ===\n");

        IO.println("--- String型のBox ---");
        GenericBox<String> stringBox = new GenericBox<>();
        stringBox.put("Hello, Generics!");
        stringBox.displayInfo();
        IO.println("取得: " + stringBox.get());

        IO.println("\n--- Integer型のBox ---");
        GenericBox<Integer> intBox = new GenericBox<>(42);
        intBox.displayInfo();

        IO.println("\n--- Pair型のBox ---");
        GenericBox<Pair<String, Integer>> pairBox = new GenericBox<>();
        pairBox.put(new Pair<>("山田太郎", 25));
        pairBox.displayInfo();

        IO.println("\n--- Box操作デモ ---");
        GenericBox<Double> doubleBox = new GenericBox<>(3.14);
        IO.println("空？: " + doubleBox.isEmpty());
        doubleBox.clear();
        IO.println("クリア後、空？: " + doubleBox.isEmpty());
        doubleBox.displayInfo();

        IO.println("\n--- 型安全性 ---");
        GenericBox<String> safeBox = new GenericBox<>("型安全");
        String value = safeBox.get(); // キャストは要らない
        // safeBox.put(100); // String以外は入れられない（コンパイルエラー）
        IO.println("取得した値: " + value);

        IO.println("\n--- ワイルドカード ---");
        displayBoxContent(stringBox);
        displayBoxContent(intBox);
    }

    /**
     * どの型の箱でも受け取る。中身はObjectとして読める（入れることはできない）
     */
    private static void displayBoxContent(GenericBox<?> box) {
        Object content = box.get();
        IO.println("Boxの内容: " + content);
    }
}
