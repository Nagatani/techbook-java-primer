import java.util.List;

// Objectの値がList<String>かどうかは、実行時に確かめられない
class InstanceofUnsafe {
    boolean test(Object o) {
        return o instanceof List<String>;
    }
}
