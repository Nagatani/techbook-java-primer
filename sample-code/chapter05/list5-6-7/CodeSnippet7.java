/**
 * リスト5-7
 * MyStackクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (424行目)
 */

// 良い例：内部実装を隠蔽
public class MyStack<E> {
    private final ArrayList<E> elements = new ArrayList<>(); // privateで隠蔽
    
    public void push(E item) {
        elements.add(item);
    }
    
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.remove(elements.size() - 1);
    }
    
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.get(elements.size() - 1);
    }
    
    public boolean isEmpty() {
        return elements.isEmpty();
    }
    
    public int size() {
        return elements.size();
    }
    // ArrayListの他のメソッドは公開されない
}