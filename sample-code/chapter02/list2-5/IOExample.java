/**
 * リスト2-5
 * IOExampleクラス
 * 
 * 元ファイル: chapter02-getting-started.md (327行目)
 */

import java.util.Scanner;

public class IOExample {
    public static void main(String[] args) {
        // 出力
        System.out.println("Hello, World!");
        System.out.print("改行なし");
        System.out.printf("書式付き出力: %d, %.2f%n", 10, 3.14159);
        
        // 入力
        Scanner scanner = new Scanner(System.in);
        System.out.print("名前を入力: ");
        String name = scanner.nextLine();
        System.out.print("年齢を入力: ");
        int age = scanner.nextInt();
        
        System.out.printf("%sさん（%d歳）、こんにちは！%n", name, age);
        scanner.close();
    }
}