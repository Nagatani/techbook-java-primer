import java.util.List;

/** ラムダ式の中から、外のローカル変数に代入し直すことはできない */
public class CaptureMutation {
    static int count(List<String> names) {
        int count = 0;
        names.forEach(name -> count++);
        return count;
    }
}
