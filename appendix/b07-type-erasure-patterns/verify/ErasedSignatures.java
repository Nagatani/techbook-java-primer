import java.io.Serializable;
import java.util.List;

/**
 * 型消去のあとのシグネチャと、呼び出し側に入るキャストを観察する。
 *
 *   javac ErasedSignatures.java
 *   javap -s -p 'ErasedSignatures$Box'       消去後のディスクリプター（descriptor）
 *   javap -c -p ErasedSignatures             呼び出し側のcheckcast
 */
public class ErasedSignatures {
    // 境界のない型パラメーターは Object に消去される
    static class Box<T> {
        private T value;
        void set(T value) { this.value = value; }
        T get() { return value; }
        T[] toArray(T[] a) { return a; }
        void names(List<String> list) { }
    }

    // 境界のある型パラメーターは、いちばん左の境界に消去される
    static class NumberBox<T extends Number> {
        private T value;
        T get() { return value; }
    }

    static class Multi<T extends CharSequence & Comparable<T> & Serializable> {
        T first(T a, T b) { return a.compareTo(b) <= 0 ? a : b; }
    }

    static String useBox(Box<String> box) {
        box.set("hello");               // 引数にはキャストが要らない
        Object o = box.get();           // Objectで受けるならキャストは要らない
        String s = box.get();           // ここに checkcast String が入る
        return s + o;
    }

    public static void main(String[] args) {
        Box<String> box = new Box<>();
        IO.println(useBox(box));
        IO.println(new Multi<String>().first("b", "a"));
    }
}
