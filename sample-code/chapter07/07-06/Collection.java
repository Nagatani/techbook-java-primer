import java.util.Iterator;
import java.util.function.Predicate;

// 既存のインターフェイス（多くの実装クラスが存在）
public interface Collection<E> extends Iterable<E> {
    int size();
    boolean isEmpty();
    boolean contains(Object o);
    Iterator<E> iterator();
    boolean containsAll(Collection<?> c);
    // ... 他の既存メソッド
    
    // Java 8で追加されたdefaultメソッドの例
    // （本物のjava.util.CollectionにもremoveIfがdefaultメソッドとして追加された）
    default boolean removeIf(Predicate<? super E> filter) {
        boolean removed = false;
        Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
}

// 既存の実装クラスは変更不要
// （size()などの既存メソッドの実装は省略するため、ここではabstractにしている）
abstract class MyCollection<E> implements Collection<E> {
    // defaultメソッドを実装しなくても、デフォルト実装が使われる
}
