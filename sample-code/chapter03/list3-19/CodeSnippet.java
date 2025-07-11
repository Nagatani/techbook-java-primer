/**
 * リスト3-19
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (1231行目)
 */

char singleChar = 'A';          // 文字リテラル（シングルクォート）
char unicodeChar = '\u0041';    // Unicode表現（'A'と同じ）
String text = "Hello World";    // 文字列リテラル（ダブルクォート）

// エスケープシーケンス
String escaped = "Line 1\nLine 2\t\"Quoted\"";
// \n: 改行、\t: タブ、\": ダブルクォート