/**
 * リスト3-34
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (1732行目)
 */

// 従来のswitch文
String dayType;
switch (day) {
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
        dayType = "平日";
        break;
    case 6:
    case 7:
        dayType = "休日";
        break;
    default:
        dayType = "無効な曜日";
}

// 新しいswitch式（Java 14以降）
int day = 3;
String dayType = switch (day) {
    case 1, 2, 3, 4, 5 -> "平日";
    case 6, 7 -> "休日";
    default -> "無効な曜日";
};