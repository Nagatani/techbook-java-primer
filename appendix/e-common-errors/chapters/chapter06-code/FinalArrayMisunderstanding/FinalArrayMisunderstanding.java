public class FinalArrayMisunderstanding {
    public static void main(String[] args) {
        final int[] numbers = {1, 2, 3, 4, 5};
        
        // これは可能（配列の要素は変更可能）
        numbers[0] = 10; // OK
        
        // これはエラー（配列参照の変更は不可）
        // numbers = new int[]{6, 7, 8}; // コンパイルエラー
        
        IO.println(java.util.Arrays.toString(numbers)); // [10, 2, 3, 4, 5]
    }
}
