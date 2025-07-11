/**
 * リスト22-1
 * コードスニペット
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (54行目)
 */

/**
 * 2つの整数の和を計算して返します。
 * <p>
 * このメソッドは、nullを許容せず、オーバーフローも考慮しません。
 * </p>
 *
 * @param a 加算する最初の整数
 * @param b 加算する2番目の整数
 * @return 2つの整数の和
 * @throws ArithmeticException 計算結果がオーバーフローした場合（この例では発生しないが記述例として）
 * @see Math#addExact(int, int)
 * @since 1.0
 * @author Taro Yamada
 */
public int add(int a, int b) {
    return a + b;
}