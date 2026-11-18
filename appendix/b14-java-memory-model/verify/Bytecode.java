/**
 * count++ とオブジェクトの公開が、バイトコードでは何段階の操作になるかを観察する。
 *
 *   javac Bytecode.java && javap -c -p -v Bytecode
 */
public class Bytecode {

    static volatile int count;
    static int plainCount;

    static Bytecode instance;
    final int value;

    Bytecode(int value) {
        this.value = value;
    }

    /** volatileのフィールドを増やす（読む・足す・書くの3段階） */
    static void incrementVolatile() {
        count++;
    }

    /** volatileでないフィールドを増やす（命令はincrementVolatileと同じ） */
    static void incrementPlain() {
        plainCount++;
    }

    /** オブジェクトを作り、フィールドに入れて公開する */
    static void publish() {
        instance = new Bytecode(42);
    }

    public static void main(String[] args) {
        incrementVolatile();
        incrementPlain();
        publish();
        IO.println(count + " " + plainCount + " " + instance.value);
    }
}
