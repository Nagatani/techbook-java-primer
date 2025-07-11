/**
 * リスト2-8
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (426行目)
 */

String grade = switch (score / 10) {
    case 10, 9 -> "優";
    case 8 -> "良";
    case 7 -> "可";
    default -> "不可";
};