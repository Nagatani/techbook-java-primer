import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 反復の途中でコレクションを変更したときの、イテレーターのfail-fastな振る舞いを見る。
 * スレッドを1つしか使わなくてもConcurrentModificationExceptionになる。
 *
 *   javac FailFast.java
 *   java FailFast
 */
public class FailFast {

    public static void main(String[] args) {
        removeInForEach("B");
        removeInForEach("C");   // 最後から2番目の要素
        removeWithIterator();
        removeIf();
        mapPutWhileIterating();
    }

    /** 拡張for文の中でlist.removeを呼ぶ */
    static void removeInForEach(String target) {
        List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));
        try {
            for (String s : list) {
                if (s.equals(target)) {
                    list.remove(s);
                }
            }
            IO.println("拡張for文で" + target + "をremove: 例外なし 結果=" + list);
        } catch (ConcurrentModificationException e) {
            IO.println("拡張for文で" + target + "をremove: ConcurrentModificationException");
        }
    }

    /** Iterator.removeで消す */
    static void removeWithIterator() {
        List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));
        for (Iterator<String> it = list.iterator(); it.hasNext();) {
            if (it.next().equals("B")) {
                it.remove();
            }
        }
        IO.println("Iterator.removeでBを消す: 結果=" + list);
    }

    /** removeIfで消す */
    static void removeIf() {
        List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));
        list.removeIf(s -> s.equals("B"));
        IO.println("removeIfでBを消す: 結果=" + list);
    }

    /** HashMapのキーをたどりながら、新しいキーを入れる */
    static void mapPutWhileIterating() {
        Map<String, Integer> map = new HashMap<>(Map.of("A", 1, "B", 2));
        try {
            for (String key : map.keySet()) {
                map.put(key + "2", 0);
            }
            IO.println("HashMapのkeySetをたどりながらput: 例外なし");
        } catch (ConcurrentModificationException e) {
            IO.println("HashMapのkeySetをたどりながらput: ConcurrentModificationException");
        }
        Map<String, Integer> values = new HashMap<>(Map.of("A", 1, "B", 2));
        for (String key : values.keySet()) {
            values.put(key, 10);   // すでにあるキーの値を置き換えるだけなら構造は変わらない
        }
        IO.println("すでにあるキーの値だけを置き換える: 例外なし 結果=" + new java.util.TreeMap<>(values));
    }
}
