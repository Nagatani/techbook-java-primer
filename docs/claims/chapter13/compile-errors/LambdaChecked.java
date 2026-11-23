// 第13章：検査例外を投げるメソッドをラムダ式のなかで呼ぶとコンパイルエラー
import java.io.IOException;
import java.util.List;

public class LambdaChecked {
    static String load(String name) throws IOException {
        return name;
    }

    static void run(List<String> names) {
        names.stream().map(n -> load(n)).toList();
    }
}
