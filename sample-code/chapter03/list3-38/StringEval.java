/**
 * リスト3-38
 * StringEvalクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (1936行目)
 */

public class StringEval {
  public static void main(String[] args) {

    String a = "Hello";
    String b = "Hello";

    if (a == b) {
      System.out.println("同じだよ！");  // こっち
    } else {
      System.out.println("違うよ！");
    }

    // a,b両方に処理を加えて値を変化させる
    a += 1;
    b += 1;
    System.out.println("a:" + System.identityHashCode(a));
    System.out.println("b:" + System.identityHashCode(b));

    if (a == b) {
      System.out.println("同じだよ！");
    } else {
      System.out.println("違うよ！");  // こっち
    }

    if (a.equals(b)) {
      System.out.println("equalsなら同じだよ！");  // こっち
    } else {
      System.out.println("equalsでも違うよ！");
    }
  }
}