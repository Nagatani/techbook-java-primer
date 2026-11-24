// 問題点が露呈する使用例
public class StackProblem {
    public static void main(String[] args) {
        MyStack stack = new MyStack();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        
        // ArrayListのメソッドが露出してしまう
        stack.add(1, "WRONG!"); // スタックの途中に要素を挿入できてしまう
        stack.remove(0); // スタックの底から要素を削除できてしまう
        stack.clear(); // スタック全体をクリアできてしまう
        
        // スタックの約束（LIFO）が破られる
    }
}
