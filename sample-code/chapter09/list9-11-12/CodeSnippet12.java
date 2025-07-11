/**
 * リスト9-12
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (444行目)
 */

// 面積計算（パターンマッチング使用）
public static double calculateArea(Shape shape) {
    return switch (shape) {
        case Circle(var radius) -> Math.PI * radius * radius;
        case Rectangle(var width, var height) -> width * height;
        case Triangle(var base, var height) -> 0.5 * base * height;
    };
}

// 周囲計算
public static double calculatePerimeter(Shape shape) {
    return switch (shape) {
        case Circle(var radius) -> 2 * Math.PI * radius;
        case Rectangle(var width, var height) -> 2 * (width + height);
        case Triangle(var base, var height) -> {
            // 正三角形と仮定した場合の計算
            double side = Math.sqrt(Math.pow(base/2, 2) + Math.pow(height, 2));
            yield base + 2 * side;
        }
    };
}