/**
 * リスト3-35
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (1769行目)
 */

// yieldを使った例
String message = switch (day) {
    case 1, 2, 3, 4, 5 -> {
        System.out.println("今日は頑張る日！");
        yield "平日"; // ブロックから値を返す
    }
    case 6, 7 -> "休日";
    default -> "無効な曜日";
};