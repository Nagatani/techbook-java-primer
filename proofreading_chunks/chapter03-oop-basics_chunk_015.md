<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 15/15
行範囲: 2642 - 2746
作成日時: 2025-08-02 22:58:07

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

### Recordクラス（Java 14以降）

不変のデータクラスを簡潔に定義できる`record`は、多くのボイラープレートコードを削減します。

<span class="listing-number">**サンプルコード3-48**</span>

```java
// 従来の方法
import java.util.Objects;

public class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "Point{x=" + x + ", y=" + y + '}';
    }
}

// Record を使った方法（Java 14以降）
public record Point(int x, int y) {
    // コンストラクタ、getter、equals、hashCode、toStringが自動生成される
}
```

### Pattern Matching（Java 16以降）

`instanceof`演算子と組み合わせたパターンマッチングにより、型チェックとキャストを簡潔に記述できます。

<span class="listing-number">**サンプルコード3-49**</span>

```java
// 従来の方法
public void processShape(Shape shape) {
    if (shape instanceof Circle) {
        Circle circle = (Circle) shape;
        System.out.println("円の半径: " + circle.getRadius());
    } else if (shape instanceof Rectangle) {
        Rectangle rectangle = (Rectangle) shape;
        System.out.println("長方形の面積: " + rectangle.getArea());
    }
}

// Pattern Matching を使った方法（Java 16以降）
public void processShape(Shape shape) {
    if (shape instanceof Circle circle) {
        System.out.println("円の半径: " + circle.getRadius());
    } else if (shape instanceof Rectangle rectangle) {
        System.out.println("長方形の面積: " + rectangle.getArea());
    }
}
```

### Sealed Classes（Java 17以降）

クラス階層を制限し、より安全な継承を実現できます。

<span class="listing-number">**サンプルコード3-50**</span>

```java
public sealed class Shape 
    permits Circle, Rectangle, Triangle {
    // Shapeクラスを継承できるのは、Circle、Rectangle、Triangleのみ
}

public final class Circle extends Shape {
    private double radius;
    // 実装
}

public final class Rectangle extends Shape {
    private double width, height;
    // 実装
}

public final class Triangle extends Shape {
    private double base, height;
    // 実装
}
```

これらの新機能により、より安全で簡潔なオブジェクト指向プログラミングが可能になっています。
ただし、基本的な概念の理解が前提となるため、本章で学んだ基礎をしっかりと理解してから活用しましょう。


<!-- 
================
チャンク 15/15 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
