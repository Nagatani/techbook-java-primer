package chapter09.solutions;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 重複の除去と集合演算（課題4の解答例）
 *
 * addAll・retainAll・removeAllは、呼び出した集合そのものを書き換えます。
 * 元の集合を壊さないよう、コピー（new TreeSet<>(a)）に対して操作します。
 */
public class SetOperations {

    public static Set<String> union(Set<String> a, Set<String> b) {
        Set<String> result = new TreeSet<>(a);
        result.addAll(b);
        return result;
    }

    public static Set<String> intersection(Set<String> a, Set<String> b) {
        Set<String> result = new TreeSet<>(a);
        result.retainAll(b);
        return result;
    }

    public static Set<String> difference(Set<String> a, Set<String> b) {
        Set<String> result = new TreeSet<>(a);
        result.removeAll(b);
        return result;
    }

    public static void main(String[] args) {
        List<String> fruits = List.of("apple", "banana", "apple", "orange", "banana", "grape");
        IO.println("元のリスト: " + fruits);
        IO.println("LinkedHashSet（最初に出た順）: " + new LinkedHashSet<>(fruits));
        IO.println("TreeSet（辞書順）: " + new TreeSet<>(fruits));

        // HashSetでは表示の順序が決まらないので、TreeSetで作る
        Set<String> a = new TreeSet<>(List.of("Java", "Python", "C"));
        Set<String> b = new TreeSet<>(List.of("Python", "JavaScript", "Java"));
        IO.println("\nA: " + a);
        IO.println("B: " + b);
        IO.println("和集合: " + union(a, b));
        IO.println("積集合: " + intersection(a, b));
        IO.println("差集合（A-B）: " + difference(a, b));
        IO.println("差集合（B-A）: " + difference(b, a));
    }
}
