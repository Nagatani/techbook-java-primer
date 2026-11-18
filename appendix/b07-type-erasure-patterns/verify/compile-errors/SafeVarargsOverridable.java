import java.util.List;

// オーバーライドできるメソッドには@SafeVarargsを付けられない
class SafeVarargsOverridable {
    @SafeVarargs
    void addAll(List<String>... lists) { }
}
