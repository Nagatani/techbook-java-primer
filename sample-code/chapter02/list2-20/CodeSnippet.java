/**
 * リスト2-20
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (767行目)
 */

String str = "Hello";
str.concat(" World");  // str自体は変更されない
System.out.println(str);  // "Hello"

// 新しい文字列を作成して代入する必要がある
str = str.concat(" World");
System.out.println(str);  // "Hello World"