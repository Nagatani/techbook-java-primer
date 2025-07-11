/**
 * リスト3-49
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (2243行目)
 */

class Example {
    String instanceField = "インスタンス";
    static String staticField = "スタティック";

    void instanceMethod() {
        System.out.println(instanceField);  // OK
        System.out.println(staticField);    // OK
    }

    static void staticMethod() {
        // System.out.println(instanceField);  // エラー！
        System.out.println(staticField);        // OK
        // instanceMethod();                    // エラー！
    }
}