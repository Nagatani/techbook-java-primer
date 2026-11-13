/**
 * 第21章 基本課題1の解答例で使う、テスト対象の計算機
 *
 * basic/JUnitBasics.javaのSimpleCalculatorと同じ内容です。
 * 解答例のテスト（SimpleCalculatorTest.java）と同じフォルダーでコンパイルできるよう、ここにも置いています。
 */
class SimpleCalculator {
    
    /**
     * 加算を行います
     * @param a 値1
     * @param b 値2
     * @return a + b
     */
    public int add(int a, int b) {
        return a + b;
    }
    
    /**
     * 減算を行います
     * @param a 値1
     * @param b 値2
     * @return a - b
     */
    public int subtract(int a, int b) {
        return a - b;
    }
    
    /**
     * 乗算を行います
     * @param a 値1
     * @param b 値2
     * @return a * b
     */
    public int multiply(int a, int b) {
        return a * b;
    }
    
    /**
     * 除算を行います
     * @param a 値1（被除数）
     * @param b 値2（除数）
     * @return a / b
     * @throws IllegalArgumentException bが0の場合
     */
    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("除数は0にできません");
        }
        return (double) a / b;
    }
    
    /**
     * 数値が偶数かどうかを判定します
     * @param number 判定する数値
     * @return 偶数の場合true、奇数の場合false
     */
    public boolean isEven(int number) {
        return number % 2 == 0;
    }
    
    /**
     * 数値が正の値かどうかを判定します
     * @param number 判定する数値
     * @return 正の値の場合true、0または負の値の場合false
     */
    public boolean isPositive(int number) {
        return number > 0;
    }
    
    /**
     * 配列の最大値を取得します
     * @param numbers 数値の配列
     * @return 最大値
     * @throws IllegalArgumentException 配列がnullまたは空の場合
     */
    public int max(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("配列は空にできません");
        }
        
        int max = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }
        return max;
    }
}
