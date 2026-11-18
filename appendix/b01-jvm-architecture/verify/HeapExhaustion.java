import java.util.ArrayList;
import java.util.List;

/**
 * 参照を持ち続けるとGCが回収できず、ヒープを使い切ってOutOfMemoryErrorになることを観察する。
 *
 *   javac HeapExhaustion.java
 *   java -Xmx32m HeapExhaustion
 */
public class HeapExhaustion {
    private static final List<byte[]> CACHE = new ArrayList<>();   // 消さないキャッシュ

    public static void main(String[] args) {
        int megabytes = 0;
        try {
            while (true) {
                CACHE.add(new byte[1024 * 1024]);
                megabytes++;
            }
        } catch (OutOfMemoryError e) {
            int held = megabytes;
            CACHE.clear();                       // 表示のためのメモリを空ける
            IO.println("OutOfMemoryError: " + e.getMessage());
            IO.println("1MBの配列を" + held + "個持ったところで、ヒープが足りなくなった");
        }
    }
}
