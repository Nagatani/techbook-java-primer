import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Stack;

/**
 * 継承で余計な操作を引き継いだ例：StackはVectorを継承している。
 * 実行: javac StackInheritance.java && java StackInheritance
 */
public class StackInheritance {
    public static void main(String[] args) {
        IO.println("Stackのスーパークラス: " + Stack.class.getSuperclass().getName());
        String own = Arrays.stream(Stack.class.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !m.isSynthetic())
                .map(Method::getName).sorted().toList().toString();
        IO.println("Stackが宣言するpublicメソッド: " + own);
        IO.println("Stackで呼べるpublicメソッドの数: " + Stack.class.getMethods().length);

        Stack<String> stack = new Stack<>();
        stack.push("1");
        stack.push("2");
        stack.push("3");
        stack.add(0, "割り込み"); // Vectorから引き継いだ、位置を指定して入れる操作
        stack.remove(2);          // 途中の要素も消せる
        IO.println("Stackの中身: " + stack);

        // Dequeには位置を指定する操作がない
        Deque<String> deque = new ArrayDeque<>();
        deque.push("1");
        deque.push("2");
        deque.push("3");
        IO.println("ArrayDequeの中身（先頭が最後に入れた要素）: " + deque);
        boolean hasIndexOps = Arrays.stream(Deque.class.getMethods())
                .anyMatch(m -> m.getName().equals("get") || m.getName().equals("insertElementAt"));
        IO.println("Dequeに位置を指定する操作があるか: " + hasIndexOps);
    }
}
