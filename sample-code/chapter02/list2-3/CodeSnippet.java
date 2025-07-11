/**
 * リスト2-3
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (250行目)
 */

// 算術演算子
int a = 10, b = 3;
int sum = a + b;      // ① 13
int diff = a - b;     // ① 7
int prod = a * b;     // ① 30
int quot = a / b;     // ② 3
int rem = a % b;      // ③ 1

// インクリメント・デクリメント
int x = 5;
int y = x++;  // ④ y = 5, x = 6
int z = ++x;  // ⑤ z = 7, x = 7

// 比較演算子
boolean result1 = (a > b);   // ⑥ true
boolean result2 = (a == b);  // ⑥ false
boolean result3 = (a != b);  // ⑥ true

// 論理演算子
boolean p = true, q = false;
boolean and = p && q;        // ⑦ false
boolean or = p || q;         // ⑦ true
boolean not = !p;            // ⑦ false

// ビット演算子
int m = 5;    // 0101
int n = 3;    // 0011
int bitAnd = m & n;  // ⑧ 0001 = 1
int bitOr = m | n;   // ⑧ 0111 = 7
int bitXor = m ^ n;  // ⑧ 0110 = 6