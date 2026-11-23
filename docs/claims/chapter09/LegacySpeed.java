// 第9章：1つのスレッドでのVectorとArrayList、keySet+getとentrySetの走査の時間を比べる
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class LegacySpeed {
    static long fillAndSum(List<Integer> list) {
        long t = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) list.add(i);
        long sum = 0;
        for (int i = 0; i < list.size(); i++) sum += list.get(i);
        long e = System.nanoTime() - t;
        if (sum == 42) System.out.print("");
        return e;
    }

    public static void main(String[] args) {
        int vectorSlower = 0;
        for (int round = 0; round < 7; round++) {
            long v = fillAndSum(new Vector<>());
            long a = fillAndSum(new ArrayList<>());
            if (round >= 2 && v > a) vectorSlower++;
        }
        System.out.println("Vector slower than ArrayList (5 rounds): " + vectorSlower);

        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 500_000; i++) map.put(i, i);
        int entryFaster = 0;
        for (int round = 0; round < 7; round++) {
            long t0 = System.nanoTime();
            long s1 = 0;
            for (Integer k : map.keySet()) s1 += map.get(k);
            long t1 = System.nanoTime();
            long s2 = 0;
            for (Map.Entry<Integer, Integer> en : map.entrySet()) s2 += en.getValue();
            long t2 = System.nanoTime();
            if (round >= 2 && s1 == s2 && (t2 - t1) < (t1 - t0)) entryFaster++;
        }
        System.out.println("entrySet faster than keySet+get (5 rounds): " + entryFaster);
    }
}
