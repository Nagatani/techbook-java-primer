import java.util.Scanner;  // Scannerクラスを使用するための宣言

public class InputExample {
    public static void main(String[] args) {
        // Scannerオブジェクトの作成（標準入力から読み取る）
        Scanner scanner = new Scanner(System.in);
        
        // ユーザーへの入力促進メッセージ
        System.out.print("整数値を入力してください: ");
        
        // 文字列として入力を受け取る
        String input = scanner.nextLine();
        
        // 文字列を整数に変換
        int number = Integer.parseInt(input);
        
        // 変換した値を使った処理の例
        System.out.println("入力された値: " + number);
        System.out.println("2倍の値: " + (number * 2));
        
        // Scannerを閉じる（リソースの解放）
        scanner.close();
    }
}
