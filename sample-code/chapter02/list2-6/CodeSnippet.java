/**
 * リスト2-6
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (371行目)
 */

// if文（C言語と同じ）
int score = 85;
if (score >= 90) {
    System.out.println("優");
} else if (score >= 80) {
    System.out.println("良");
} else if (score >= 70) {
    System.out.println("可");
} else {
    System.out.println("不可");
}

// switch文（Java 12以降の新記法も利用可能）
String grade = switch (score / 10) {
    case 10, 9 -> "優";
    case 8 -> "良";
    case 7 -> "可";
    default -> "不可";
};