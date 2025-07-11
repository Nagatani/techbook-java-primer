/**
 * リスト2-4
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (306行目)
 */

// 暗黙的な型変換（拡大変換）
int i = 100;
long l = i;        // OK: int → long
double d = i;      // OK: int → double

// 明示的な型変換（縮小変換）
double pi = 3.14159;
int truncated = (int) pi;  // 3（小数部分は切り捨て）

// 文字列との変換
String str = "123";
int num = Integer.parseInt(str);  // 文字列→整数
String str2 = String.valueOf(num);  // 整数→文字列