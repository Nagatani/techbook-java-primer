import java.util.InputMismatchException;
import java.util.Scanner;

public class SafeInputExample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("整数を入力してください: ");
            try {
                int number = scanner.nextInt();
                System.out.println("入力された数値: " + number);
                break;
            } catch (InputMismatchException e) {
                System.out.println("エラー: 整数を入力してください");
                scanner.nextLine(); // 不正な入力をクリア
            }
        }
        
        scanner.close();
    }
}
