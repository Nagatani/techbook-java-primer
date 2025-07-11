/**
 * リスト2-17
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (689行目)
 */

// 文字列の作成
String str1 = "Hello";
String str2 = new String("World");

// 文字列の連結
String message = str1 + " " + str2;  // "Hello World"

// 文字列の長さ
int length = message.length();  // 11

// 文字の取得
char firstChar = message.charAt(0);  // 'H'