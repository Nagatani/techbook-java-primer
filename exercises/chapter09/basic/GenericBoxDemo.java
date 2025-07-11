package chapter09.basic;

/**
 * GenericBoxクラスのデモプログラム
 * 
 * ジェネリッククラスの基本的な使い方を学習します。
 */
public class GenericBoxDemo {
    public static void main(String[] args) {
        System.out.println("=== GenericBoxデモ ===\n");
        
        // TODO: String型のGenericBoxを作成
        System.out.println("--- String型のBox ---");
        // GenericBox<String> stringBox = new GenericBox<>();
        // stringBox.put("Hello, Generics!");
        // stringBox.displayInfo();
        // System.out.println("取得: " + stringBox.get());
        
        // TODO: Integer型のGenericBoxを作成
        System.out.println("\n--- Integer型のBox ---");
        // GenericBox<Integer> intBox = new GenericBox<>(42);
        // intBox.displayInfo();
        
        // TODO: 独自クラス（Student）型のGenericBoxを作成
        System.out.println("\n--- Student型のBox ---");
        // 注意: Studentクラスは第8章で作成したものを使用
        // GenericBox<Student> studentBox = new GenericBox<>();
        // studentBox.put(new Student("S001", "山田太郎", 2, 85.5));
        // studentBox.displayInfo();
        
        // TODO: Boxの操作デモ
        System.out.println("\n--- Box操作デモ ---");
        // GenericBox<Double> doubleBox = new GenericBox<>(3.14);
        // System.out.println("空？: " + doubleBox.isEmpty());
        // doubleBox.clear();
        // System.out.println("クリア後、空？: " + doubleBox.isEmpty());
        
        // TODO: 型安全性のデモ
        System.out.println("\n--- 型安全性 ---");
        // GenericBox<String> safeBox = new GenericBox<>("型安全");
        // String value = safeBox.get(); // キャスト不要
        // System.out.println("取得した値: " + value);
        
        // TODO: ワイルドカードの使用例
        System.out.println("\n--- ワイルドカード ---");
        // displayBoxContent(stringBox);
        // displayBoxContent(intBox);
    }
    
    /**
     * 任意の型のGenericBoxの内容を表示する
     * TODO: ワイルドカード(?)を使用したメソッド
     */
    private static void displayBoxContent(GenericBox<?> box) {
        // TODO: 実装してください
        System.out.println("Boxの内容: " + box.get());
    }
}