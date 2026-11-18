package com.example.immutability;

import com.example.immutability.PersistentDataStructuresDemo.ImmutableArrayList;
import com.example.immutability.PersistentDataStructuresDemo.PersistentHashMap;
import com.example.immutability.PersistentDataStructuresDemo.PersistentList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 自作の永続的データ構造が正しく動くかを、標準のコレクションと突き合わせて確かめる。
 * 乱数の種を固定しているので、何度実行しても同じ操作の列になる。
 *   java -cp out com.example.immutability.PersistentStructuresCheck
 */
public class PersistentStructuresCheck {
    private static int failures = 0;

    public static void main(String[] args) {
        checkListSharing();
        checkListOperations();
        checkHashMapAgainstHashMap();
        checkImmutableArrayList();
        IO.println(failures == 0 ? "すべての確認に合格" : "不合格: " + failures + "件");
    }

    // prependは元のノードを共有し、元のリストは変わらない
    static void checkListSharing() {
        PersistentList<String> abc = PersistentList.of("A", "B", "C");
        PersistentList<String> xabc = abc.prepend("X");
        PersistentList<String> yabc = abc.prepend("Y");
        check("prependした2つのリストが元のノードを共有する",
                abc.isTailOf(xabc) && abc.isTailOf(yabc));
        check("元のリストは変わらない", abc.toList().equals(List.of("A", "B", "C")));
        check("tailは2番目以降のノードをそのまま使う",
                xabc.tail().equals(abc) && xabc.tail().isTailOf(yabc));
        PersistentList<String> appended = abc.append("D");
        check("appendは共有しない（全体を作り直す）", !appended.sharesHeadWith(abc)
                && !abc.tail().sharesHeadWith(appended.tail())
                && appended.toList().equals(List.of("A", "B", "C", "D")));
        PersistentList<Integer> nums = PersistentList.of(1, 2, 3, 4, 5);
        PersistentList<Integer> odd = nums.filter(n -> n != 2);
        check("filterは最後に除いた要素より後ろを共有する",
                odd.toList().equals(List.of(1, 3, 4, 5))
                        && nums.tail().tail().isTailOf(odd));
    }

    // 長いリストでもmap・filterが動き、ArrayListでの結果と一致する
    static void checkListOperations() {
        PersistentList<Integer> list = PersistentList.empty();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 100_000; i++) {
            list = list.prepend(i);
            expected.add(0, i);
        }
        List<Integer> doubled = new ArrayList<>();
        List<Integer> evens = new ArrayList<>();
        for (int v : expected) {
            doubled.add(v * 2);
            if (v % 2 == 0) {
                evens.add(v);
            }
        }
        check("10万要素のmap", list.map(v -> v * 2).toList().equals(doubled));
        check("10万要素のfilter", list.filter(v -> v % 2 == 0).toList().equals(evens));
        check("size", list.size() == 100_000);
    }

    // 同じ操作の列をHashMapとPersistentHashMapに行い、すべての版を突き合わせる
    static void checkHashMapAgainstHashMap() {
        Random random = new Random(42);
        PersistentHashMap<Key, Integer> map = PersistentHashMap.empty();
        Map<Key, Integer> model = new HashMap<>();
        List<PersistentHashMap<Key, Integer>> versions = new ArrayList<>();
        List<Map<Key, Integer>> snapshots = new ArrayList<>();
        int mismatches = 0;
        for (int step = 0; step < 20_000; step++) {
            // ハッシュ値の衝突を起こすため、ハッシュ値を64通りに絞ったキーを使う
            Key key = new Key(random.nextInt(500));
            if (random.nextInt(3) == 0) {
                map = map.remove(key);
                model.remove(key);
            } else {
                int value = random.nextInt(1000);
                map = map.put(key, value);
                model.put(key, value);
            }
            if (step % 1000 == 0) {
                versions.add(map);
                snapshots.add(new HashMap<>(model));
            }
            if (map.size() != model.size()) {
                mismatches++;
            }
        }
        for (int v = 0; v < versions.size(); v++) {
            for (int k = 0; k < 500; k++) {
                Key key = new Key(k);
                Integer actual = versions.get(v).get(key);
                Integer want = snapshots.get(v).get(key);
                if (actual == null ? want != null : !actual.equals(want)) {
                    mismatches++;
                }
            }
        }
        check("PersistentHashMapの全20版がHashMapの写しと一致する（衝突するキーを含む）",
                mismatches == 0);
    }

    static void checkImmutableArrayList() {
        List<String> source = new ArrayList<>(List.of("a", "b"));
        ImmutableArrayList<String> list = new ImmutableArrayList<>(source);
        source.set(0, "changed");
        ImmutableArrayList<String> appended = list.append("c");
        check("ImmutableArrayListは元のリストの変更を受けない", list.get(0).equals("a"));
        check("appendは新しいリストを返し、元は変わらない",
                list.size() == 2 && appended.equals(List.of("a", "b", "c")));
    }

    // hashCodeを64通りに絞ったキー（ハッシュ値が同じで別のキーができる）
    record Key(int id) {
        @Override
        public int hashCode() {
            return id % 64;
        }
    }

    static void check(String label, boolean ok) {
        if (!ok) {
            failures++;
        }
        IO.println((ok ? "合格: " : "不合格: ") + label);
    }
}
