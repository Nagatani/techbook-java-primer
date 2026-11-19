// 第9章：VectorとHashtable（Java 1.0から）、コレクションフレームワーク（Java 1.2）、
// オートボクシングと拡張for文（J2SE 5.0）。--release 8で通ることだけを確かめる
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class LegacyCollections {
    static Vector<String> v = new Vector<String>();
    static Hashtable<String, Integer> h = new Hashtable<String, Integer>();
    static Collection<String> c = new ArrayList<String>();

    static int sum(List<Integer> xs) {
        int total = 0;
        for (int x : xs) {
            total += x;
        }
        Integer boxed = total;
        return boxed;
    }
}
