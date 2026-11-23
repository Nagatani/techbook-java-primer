// 第9章：要素数を10倍にしたときの時間の伸び方（ArrayList.getとLinkedList.get、HashSetとTreeSetの検索）
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ComplexityProbe {
    static long timeGet(List<Integer> list, int reps) {
        long t = System.nanoTime();
        long sum = 0;
        int n = list.size();
        for (int i = 0; i < reps; i++) sum += list.get((int) ((i * 2654435761L) % n));
        long e = System.nanoTime() - t;
        if (sum == 42) System.out.print("");
        return e;
    }

    static long timeContains(Set<Integer> set, int n, int reps) {
        long t = System.nanoTime();
        int hits = 0;
        for (int i = 0; i < reps; i++) if (set.contains((int) ((i * 2654435761L) % n))) hits++;
        long e = System.nanoTime() - t;
        if (hits == -1) System.out.print("");
        return e;
    }

    static List<Integer> fill(List<Integer> list, int n) {
        for (int i = 0; i < n; i++) list.add(i);
        return list;
    }

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "list";
        if (mode.equals("list")) {
            for (int w = 0; w < 3; w++) {
                timeGet(fill(new ArrayList<>(), 2_000), 20_000);
                timeGet(fill(new LinkedList<>(), 2_000), 20_000);
            }
            double arr = (double) timeGet(fill(new ArrayList<>(), 200_000), 200_000)
                    / timeGet(fill(new ArrayList<>(), 20_000), 200_000);
            double linked = (double) timeGet(fill(new LinkedList<>(), 20_000), 2_000)
                    / timeGet(fill(new LinkedList<>(), 2_000), 2_000);
            System.out.println("ArrayList.get x10 elements: " + (arr < 4 ? "ほぼ一定" : "増えた"));
            System.out.println("LinkedList.get x10 elements: " + (linked > 4 ? "大きく増えた" : "ほぼ一定"));
        }
    }
}
