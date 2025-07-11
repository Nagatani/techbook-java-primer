/**
 * リスト3-22
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (1281行目)
 */

// オートボクシング（Java 5以降）
Integer boxedInt = 100;  // int → Integer（自動変換）
Double boxedDouble = 3.14;  // double → Double（自動変換）

// アンボクシング
int primitiveInt = boxedInt;  // Integer → int（自動変換）
double primitiveDouble = boxedDouble;  // Double → double（自動変換）

// ラッパークラスの便利なメソッド
String numberStr = "123";
int parsedInt = Integer.parseInt(numberStr);  // 文字列から数値への変換
String binaryStr = Integer.toBinaryString(42); // "101010"