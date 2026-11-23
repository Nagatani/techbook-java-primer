// 第9章：ラッパークラスの比較、Setのnull、LinkedHashMapの並び順、走査中の削除
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CollectionFacts {
    public static void main(String[] args) {
        Integer a = 127, b = 127, c = 128, d = 128;
        System.out.println("127==127: " + (a == b) + ", 128==128: " + (c == d)
                + ", equals: " + c.equals(d));

        Integer none = null;
        try {
            int x = none;
            System.out.println("unboxed " + x);
        } catch (NullPointerException e) {
            System.out.println("unbox null -> NullPointerException");
        }

        List<Integer> nums = new ArrayList<>(List.of(5, 1, 7));
        nums.remove(1);
        System.out.println("remove(1): " + nums);
        nums.remove(Integer.valueOf(7));
        System.out.println("remove(Integer.valueOf(7)): " + nums);

        Set<String> tree = new TreeSet<>();
        try {
            tree.add(null);
        } catch (NullPointerException e) {
            System.out.println("TreeSet.add(null) -> NullPointerException");
        }
        Set<String> hash = new HashSet<>();
        hash.add(null);
        hash.add(null);
        System.out.println("HashSet nulls: " + hash.size());

        Map<String, Integer> lru = new LinkedHashMap<>(16, 0.75f, true);
        lru.put("A", 1);
        lru.put("B", 2);
        lru.put("C", 3);
        lru.get("A");
        System.out.println("access order: " + lru.keySet());

        List<String> list = new ArrayList<>(List.of("a", "b", "c", "d"));
        try {
            for (String s : list) {
                if (s.equals("b")) list.remove(s);
            }
            System.out.println("no exception");
        } catch (ConcurrentModificationException e) {
            System.out.println("remove in for-each -> ConcurrentModificationException");
        }
        List<String> list2 = new ArrayList<>(List.of("a", "b", "c", "d"));
        for (String s : list2) {
            if (s.equals("c")) list2.remove(s);
        }
        System.out.println("remove second-to-last: " + list2);
        List<String> list3 = new ArrayList<>(List.of("a", "b", "c", "d"));
        for (Iterator<String> it = list3.iterator(); it.hasNext();) {
            if (it.next().equals("b")) it.remove();
        }
        System.out.println("iterator remove: " + list3);
    }
}
