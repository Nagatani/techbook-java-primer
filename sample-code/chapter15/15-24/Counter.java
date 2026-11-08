// エラー例
public class Counter {
    private int count = 0;
    
    public void increment() {
        count++; // 非原子的操作
    }
}
