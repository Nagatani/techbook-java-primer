// 第10章：ローカル変数の型推論var（Java 10で導入、JEP 286）
import java.util.ArrayList;

public class VarLocal {
    static int size() {
        var list = new ArrayList<String>();
        return list.size();
    }
}
