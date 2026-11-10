// ジェネリクスがなかった時代のコード（現在は非推奨）
import java.util.ArrayList;
import java.util.List;

public class WithoutGenericsExample {
    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("Hello");
        list.add(123); // 文字列も数値も、何でも入ってしまう！

        // 取り出すときにキャストが必要
        String message = (String) list.get(0); // OK
        IO.println("取り出した文字列: " + message);

        // もし間違った型で取り出そうとすると...
        // コンパイルは通るが実行時にClassCastExceptionが発生
        // String wrong = (String) list.get(1); 
    }
}
