/**
 * リスト3-23
 * 実行可能プログラム
 * 
 * 元ファイル: chapter03-oop-basics.md (1353行目)
 */

class ArithmeticSample {
  public static void main(String[] args) {
    int num1 = 10;
    int num2 = 5;

    System.out.println("num1とnum2にいろいろな演算を行います。");
    System.out.println("num1 + num2は" + (num1 + num2) + "です。");
    System.out.println("num1 - num2は" + (num1 - num2) + "です。");
    System.out.println("num1 * num2は" + (num1 * num2) + "です。");
    System.out.println("num1 / num2は" + (num1 / num2) + "です。");
    System.out.println("num1 % num2は" + (num1 % num2) + "です。");
  }
}