/**
 * 文字列リテラルの共有と、実行時に作る文字列を比べる。
 */
public class StringIdentity {
    static final String HEAD = "Ja";

    public static void main(String[] args) {
        String literal = "Java";
        String sameLiteral = "Java";
        String constant = HEAD + "va";          // 定数式なのでコンパイル時に"Java"になる
        String tail = "va";
        String runtime = "Ja" + tail;           // 実行時に結合する
        String created = new String("Java");

        IO.println("リテラルどうし: " + (literal == sameLiteral));
        IO.println("定数式の結合: " + (literal == constant));
        IO.println("実行時の結合: " + (literal == runtime));
        IO.println("new String: " + (literal == created));
        IO.println("intern()の結果: " + (literal == runtime.intern()));
        IO.println("equals: " + literal.equals(runtime));

        // 文字列を変えるように見えるメソッドは、新しい文字列を返す
        String upper = literal.toUpperCase();
        IO.println("toUpperCase()の後の元の文字列: " + literal + "、戻り値: " + upper);
    }
}
