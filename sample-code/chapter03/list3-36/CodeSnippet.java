/**
 * リスト3-36
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (1785行目)
 */

Object obj = "Hello";
String formatted = switch (obj) {
    case Integer i -> String.format("Integer: %d", i);
    case String s -> String.format("String: %s", s);
    case null -> "It's null"; // nullもcaseとして扱える
    default -> "Unknown type";
};
System.out.println(formatted); // "String: Hello"