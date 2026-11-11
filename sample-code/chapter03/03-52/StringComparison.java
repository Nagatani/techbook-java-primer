public class StringComparison {
    public static void main(String[] args) {
        String str1 = new String("Hello");
        String str2 = new String("Hello");
        String str3 = str1;
        
        // ==演算子：参照の比較
        IO.println(str1 == str2);  // false（異なるオブジェクト）
        IO.println(str1 == str3);  // true（同じオブジェクトを参照）
        
        // equalsメソッド：値の比較
        IO.println(str1.equals(str2));  // true（同じ値）
        IO.println(str1.equals(str3));  // true（同じ値）
    }
}
