/**
 * リスト7-9
 * Collectionインターフェース
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (300行目)
 */

// 既存のインターフェイス（多くの実装クラスが存在）
public interface Collection<E> {
    int size();
    boolean isEmpty();
    // ... 他の既存メソッド
    
    // Java 8で追加されたdefaultメソッド
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    
    default Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}

// 既存の実装クラスは変更不要
class MyCollection<E> implements Collection<E> {
    // streamメソッドを実装しなくても、defaultが使われる
}