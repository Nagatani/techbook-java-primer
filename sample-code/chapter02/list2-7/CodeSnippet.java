/**
 * リスト2-7
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (405行目)
 */

String grade;
switch (score / 10) {
    case 10:
    case 9:
        grade = "優";
        break;
    case 8:
        grade = "良";
        break;
    case 7:
        grade = "可";
        break;
    default:
        grade = "不可";
        break;
}