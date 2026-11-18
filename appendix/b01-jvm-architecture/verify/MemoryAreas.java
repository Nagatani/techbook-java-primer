import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;

/**
 * JVMが管理するメモリの領域（メモリプール）と、GCの名前を表示する。
 *
 *   javac MemoryAreas.java
 *   java MemoryAreas                     （既定のGC）
 *   java -XX:+UseSerialGC MemoryAreas    （GCを変えると、ヒープの領域の名前も変わる）
 *   java -XX:MaxRAM=1g MemoryAreas       （メモリが1GBの機械とみなしたときの、ヒープの上限）
 */
public class MemoryAreas {
    public static void main(String[] args) {
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            String type = switch (pool.getType()) {
                case HEAP -> "ヒープ";
                case NON_HEAP -> "ヒープ以外";
            };
            IO.println("メモリプール: " + pool.getName() + "（" + type + "）");
        }
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            IO.println("GC: " + gc.getName());
        }
        IO.println("ヒープの上限: " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + "MB");
    }
}
