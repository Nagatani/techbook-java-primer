package chapter10.basic;

/**
 * GenericBoxクラスのデモプログラム
 * 
 * ジェネリッククラスの基本的な使い方を学習します。
 */
public class GenericBoxDemo {
    public static void main(String[] args) {
        IO.println("=== GenericBoxデモ ===\n");
        
        // TODO: String型のGenericBoxを作成
        IO.println("--- String型のBox ---");
        // GenericBox<String> stringBox = new GenericBox<>();
        // stringBox.put("Hello, Generics!");
        // stringBox.displayInfo();
        // IO.println("取得: " + stringBox.get());
        
        // TODO: Integer型のGenericBoxを作成
        IO.println("\n--- Integer型のBox ---");
        // GenericBox<Integer> intBox = new GenericBox<>(42);
        // intBox.displayInfo();
        
        // TODO: 自作のクラス（Pair）型のGenericBoxを作成
        IO.println("\n--- Pair型のBox ---");
        // GenericBox<Pair<String, Integer>> pairBox = new GenericBox<>();
        // pairBox.put(new Pair<>("山田太郎", 25));
        // pairBox.displayInfo();
        
        // TODO: Boxの操作デモ
        IO.println("\n--- Box操作デモ ---");
        // GenericBox<Double> doubleBox = new GenericBox<>(3.14);
        // IO.println("空？: " + doubleBox.isEmpty());
        // doubleBox.clear();
        // IO.println("クリア後、空？: " + doubleBox.isEmpty());
        
        // TODO: 型安全性のデモ
        IO.println("\n--- 型安全性 ---");
        // GenericBox<String> safeBox = new GenericBox<>("型安全");
        // String value = safeBox.get(); // キャスト不要
        // IO.println("取得した値: " + value);
        
        // TODO: ワイルドカードの使用例
        IO.println("\n--- ワイルドカード ---");
        // displayBoxContent(stringBox);
        // displayBoxContent(intBox);
    }
    
    /**
     * 任意の型のGenericBoxの内容を表示する
     * TODO: ワイルドカード(?)を使用したメソッド
     */
    private static void displayBoxContent(GenericBox<?> box) {
        // TODO: 実装してください
        IO.println("Boxの内容: " + box.get());
    }
}