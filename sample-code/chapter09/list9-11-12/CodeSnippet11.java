/**
 * リスト9-11
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (430行目)
 */

// 図形を表現する代数的データ型
public sealed interface Shape permits Circle, Rectangle, Triangle {}

public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
public record Triangle(double base, double height) implements Shape {}