// 第10章：List<Number>の引数にList<Integer>は渡せない
import java.util.ArrayList;
import java.util.List;

public class ListInvariance {
    static void addAll(List<Number> list) { }

    static void run() {
        List<Integer> ints = new ArrayList<>();
        addAll(ints);
    }
}
