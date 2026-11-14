package chapter10.basic;

import java.util.Arrays;
import java.util.List;

/**
 * Pairクラスとジェネリックメソッドのデモプログラム
 * 
 * 複数の型パラメーターとジェネリックメソッドの使い方を学習します。
 */
public class PairDemo {
    public static void main(String[] args) {
        IO.println("=== Pairクラスデモ ===\n");
        
        // TODO: String-Integer型のPairを作成
        IO.println("--- String-Integer Pair ---");
        // Pair<String, Integer> nameAge = new Pair<>("山田太郎", 25);
        // nameAge.displayInfo();
        
        // TODO: Integer-Double型のPairを作成
        IO.println("\n--- Integer-Double Pair ---");
        // Pair<Integer, Double> scoreGrade = new Pair<>(85, 3.5);
        // IO.println("スコア: " + scoreGrade.getFirst());
        // IO.println("評点: " + scoreGrade.getSecond());
        
        // TODO: Pairの値を入れ替え
        IO.println("\n--- Pairの入れ替え ---");
        // Pair<String, Integer> original = new Pair<>("項目", 100);
        // Pair<Integer, String> swapped = original.swap();
        // IO.println("元: " + original);
        // IO.println("入れ替え後: " + swapped);
        
        // TODO: NumberBoxのデモ
        IO.println("\n=== NumberBoxデモ ===");
        // NumberBox<Integer> intBox = new NumberBox<>(42);
        // NumberBox<Double> doubleBox = new NumberBox<>(3.14);
        // intBox.displayInfo();
        // doubleBox.displayInfo();
        // IO.println("比較結果: " + intBox.compareTo(doubleBox));
        
        // TODO: GenericUtilsのデモ
        IO.println("\n=== GenericUtilsデモ ===");
        
        // 配列の要素入れ替え
        IO.println("\n--- 配列要素の入れ替え ---");
        // String[] words = {"Hello", "World", "Java", "Generics"};
        // IO.print("元の配列: ");
        // GenericUtils.printArray(words);
        // GenericUtils.swap(words, 0, 2);
        // IO.print("入れ替え後: ");
        // GenericUtils.printArray(words);
        
        // 要素の検索
        IO.println("\n--- 要素の検索 ---");
        // Integer[] numbers = {10, 20, 30, 40, 50};
        // int index = GenericUtils.find(numbers, 30);
        // IO.println("30のインデックス: " + index);
        
        // 最大値の検索
        IO.println("\n--- 最大値 ---");
        // String max = GenericUtils.max("Apple", "Banana");
        // IO.println("最大値: " + max);
        
        // リストの最小値
        IO.println("\n--- リストの最小値 ---");
        // List<Integer> numList = Arrays.asList(5, 2, 8, 1, 9);
        // Integer min = GenericUtils.findMin(numList);
        // IO.println("最小値: " + min);
        
        // Pairの結合
        IO.println("\n--- Pairの結合 ---");
        // Pair<String, Integer> p1 = new Pair<>("Start", 1);
        // Pair<Double, String> p2 = new Pair<>(3.14, "End");
        // Pair<String, String> combined = GenericUtils.combinePairs(p1, p2);
        // IO.println("結合結果: " + combined);
    }
}