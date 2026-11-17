// 良い例：内部実装を隠蔽
import java.util.ArrayList;
import java.util.EmptyStackException;

public class MyStack {
    // privateで隠蔽
    private final ArrayList<String> elements = new ArrayList<>();
    
    public void push(String item) {
        elements.add(item);
    }
    
    public String pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.remove(elements.size() - 1);
    }
    
    public String peek() {
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
