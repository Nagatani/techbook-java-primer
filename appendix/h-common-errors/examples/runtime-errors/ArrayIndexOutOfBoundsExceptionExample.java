package examples.runtime;

import java.util.Arrays;

/**
 * ArrayIndexOutOfBoundsExceptionの実例と解決方法を示すサンプルコード
 */
public class ArrayIndexOutOfBoundsExceptionExample {
    
    // エラーを発生させる例
    static class ErrorExample {
        public static void demonstrateError() {
            System.out.println("=== ArrayIndexOutOfBoundsException発生例 ===");
            
            int[] numbers = new int[5];  // インデックスは0〜4
            
            try {
                // エラー: i <= lengthなので、i=5でエラー
                for (int i = 0; i <= numbers.length; i++) {
                    numbers[i] = i * 10;
                    System.out.println("numbers[" + i + "] = " + numbers[i]);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("\nエラー発生: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        public static void demonstrateNegativeIndex() {
            System.out.println("\n=== 負のインデックスによるエラー ===");
            
            int[] array = {10, 20, 30, 40, 50};
            
            try {
                int offset = 10;  // 配列の長さより大きい
                int value = array[array.length - offset];  // 負のインデックス
                System.out.println("値: " + value);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("負のインデックスエラー: " + e.getMessage());
            }
        }
    }
    
    // 解決策1: 正しいループ条件
    static class Solution1 {
        public static void demonstrate() {
            System.out.println("\n=== 解決策1: 正しいループ条件 ===");
            
            int[] numbers = new int[5];
            
            // 正しい: i < length
            System.out.println("通常のforループ:");
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = i * 10;
                System.out.println("numbers[" + i + "] = " + numbers[i]);
            }
            
            // 拡張for文を使用（インデックスエラーを防ぐ）
            System.out.println("\n拡張for文:");
            for (int number : numbers) {
                System.out.println("値: " + number);
            }
        }
    }
    
    // 解決策2: 境界チェック
    static class Solution2 {
        public static void safeArrayAccess(int[] array, int index) {
            if (index >= 0 && index < array.length) {
                System.out.println("array[" + index + "] = " + array[index]);
            } else {
                System.err.println("インデックス " + index + 
                    " は範囲外です（有効範囲: 0〜" + (array.length - 1) + "）");
            }
        }
        
        public static void demonstrate() {
            System.out.println("\n=== 解決策2: 境界チェック ===");
            
            int[] array = {10, 20, 30, 40, 50};
            
            // 正常なアクセス
            safeArrayAccess(array, 2);
            
            // 範囲外のアクセス（エラーにならない）
            safeArrayAccess(array, -1);
            safeArrayAccess(array, 5);
            safeArrayAccess(array, 100);
        }
    }
    
    // 解決策3: 安全なアクセスメソッド
    static class SafeArray {
        private int[] data;
        
        public SafeArray(int[] data) {
            this.data = data;
        }
        
        // 範囲外の場合はデフォルト値を返す
        public int getOrDefault(int index, int defaultValue) {
            if (index >= 0 && index < data.length) {
                return data[index];
            }
            return defaultValue;
        }
        
        // 循環インデックス（範囲を超えたら最初に戻る）
        public int getCircular(int index) {
            if (data.length == 0) {
                throw new IllegalStateException("配列が空です");
            }
            // 負の値でも正しく動作する
            int normalizedIndex = ((index % data.length) + data.length) % data.length;
            return data[normalizedIndex];
        }
        
        // 範囲をクランプ（制限）する
        public int getClamped(int index) {
            if (data.length == 0) {
                throw new IllegalStateException("配列が空です");
            }
            int clampedIndex = Math.max(0, Math.min(index, data.length - 1));
            return data[clampedIndex];
        }
        
        public static void demonstrate() {
            System.out.println("\n=== 解決策3: 安全なアクセスメソッド ===");
            
            int[] array = {10, 20, 30, 40, 50};
            SafeArray safeArray = new SafeArray(array);
            
            System.out.println("通常のアクセス:");
            System.out.println("index=2: " + safeArray.getOrDefault(2, -1));
            System.out.println("index=10: " + safeArray.getOrDefault(10, -1));
            
            System.out.println("\n循環インデックス:");
            System.out.println("index=7: " + safeArray.getCircular(7));  // 7 % 5 = 2
            System.out.println("index=-1: " + safeArray.getCircular(-1)); // 最後の要素
            
            System.out.println("\nクランプインデックス:");
            System.out.println("index=-5: " + safeArray.getClamped(-5)); // 0にクランプ
            System.out.println("index=10: " + safeArray.getClamped(10)); // 4にクランプ
        }
    }
    
    // 二次元配列での例
    static class TwoDimensionalExample {
        public static void demonstrateError() {
            System.out.println("\n=== 二次元配列でのエラー ===");
            
            int[][] matrix = new int[3][4];  // 3行4列
            
            try {
                // エラー: 内側のループでmatrix.lengthを使っている
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix.length; j++) {  // 間違い！
                        matrix[i][j] = i * 10 + j;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("二次元配列エラー: " + e.getMessage());
            }
        }
        
        public static void demonstrateSolution() {
            System.out.println("\n=== 二次元配列の正しい処理 ===");
            
            int[][] matrix = new int[3][4];  // 3行4列
            
            // 正しい: 各行の長さを使用
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    matrix[i][j] = i * 10 + j;
                }
            }
            
            // 結果を表示
            for (int i = 0; i < matrix.length; i++) {
                System.out.println("行 " + i + ": " + Arrays.toString(matrix[i]));
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("ArrayIndexOutOfBoundsException サンプルプログラム");
        System.out.println("===============================================");
        
        // エラー例の実行
        ErrorExample.demonstrateError();
        ErrorExample.demonstrateNegativeIndex();
        
        // 各解決策の実行
        Solution1.demonstrate();
        Solution2.demonstrate();
        Solution3.demonstrate();
        
        // 二次元配列の例
        TwoDimensionalExample.demonstrateError();
        TwoDimensionalExample.demonstrateSolution();
    }
}