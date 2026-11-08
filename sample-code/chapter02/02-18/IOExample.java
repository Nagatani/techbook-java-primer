import java.util.Scanner;
import java.util.InputMismatchException;

public class IOExample {
    public static void main(String[] args) {
        // === 出力メソッドの使い分け ===
        System.out.println("Hello, World!");      // 改行付き出力
        System.out.print("改行なし");             // 改行なし出力
        System.out.print("出力\n");               // 手動で改行を追加
        
        // printf による書式付き出力（C言語風）
        System.out.printf("整数: %d, 浮動小数点: %.2f%n", 10, 3.14159);
        System.out.printf("文字列: %-10s | 右寄せ: %10s%n", "左寄せ", "右寄せ");
        
        // === Scanner による安全な入力処理 ===
        Scanner scanner = new Scanner(System.in);
        
        // 文字列入力
        System.out.print("名前を入力してください: ");
        String name = scanner.nextLine();
        
        // 整数入力（エラー処理付き）
        int age = 0;
        boolean validInput = false;
        while (!validInput) {
            System.out.print("年齢を入力してください: ");
            try {
                age = scanner.nextInt();
                scanner.nextLine(); // 改行文字を消費
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("エラー: 整数を入力してください");
                scanner.nextLine(); // 不正な入力をクリア
            }
        }
        
        // 入力値の確認出力
        System.out.printf("%sさん（%d歳）、こんにちは！%n", name, age);
        
        // リソースの解放
        scanner.close();
    }
}
