// 第10章：List<?>にはnull以外を追加できない
import java.util.ArrayList;
import java.util.List;

public class UnknownAdd {
    static void run() {
        List<?> list = new ArrayList<String>();
        list.add("x");
    }
}
