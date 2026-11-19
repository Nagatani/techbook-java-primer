// 第5章：java.util.StackはVectorを継承している（Java 1.0から）
import java.util.Stack;
import java.util.Vector;

public class StackVector {
    static Vector<String> f() {
        Stack<String> stack = new Stack<String>();
        stack.push("a");
        stack.insertElementAt("b", 0);
        return stack;
    }
}
