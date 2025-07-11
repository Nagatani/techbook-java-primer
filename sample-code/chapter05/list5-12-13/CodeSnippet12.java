/**
 * リスト5-12
 * MyStackクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (689行目)
 */

// 悪い例：Stackを継承したMyStack
public class MyStack<E> extends ArrayList<E> {
    public void push(E item) {
        add(item);
    }
    
    public E pop() {
        return remove(size() - 1);
    }
    
    public E peek() {
        return get(size() - 1);
    }
}

// 問題：ArrayListのすべてのメソッドが公開されてしまう
MyStack<String> stack = new MyStack<>();
stack.push("A");
stack.push("B");
stack.add(0, "C");  // スタックの途中に挿入できてしまう！
stack.remove(1);    // スタックの途中から削除できてしまう！