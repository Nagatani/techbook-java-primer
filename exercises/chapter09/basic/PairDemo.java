package chapter09.basic;

import java.util.Arrays;
import java.util.List;

/**
 * Pairクラスとジェネリックメソッドのデモプログラム
 * 
 * 複数の型パラメータとジェネリックメソッドの使い方を学習します。
 */
public class PairDemo {
    public static void main(String[] args) {
        System.out.println("=== Pairクラスデモ ===\n");
        
        // TODO: String-Integer型のPairを作成
        System.out.println("--- String-Integer Pair ---");
        // Pair<String, Integer> nameAge = new Pair<>("山田太郎", 25);
        // nameAge.displayInfo();
        
        // TODO: Integer-Double型のPairを作成
        System.out.println("\n--- Integer-Double Pair ---");
        // Pair<Integer, Double> scoreGrade = new Pair<>(85, 3.5);
        // System.out.println("スコア: " + scoreGrade.getFirst());
        // System.out.println("評点: " + scoreGrade.getSecond());
        
        // TODO: Pairの値を入れ替え
        System.out.println("\n--- Pairの入れ替え ---");
        // Pair<String, Integer> original = new Pair<>("項目", 100);
        // Pair<Integer, String> swapped = original.swap();
        // System.out.println("元: " + original);
        // System.out.println("入れ替え後: " + swapped);
        
        // TODO: NumberBoxのデモ
        System.out.println("\n=== NumberBoxデモ ===");
        // NumberBox<Integer> intBox = new NumberBox<>(42);
        // NumberBox<Double> doubleBox = new NumberBox<>(3.14);
        // intBox.displayInfo();
        // doubleBox.displayInfo();
        // System.out.println("比較結果: " + intBox.compareTo(doubleBox));
        
        // TODO: GenericUtilsのデモ
        System.out.println("\n=== GenericUtilsデモ ===");
        
        // 配列の要素入れ替え
        System.out.println("\n--- 配列要素の入れ替え ---");
        // String[] words = {"Hello", "World", "Java", "Generics"};
        // System.out.print("元の配列: ");
        // GenericUtils.printArray(words);
        // GenericUtils.swap(words, 0, 2);
        // System.out.print("入れ替え後: ");
        // GenericUtils.printArray(words);
        
        // 要素の検索
        System.out.println("\n--- 要素の検索 ---");
        // Integer[] numbers = {10, 20, 30, 40, 50};
        // int index = GenericUtils.find(numbers, 30);
        // System.out.println("30のインデックス: " + index);
        
        // 最大値の検索
        System.out.println("\n--- 最大値 ---");
        // String max = GenericUtils.max("Apple", "Banana");
        // System.out.println("最大値: " + max);
        
        // リストの最小値
        System.out.println("\n--- リストの最小値 ---");
        // List<Integer> numList = Arrays.asList(5, 2, 8, 1, 9);
        // Integer min = GenericUtils.findMin(numList);
        // System.out.println("最小値: " + min);
        
        // Pairの結合
        System.out.println("\n--- Pairの結合 ---");
        // Pair<String, Integer> p1 = new Pair<>("Start", 1);
        // Pair<Double, String> p2 = new Pair<>(3.14, "End");
        // Pair<String, String> combined = GenericUtils.combinePairs(p1, p2);
        // System.out.println("結合結果: " + combined);
    }
}