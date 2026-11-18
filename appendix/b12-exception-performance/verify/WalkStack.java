import java.util.List;

/**
 * StackWalkerで、例外を作らずに呼び出し元をたどる。
 *
 *   javac WalkStack.java && java WalkStack
 */
public class WalkStack {

    static final StackWalker WALKER = StackWalker.getInstance();

    /** 呼び出し元のメソッドを、近い順に3つだけ返す */
    static List<String> callers() {
        return WALKER.walk(frames -> frames
                .skip(1)
                .limit(3)
                .map(f -> f.getMethodName() + ":" + f.getLineNumber())
                .toList());
    }

    static void level(int n) {
        if (n > 0) {
            level(n - 1);
            return;
        }
        IO.println("近い順の呼び出し元: " + callers());
        IO.println("例外で数えた深さ: " + new Throwable().getStackTrace().length);
        IO.println("StackWalkerで数えた深さ: " + WALKER.walk(s -> s.count()));
    }

    public static void main(String[] args) {
        level(100);
    }
}
