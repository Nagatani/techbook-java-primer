/**
 * リスト2-19
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (735行目)
 */

String text = "  Java Programming  ";

// 空白の除去
String trimmed = text.trim();  // "Java Programming"

// 大文字・小文字変換
String upper = text.toUpperCase();  // "  JAVA PROGRAMMING  "
String lower = text.toLowerCase();  // "  java programming  "

// 部分文字列の取得
String sub = text.substring(2, 6);  // "Java"

// 文字列の置換
String replaced = text.replace("Java", "Python");  // "  Python Programming  "

// 文字列の分割
String csv = "apple,banana,orange";
String[] fruits = csv.split(",");  // ["apple", "banana", "orange"]

// 文字列の検索
int index = text.indexOf("Pro");  // 7
boolean contains = text.contains("Java");  // true