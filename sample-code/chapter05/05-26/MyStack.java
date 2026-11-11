// 悪い例：実装の詳細を継承してしまう
import java.util.ArrayList;
import java.util.EmptyStackException;

public class MyStack extends ArrayList<String> {
    public void push(String item) {
        add(item);
    }
    
    public String pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return remove(size() - 1);
    }
    
    public String peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return get(size() - 1);
    }
}
