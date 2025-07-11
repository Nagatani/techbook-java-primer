/**
 * リスト2-18
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (711行目)
 */

String s1 = "Java";
String s2 = "Java";
String s3 = new String("Java");

// == は参照の比較（推奨されない）
boolean ref1 = (s1 == s2);    // true（文字列プール）
boolean ref2 = (s1 == s3);    // false

// equals() は内容の比較（推奨）
boolean content1 = s1.equals(s2);  // true
boolean content2 = s1.equals(s3);  // true

// 大文字小文字を無視した比較
boolean ignore = s1.equalsIgnoreCase("java");  // true