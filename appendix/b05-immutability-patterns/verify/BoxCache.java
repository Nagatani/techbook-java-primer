/**
 * Integer.valueOf（オートボクシング）が返すオブジェクトが共有されるかを観察する。
 *   javac BoxCache.java && java BoxCache
 *   java -XX:AutoBoxCacheMax=1000 BoxCache
 */
public class BoxCache {
    public static void main(String[] args) {
        for (int v : new int[] {-129, -128, 127, 128, 1000}) {
            Integer a = v;                  // Integer.valueOf(v)になる
            Integer b = v;
            IO.println("Integer %5d: 同じオブジェクト=%s equals=%s"
                    .formatted(v, a == b, a.equals(b)));
        }
        Long la = 128L;
        Long lb = 128L;
        IO.println("Long 128: 同じオブジェクト=" + (la == lb));
        Long lc = 127L;
        Long ld = 127L;
        IO.println("Long 127: 同じオブジェクト=" + (lc == ld));
        Boolean ba = true;
        Boolean bb = Boolean.valueOf("TRUE");
        IO.println("Boolean: 同じオブジェクト=" + (ba == bb));
    }
}
