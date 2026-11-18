import java.util.ArrayList;

/**
 * 配列とジェネリクスの違いを観察する。
 * 配列は実行時に要素の型を持ち（具象化される）、代入のたびに検査する。
 * ジェネリクスは実行時に型引数を持たないので、検査をコンパイル時に済ませる。
 *
 *   javac ArraysAndGenerics.java && java ArraysAndGenerics
 */
public class ArraysAndGenerics {
    public static void main(String[] args) {
        // 配列は共変: String[]をObject[]の変数に代入できる
        Object[] objects = new String[1];
        try {
            objects[0] = 1;             // 実行時に検査され、失敗する
        } catch (ArrayStoreException e) {
            IO.println("ArrayStoreException: " + e.getMessage());
        }
        IO.println("配列のクラス: " + new String[0].getClass().getName()
                + " / " + new Integer[0].getClass().getName());

        // ジェネリクスの型は実行時に区別できない
        IO.println("リストのクラス: " + new ArrayList<String>().getClass().getName()
                + " / " + new ArrayList<Integer>().getClass().getName());

    }
}
