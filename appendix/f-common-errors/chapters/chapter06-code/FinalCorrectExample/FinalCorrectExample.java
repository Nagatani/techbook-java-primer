public class FinalCorrectExample {
    public static void main(String[] args) {
        final int number = 10; // 宣言と同時に初期化
        // number = 20; // 再代入しない
        
        // 新しい値が必要な場合は新しい変数を作成
        final int newNumber = 20;
        IO.println("元の値: " + number);
        IO.println("新しい値: " + newNumber);
    }
}
