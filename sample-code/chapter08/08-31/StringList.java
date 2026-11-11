// 要素を1つずつ順に取り出すためのインターフェイス
interface StringIterator {
    boolean hasNext();
    String next();
}

public class StringList {
    private final String[] elements;
    private int size = 0;
    
    public StringList(int capacity) {
        elements = new String[capacity];
    }
    
    public void add(String element) {
        if (size < elements.length) {
            elements[size++] = element;
        }
    }
    
    public StringIterator iterator() {
        // 匿名内部クラスでStringIteratorを実装
        return new StringIterator() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < size;  // 外側のクラスのフィールドを直接参照
            }
            
            @Override
            public String next() {
                return elements[index++];
            }
        };
    }
    
    public static void main(String[] args) {
        StringList list = new StringList(5);
        list.add("りんご");
        list.add("みかん");
        list.add("ぶどう");
        
        StringIterator it = list.iterator();
        while (it.hasNext()) {
            IO.println(it.next());
        }
    }
}
