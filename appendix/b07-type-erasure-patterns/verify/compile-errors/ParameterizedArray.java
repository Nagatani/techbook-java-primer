import java.util.List;

// パラメーター化型の配列も作れない
class ParameterizedArray {
    List<String>[] create() {
        return new List<String>[10];
    }
}
