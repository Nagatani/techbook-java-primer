import java.util.Iterator;

public class MyList<T> implements Iterable<T> {
    private Object[] elements;
    private int size = 0;
    
    public MyList(int capacity) {
        elements = new Object[capacity];
    }
    
    public void add(T element) {
        if (size < elements.length) {
            elements[size++] = element;
        }
    }
    
    @Override
    public Iterator<T> iterator() {
        // 匿名内部クラスでIteratorを実装
        return new Iterator<T>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < size;
            }
            
            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                return (T) elements[index++];
            }
        };
    }
}
