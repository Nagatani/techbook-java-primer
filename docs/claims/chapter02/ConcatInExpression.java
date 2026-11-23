// 第2章：1つの式のなかの+の連結は、まとめて1回の処理（invokedynamic）になる
public class ConcatInExpression {
    static String join(String a, String b, String c) {
        return a + "-" + b + "-" + c;
    }
}
