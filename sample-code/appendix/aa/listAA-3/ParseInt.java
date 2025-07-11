/**
 * リストAA-3
 * ParseIntクラス
 * 
 * 元ファイル: appendix-a-environment-setup.md (514行目)
 */

import java.util.Scanner;  // このライブラリをParseIntクラスで使う宣言

public class ParseInt {
    public static void main(String[] args) {
        // 標準入力をScannerで取得する
        Scanner in = new Scanner(System.in);
        // nextLine()メソッドは、キーボードからReturnキーの入力があるまで待ち、入力された1行を返す
        String inputLine = in.nextLine();
        // ↑inputLineという変数には、入力された文字列データが格納されます

        // 変数numに文字列データを整数値に変換して格納
        int num = Integer.parseInt(inputLine);
        // 整数変換された文字列データに10を足した結果を変数ansに格納
        int ans = num + 10;

        // 変数ansを出力
        System.out.println(ans);
    }
}