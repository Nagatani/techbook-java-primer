/**
 * リスト2-21
 * コードスニペット
 * 
 * 元ファイル: chapter02-getting-started.md (784行目)
 */

StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.append(" ");
sb.append("World");
sb.insert(5, ",");  // "Hello, World"
sb.reverse();       // "dlroW ,olleH"

String result = sb.toString();