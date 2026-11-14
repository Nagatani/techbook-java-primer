package examples.compile;

import java.util.*;

/**
 * 型の不一致エラーの実例と解決方法を示すサンプルコード
 * このファイルには意図的にコンパイルエラーを含むコードが含まれています
 */
public class TypeMismatchExample {
    
    // 基本型の不一致の例
    static class BasicTypeMismatch {
        public static void demonstrateErrors() {
            IO.println("=== 基本型の不一致 ===");
            
            // エラー例1: 文字列を数値型に代入
            // int number = "123";  // コンパイルエラー
            
            // 解決策1: 型変換
            int number1 = Integer.parseInt("123");
            IO.println("文字列から数値への変換: " + number1);
            
            // エラー例2: 小数を整数型に代入
            // int count = 3.14;  // コンパイルエラー
            
            // 解決策2: 明示的キャスト
            int count = (int) 3.14;  // 小数部分は切り捨て
            IO.println("小数から整数への変換: " + count);
            
            // 解決策3: 適切な型を使用
            double pi = 3.14;
            IO.println("適切な型の使用: " + pi);
            
            // エラー例3: 大きな数値を小さな型に代入
            // byte small = 1000;  // コンパイルエラー（byteの範囲は-128〜127）
            
            // 解決策4: 適切な型またはキャスト
            byte small = (byte) 100;  // 範囲内の値
            short medium = 1000;      // より大きな型を使用
            IO.println("byte値: " + small + ", short値: " + medium);
        }
    }
    
    // 参照型の不一致の例
    static class ReferenceTypeMismatch {
        public static void demonstrateErrors() {
            IO.println("\n=== 参照型の不一致 ===");
            
            // エラー例1: 異なるクラス間の代入
            // Integer boxed = 42;
            // String text = boxed;  // コンパイルエラー
            
            // 解決策1: 適切な変換メソッドを使用
            String text1 = String.valueOf(42);
            String text2 = Integer.toString(42);
            String text3 = "" + 42;  // 文字列連結による変換
            IO.println("数値から文字列: " + text1 + ", " + text2 + ", " + text3);
            
            // エラー例2: 配列型の不一致
            // int[] numbers = new String[5];  // コンパイルエラー
            
            // 解決策2: 正しい型の配列を作成
            int[] numbers = new int[5];
            String[] strings = new String[5];
            
            // エラー例3: 継承関係のない型への代入
            // List<String> list = new HashMap<>();  // コンパイルエラー
            
            // 解決策3: 正しい実装クラスを使用
            List<String> list = new ArrayList<>();
            Map<String, Integer> map = new HashMap<>();
        }
    }
    
    // ジェネリクスの型不一致の例
    static class GenericTypeMismatch {
        public static void demonstrateErrors() {
            IO.println("\n=== ジェネリクスの型不一致 ===");
            
            // エラー例1: ジェネリック型パラメーターの不一致
            // List<String> strings = new ArrayList<Integer>();  // コンパイルエラー
            
            // 解決策1: 型パラメーターを一致させる
            List<String> strings = new ArrayList<String>();
            // または型推論を使用（Java 7以降）
            List<String> strings2 = new ArrayList<>();
            
            // エラー例2: raw typeの使用（警告）
            List rawList = new ArrayList();  // 警告が出る
            rawList.add("文字列");
            rawList.add(123);  // 異なる型が混在（実行時エラーの原因）
            
            // 解決策2: ジェネリクスを適切に使用
            List<Object> mixedList = new ArrayList<>();
            mixedList.add("文字列");
            mixedList.add(123);
            
            // 型安全な処理
            for (Object obj : mixedList) {
                if (obj instanceof String) {
                    IO.println("文字列: " + obj);
                } else if (obj instanceof Integer) {
                    IO.println("整数: " + obj);
                }
            }
        }
        
        // ワイルドカードの使用例
        public static void wildcardExample() {
            IO.println("\n=== ワイルドカードの使用 ===");
            
            List<Integer> integers = Arrays.asList(1, 2, 3);
            List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3);
            
            // エラー例: List<Number> numbers = integers;  // コンパイルエラー
            
            // 解決策: ワイルドカードを使用
            List<? extends Number> numbers;
            numbers = integers;  // OK
            numbers = doubles;   // OK
            
            // 読み取り専用として使用
            for (Number num : numbers) {
                IO.println("数値: " + num);
            }
        }
    }
    
    // メソッドの戻り値型の不一致
    static class MethodReturnTypeMismatch {
        // エラー例: 戻り値の型が一致しない
        /*
        public int calculate() {
            return "結果";  // コンパイルエラー
        }
        */
        
        // 解決策1: 正しい型を返す
        public int calculate() {
            return 42;
        }
        
        // 解決策2: 戻り値の型を変更
        public String calculateAsString() {
            return "結果: " + 42;
        }
        
        // エラー例: voidメソッドで値を返す
        /*
        public void process() {
            return 42;  // コンパイルエラー
        }
        */
        
        // 解決策3: 戻り値が必要な場合は型を変更
        public int processAndReturn() {
            // 処理
            return 42;
        }
        
        public static void demonstrate() {
            IO.println("\n=== メソッドの戻り値型 ===");
            
            MethodReturnTypeMismatch example = new MethodReturnTypeMismatch();
            IO.println("計算結果: " + example.calculate());
            IO.println(example.calculateAsString());
        }
    }
    
    // 実用的な型変換の例
    static class PracticalConversions {
        public static void demonstrate() {
            IO.println("\n=== 実用的な型変換 ===");
            
            // 文字列と数値の相互変換
            String strNum = "456";
            int intNum = Integer.parseInt(strNum);
            double doubleNum = Double.parseDouble("3.14159");
            
            IO.println("文字列→整数: " + intNum);
            IO.println("文字列→小数: " + doubleNum);
            
            // プリミティブ型とラッパークラス（オートボクシング/アンボクシング）
            Integer boxed = 100;  // オートボクシング
            int primitive = boxed;  // アンボクシング
            
            // nullに注意
            Integer nullableInt = null;
            // int value = nullableInt;  // 実行時にNullPointerException
            int safeValue = (nullableInt != null) ? nullableInt : 0;
            IO.println("null安全な値: " + safeValue);
            
            // コレクション間の変換
            List<String> list = Arrays.asList("A", "B", "C");
            Set<String> set = new HashSet<>(list);
            String[] array = list.toArray(new String[0]);
            
            IO.println("List: " + list);
            IO.println("Set: " + set);
            IO.println("Array: " + Arrays.toString(array));
        }
    }
    
    public static void main(String[] args) {
        IO.println("型の不一致エラー サンプルプログラム");
        IO.println("===================================");
        
        BasicTypeMismatch.demonstrateErrors();
        ReferenceTypeMismatch.demonstrateErrors();
        GenericTypeMismatch.demonstrateErrors();
        GenericTypeMismatch.wildcardExample();
        MethodReturnTypeMismatch.demonstrate();
        PracticalConversions.demonstrate();
    }
}