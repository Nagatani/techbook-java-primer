/**
 * リスト9-17
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (623行目)
 */

// 新しい図形を追加した場合
public sealed interface Shape permits Circle, Rectangle, Triangle, Square {}
public record Square(double side) implements Shape {}

// コンパイルエラー：Squareケースが処理されていない
public static double calculateArea(Shape shape) {
    return switch (shape) {
        case Circle(var radius) -> Math.PI * radius * radius;
        case Rectangle(var width, var height) -> width * height;
        case Triangle(var base, var height) -> 0.5 * base * height;
        // case Square(var side) -> side * side;  // これが必要
    };
}