import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Shape継承階層のテストクラス
 * 
 * 【テストの観点】
 * 1. 抽象クラスの継承が正しく機能するか
 * 2. 抽象メソッドが適切に実装されているか
 * 3. テンプレートメソッドパターンが機能するか
 * 4. 図形固有の計算が正確か
 * 5. 継承関係でのポリモーフィズム
 */
public class ShapeTest {
    
    @Nested
    @DisplayName("Circleクラスのテスト")
    class CircleTest {
        
        private Circle circle;
        
        @BeforeEach
        void setUp() {
            circle = new Circle("TestCircle", "Red", 0, 0, 5);
        }
        
        @Test
        @DisplayName("円の基本プロパティ")
        void testCircleBasicProperties() {
            assertEquals("TestCircle", circle.getName());
            assertEquals("Red", circle.getColor());
            assertEquals(0, circle.getX());
            assertEquals(0, circle.getY());
            assertEquals(5, circle.getRadius());
            assertEquals(10, circle.getDiameter());
        }
        
        @Test
        @DisplayName("円の面積計算")
        void testCircleArea() {
            double expectedArea = Math.PI * 5 * 5;
            assertEquals(expectedArea, circle.getArea(), 0.001);
            
            // 単位円のテスト
            Circle unitCircle = new Circle(0, 0, 1);
            assertEquals(Math.PI, unitCircle.getArea(), 0.001);
        }
        
        @Test
        @DisplayName("円の周囲計算")
        void testCirclePerimeter() {
            double expectedPerimeter = 2 * Math.PI * 5;
            assertEquals(expectedPerimeter, circle.getPerimeter(), 0.001);
            
            // 単位円のテスト
            Circle unitCircle = new Circle(0, 0, 1);
            assertEquals(2 * Math.PI, unitCircle.getPerimeter(), 0.001);
        }
        
        @Test
        @DisplayName("円の点包含判定")
        void testCircleContains() {
            // 中心点
            assertTrue(circle.contains(0, 0));
            
            // 円周上の点
            assertTrue(circle.contains(5, 0));
            assertTrue(circle.contains(0, 5));
            assertTrue(circle.contains(-5, 0));
            assertTrue(circle.contains(0, -5));
            
            // 円内の点
            assertTrue(circle.contains(3, 4));  // 3² + 4² = 25 = 5²
            
            // 円外の点
            assertFalse(circle.contains(6, 0));
            assertFalse(circle.contains(4, 4));  // 4² + 4² = 32 > 25
        }
        
        @Test
        @DisplayName("円のスケーリング")
        void testCircleScale() {
            Shape scaledCircle = circle.scale(2.0);
            
            assertTrue(scaledCircle instanceof Circle);
            Circle scaled = (Circle) scaledCircle;
            
            assertEquals(10, scaled.getRadius());
            assertEquals(circle.getArea() * 4, scaled.getArea(), 0.001);  // 面積は4倍
        }
        
        @Test
        @DisplayName("円のコピー")
        void testCircleCopy() {
            Shape copy = circle.copy();
            
            assertTrue(copy instanceof Circle);
            assertNotSame(circle, copy);
            assertEquals(circle.toString(), copy.toString());
        }
        
        @Test
        @DisplayName("円の移動")
        void testCircleMove() {
            Shape moved = circle.move(10, 20);
            
            assertTrue(moved instanceof Circle);
            Circle movedCircle = (Circle) moved;
            
            assertEquals(10, movedCircle.getX());
            assertEquals(20, movedCircle.getY());
            assertEquals(circle.getRadius(), movedCircle.getRadius());
        }
        
        @Test
        @DisplayName("円のバウンディングボックス")
        void testCircleBoundingBox() {
            Shape.Rectangle bbox = circle.getBoundingBox();
            
            assertEquals(-5, bbox.getX());
            assertEquals(-5, bbox.getY());
            assertEquals(10, bbox.getWidth());
            assertEquals(10, bbox.getHeight());
        }
        
        @Test
        @DisplayName("円の中心点")
        void testCircleCenter() {
            Shape.Point center = circle.getCenter();
            assertEquals(0, center.getX());
            assertEquals(0, center.getY());
        }
        
        @Test
        @DisplayName("円の固有メソッド")
        void testCircleSpecificMethods() {
            // 内接正六角形の面積
            double hexagonArea = circle.getInscribedPolygonArea(6);
            assertTrue(hexagonArea > 0);
            assertTrue(hexagonArea < circle.getArea());
            
            // 外接正六角形の面積
            double circumHexagonArea = circle.getCircumscribedPolygonArea(6);
            assertTrue(circumHexagonArea > circle.getArea());
            
            // 円弧の長さ
            double arcLength = circle.getArcLength(Math.PI);  // 半円
            assertEquals(circle.getPerimeter() / 2, arcLength, 0.001);
            
            // 扇形の面積
            double sectorArea = circle.getSectorArea(Math.PI);  // 半円
            assertEquals(circle.getArea() / 2, sectorArea, 0.001);
        }
        
        @Test
        @DisplayName("円の交差判定")
        void testCircleIntersection() {
            Circle other1 = new Circle(8, 0, 5);  // 外接
            assertTrue(circle.intersects(other1));
            
            Circle other2 = new Circle(12, 0, 5);  // 離れている
            assertFalse(circle.intersects(other2));
            
            Circle other3 = new Circle(0, 0, 2);   // 内包
            assertTrue(circle.intersects(other3));
            assertTrue(circle.contains(other3));
        }
        
        @Test
        @DisplayName("無効な半径でエラー")
        void testInvalidRadius() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Circle(0, 0, 0);
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                new Circle(0, 0, -1);
            });
        }
        
        @Test
        @DisplayName("無効なスケーリングでエラー")
        void testInvalidScale() {
            assertThrows(IllegalArgumentException.class, () -> {
                circle.scale(0);
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                circle.scale(-1);
            });
        }
    }
    
    @Nested
    @DisplayName("Rectangleクラスのテスト")
    class RectangleTest {
        
        private Rectangle rectangle;
        
        @BeforeEach
        void setUp() {
            rectangle = new Rectangle("TestRectangle", "Blue", 0, 0, 4, 3);
        }
        
        @Test
        @DisplayName("矩形の基本プロパティ")
        void testRectangleBasicProperties() {
            assertEquals("TestRectangle", rectangle.getName());
            assertEquals("Blue", rectangle.getColor());
            assertEquals(0, rectangle.getX());
            assertEquals(0, rectangle.getY());
            assertEquals(4, rectangle.getWidth());
            assertEquals(3, rectangle.getHeight());
            assertFalse(rectangle.isSquare());
        }
        
        @Test
        @DisplayName("正方形の判定")
        void testSquareDetection() {
            Rectangle square = new Rectangle("Square", "Green", 0, 0, 5, 5);
            assertTrue(square.isSquare());
            
            Rectangle nonSquare = new Rectangle("Rectangle", "Red", 0, 0, 4, 3);
            assertFalse(nonSquare.isSquare());
        }
        
        @Test
        @DisplayName("矩形の面積計算")
        void testRectangleArea() {
            assertEquals(12, rectangle.getArea());  // 4 × 3 = 12
            
            Rectangle square = new Rectangle(0, 0, 5, 5);
            assertEquals(25, square.getArea());  // 5 × 5 = 25
        }
        
        @Test
        @DisplayName("矩形の周囲計算")
        void testRectanglePerimeter() {
            assertEquals(14, rectangle.getPerimeter());  // 2 × (4 + 3) = 14
            
            Rectangle square = new Rectangle(0, 0, 5, 5);
            assertEquals(20, square.getPerimeter());  // 2 × (5 + 5) = 20
        }
        
        @Test
        @DisplayName("矩形の対角線計算")
        void testRectangleDiagonal() {
            assertEquals(5, rectangle.getDiagonal(), 0.001);  // √(4² + 3²) = 5
            
            Rectangle square = new Rectangle(0, 0, 3, 3);
            assertEquals(3 * Math.sqrt(2), square.getDiagonal(), 0.001);
        }
        
        @Test
        @DisplayName("矩形の点包含判定")
        void testRectangleContains() {
            // 左上角
            assertTrue(rectangle.contains(0, 0));
            
            // 右下角
            assertTrue(rectangle.contains(4, 3));
            
            // 中心付近
            assertTrue(rectangle.contains(2, 1.5));
            
            // 外部の点
            assertFalse(rectangle.contains(-1, 0));
            assertFalse(rectangle.contains(5, 0));
            assertFalse(rectangle.contains(0, -1));
            assertFalse(rectangle.contains(0, 4));
        }
        
        @Test
        @DisplayName("矩形の頂点取得")
        void testRectangleVertices() {
            Shape.Point[] vertices = rectangle.getVertices();
            assertEquals(4, vertices.length);
            
            // 左上
            assertEquals(0, vertices[0].getX());
            assertEquals(0, vertices[0].getY());
            
            // 右上
            assertEquals(4, vertices[1].getX());
            assertEquals(0, vertices[1].getY());
            
            // 右下
            assertEquals(4, vertices[2].getX());
            assertEquals(3, vertices[2].getY());
            
            // 左下
            assertEquals(0, vertices[3].getX());
            assertEquals(3, vertices[3].getY());
        }
        
        @Test
        @DisplayName("矩形の中心点")
        void testRectangleCenter() {
            Shape.Point center = rectangle.getCenter();
            assertEquals(2, center.getX());  // 4 / 2 = 2
            assertEquals(1.5, center.getY());  // 3 / 2 = 1.5
        }
        
        @Test
        @DisplayName("矩形の縦横比")
        void testRectangleAspectRatio() {
            assertEquals(4.0 / 3.0, rectangle.getAspectRatio(), 0.001);
            
            Rectangle square = new Rectangle(0, 0, 5, 5);
            assertEquals(1.0, square.getAspectRatio(), 0.001);
        }
        
        @Test
        @DisplayName("矩形の交差判定")
        void testRectangleIntersection() {
            Rectangle other1 = new Rectangle(2, 1, 4, 3);  // 交差する
            assertTrue(rectangle.intersects(other1));
            
            Rectangle other2 = new Rectangle(5, 0, 2, 2);  // 離れている
            assertFalse(rectangle.intersects(other2));
            
            Rectangle other3 = new Rectangle(1, 1, 2, 1);  // 内包される
            assertTrue(rectangle.intersects(other3));
            assertTrue(rectangle.contains(other3));
        }
        
        @Test
        @DisplayName("矩形の交差領域")
        void testRectangleIntersectionArea() {
            Rectangle other = new Rectangle(2, 1, 4, 3);
            Rectangle intersection = rectangle.getIntersection(other);
            
            assertNotNull(intersection);
            assertEquals(2, intersection.getX());
            assertEquals(1, intersection.getY());
            assertEquals(2, intersection.getWidth());  // min(4, 6) - 2 = 2
            assertEquals(2, intersection.getHeight()); // min(3, 4) - 1 = 2
        }
        
        @Test
        @DisplayName("矩形の結合領域")
        void testRectangleUnion() {
            Rectangle other = new Rectangle(2, 1, 4, 3);
            Rectangle union = rectangle.getUnion(other);
            
            assertNotNull(union);
            assertEquals(0, union.getX());
            assertEquals(0, union.getY());
            assertEquals(6, union.getWidth());  // max(4, 6) - 0 = 6
            assertEquals(4, union.getHeight()); // max(3, 4) - 0 = 4
        }
        
        @Test
        @DisplayName("矩形の回転")
        void testRectangleRotation() {
            Rectangle rotated90 = rectangle.rotate90(1);
            assertEquals(3, rotated90.getWidth());   // 元の高さ
            assertEquals(4, rotated90.getHeight());  // 元の幅
            
            Rectangle rotated180 = rectangle.rotate90(2);
            assertEquals(4, rotated180.getWidth());
            assertEquals(3, rotated180.getHeight());
        }
        
        @Test
        @DisplayName("矩形の拡張・収縮")
        void testRectangleExpandShrink() {
            Rectangle expanded = rectangle.expand(1);
            assertEquals(-1, expanded.getX());
            assertEquals(-1, expanded.getY());
            assertEquals(6, expanded.getWidth());  // 4 + 2
            assertEquals(5, expanded.getHeight()); // 3 + 2
            
            Rectangle shrunken = rectangle.shrink(0.5);
            assertEquals(0.5, shrunken.getX());
            assertEquals(0.5, shrunken.getY());
            assertEquals(3, shrunken.getWidth());  // 4 - 1
            assertEquals(2, shrunken.getHeight()); // 3 - 1
        }
        
        @Test
        @DisplayName("無効な寸法でエラー")
        void testInvalidDimensions() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Rectangle(0, 0, 0, 5);
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                new Rectangle(0, 0, 5, -1);
            });
        }
        
        @Test
        @DisplayName("過度な収縮でエラー")
        void testExcessiveShrink() {
            assertThrows(IllegalArgumentException.class, () -> {
                rectangle.shrink(3);  // 幅4から6引くと負になる
            });
        }
    }
    
    @Nested
    @DisplayName("Shape抽象クラスのテスト")
    class ShapeAbstractTest {
        
        private Shape circle;
        private Shape rectangle;
        
        @BeforeEach
        void setUp() {
            circle = new Circle("TestCircle", "Red", 0, 0, 5);
            rectangle = new Rectangle("TestRectangle", "Blue", 0, 0, 4, 3);
        }
        
        @Test
        @DisplayName("ポリモーフィズムのテスト")
        void testPolymorphism() {
            Shape[] shapes = {circle, rectangle};
            
            // すべての図形に対して同じメソッドを呼び出せる
            for (Shape shape : shapes) {
                assertTrue(shape.getArea() > 0);
                assertTrue(shape.getPerimeter() > 0);
                assertNotNull(shape.getName());
                assertNotNull(shape.getColor());
            }
        }
        
        @Test
        @DisplayName("面積の比較")
        void testAreaComparison() {
            // 円の面積: π × 5² ≈ 78.54
            // 矩形の面積: 4 × 3 = 12
            assertTrue(circle.compareAreaWith(rectangle) > 0);
            assertTrue(rectangle.compareAreaWith(circle) < 0);
            
            Shape circle2 = new Circle(0, 0, Math.sqrt(12 / Math.PI));
            assertEquals(0, circle2.compareAreaWith(rectangle), 0.001);
        }
        
        @Test
        @DisplayName("面積判定")
        void testAreaPredicate() {
            assertTrue(circle.isLargerThan(50));
            assertFalse(circle.isLargerThan(100));
            
            assertTrue(rectangle.isLargerThan(10));
            assertFalse(rectangle.isLargerThan(15));
        }
        
        @Test
        @DisplayName("図形情報の取得")
        void testShapeInfo() {
            String circleInfo = circle.getInfo();
            assertTrue(circleInfo.contains("TestCircle"));
            assertTrue(circleInfo.contains("Red"));
            assertTrue(circleInfo.contains("(0.0, 0.0)"));
            
            String rectangleInfo = rectangle.getInfo();
            assertTrue(rectangleInfo.contains("TestRectangle"));
            assertTrue(rectangleInfo.contains("Blue"));
        }
        
        @Test
        @DisplayName("詳細説明の生成")
        void testDetailedDescription() {
            String description = circle.getDetailedDescription();
            assertTrue(description.contains("Circle"));
            assertTrue(description.contains("TestCircle"));
            assertTrue(description.contains("Red"));
            assertTrue(description.contains("面積"));
            assertTrue(description.contains("周囲"));
        }
        
        @Test
        @DisplayName("描画処理（テンプレートメソッド）")
        void testRenderTemplate() {
            // 例外が発生しないことを確認
            assertDoesNotThrow(() -> circle.render());
            assertDoesNotThrow(() -> rectangle.render());
        }
        
        @Test
        @DisplayName("情報表示（テンプレートメソッド）")
        void testDisplayInfoTemplate() {
            // 例外が発生しないことを確認
            assertDoesNotThrow(() -> circle.displayInfo());
            assertDoesNotThrow(() -> rectangle.displayInfo());
        }
        
        @Test
        @DisplayName("等価性の判定")
        void testEquality() {
            Shape circle1 = new Circle("Circle", "Red", 0, 0, 5);
            Shape circle2 = new Circle("Circle", "Red", 0, 0, 5);
            Shape circle3 = new Circle("Circle", "Red", 0, 0, 3);
            
            assertEquals(circle1, circle2);
            assertNotEquals(circle1, circle3);
            assertNotEquals(circle1, rectangle);
        }
        
        @Test
        @DisplayName("ハッシュコードの整合性")
        void testHashCode() {
            Shape circle1 = new Circle("Circle", "Red", 0, 0, 5);
            Shape circle2 = new Circle("Circle", "Red", 0, 0, 5);
            
            assertEquals(circle1.hashCode(), circle2.hashCode());
        }
        
        @Test
        @DisplayName("toString の実装")
        void testToString() {
            String circleStr = circle.toString();
            assertTrue(circleStr.contains("Circle"));
            assertTrue(circleStr.contains("Red"));
            
            String rectangleStr = rectangle.toString();
            assertTrue(rectangleStr.contains("Rectangle"));
            assertTrue(rectangleStr.contains("Blue"));
        }
    }
    
    @Nested
    @DisplayName("静的ユーティリティメソッドのテスト")
    class StaticUtilityTest {
        
        @Test
        @DisplayName("面積順ソート")
        void testSortByArea() {
            Shape[] shapes = {
                new Circle("Large", "Red", 0, 0, 10),      // 面積: π × 100 ≈ 314
                new Rectangle("Small", "Blue", 0, 0, 2, 3), // 面積: 6
                new Circle("Medium", "Green", 0, 0, 5)      // 面積: π × 25 ≈ 78
            };
            
            Shape[] sorted = Shape.sortByArea(shapes);
            
            assertTrue(sorted[0].getArea() <= sorted[1].getArea());
            assertTrue(sorted[1].getArea() <= sorted[2].getArea());
        }
        
        @Test
        @DisplayName("最大面積の図形を取得")
        void testFindLargestByArea() {
            Shape[] shapes = {
                new Circle("Small", "Red", 0, 0, 2),
                new Rectangle("Large", "Blue", 0, 0, 10, 10),
                new Circle("Medium", "Green", 0, 0, 5)
            };
            
            Shape largest = Shape.findLargestByArea(shapes);
            assertEquals("Large", largest.getName());
        }
        
        @Test
        @DisplayName("総面積の計算")
        void testCalculateTotalArea() {
            Shape[] shapes = {
                new Rectangle("Rect1", "Red", 0, 0, 2, 3),    // 面積: 6
                new Rectangle("Rect2", "Blue", 0, 0, 4, 5)    // 面積: 20
            };
            
            double totalArea = Shape.calculateTotalArea(shapes);
            assertEquals(26, totalArea, 0.001);
        }
        
        @Test
        @DisplayName("図形タイプの判定")
        void testGetShapeType() {
            Shape circle = new Circle(0, 0, 5);
            Shape rectangle = new Rectangle(0, 0, 4, 3);
            
            assertEquals("Circle", Shape.getShapeType(circle));
            assertEquals("Rectangle", Shape.getShapeType(rectangle));
        }
        
        @Test
        @DisplayName("空配列での処理")
        void testEmptyArray() {
            assertNull(Shape.findLargestByArea(new Shape[0]));
            assertEquals(0, Shape.calculateTotalArea(new Shape[0]));
            assertEquals(0, Shape.calculateTotalArea(null));
        }
    }
    
    @Nested
    @DisplayName("Circle固有の静的メソッドテスト")
    class CircleStaticTest {
        
        @Test
        @DisplayName("円の交点計算")
        void testCircleIntersectionPoints() {
            Circle circle1 = new Circle(0, 0, 5);
            Circle circle2 = new Circle(8, 0, 5);  // 外接
            
            Shape.Point[] points = Circle.getIntersectionPoints(circle1, circle2);
            assertEquals(1, points.length);  // 外接なので1点
            assertEquals(4, points[0].getX(), 0.001);
            assertEquals(0, points[0].getY(), 0.001);
        }
        
        @Test
        @DisplayName("最小包含円の作成")
        void testMinimumEnclosingCircle() {
            Shape.Point[] points = {
                new Shape.Point(0, 0),
                new Shape.Point(3, 0),
                new Shape.Point(0, 4)
            };
            
            Circle enclosingCircle = Circle.getMinimumEnclosingCircle(points);
            assertNotNull(enclosingCircle);
            
            // すべての点が円内に含まれることを確認
            for (Shape.Point point : points) {
                assertTrue(enclosingCircle.contains(point.getX(), point.getY()));
            }
        }
        
        @Test
        @DisplayName("単位円の作成")
        void testCreateUnitCircle() {
            Circle unitCircle = Circle.createUnitCircle(10, 20);
            assertEquals(10, unitCircle.getX());
            assertEquals(20, unitCircle.getY());
            assertEquals(1.0, unitCircle.getRadius());
            assertEquals(Math.PI, unitCircle.getArea(), 0.001);
        }
    }
    
    @Nested
    @DisplayName("Rectangle固有の静的メソッドテスト")
    class RectangleStaticTest {
        
        @Test
        @DisplayName("正方形の作成")
        void testCreateSquare() {
            Rectangle square = Rectangle.createSquare(5, 10, 7);
            assertEquals(5, square.getX());
            assertEquals(10, square.getY());
            assertEquals(7, square.getWidth());
            assertEquals(7, square.getHeight());
            assertTrue(square.isSquare());
        }
        
        @Test
        @DisplayName("単位正方形の作成")
        void testCreateUnitSquare() {
            Rectangle unitSquare = Rectangle.createUnitSquare(0, 0);
            assertEquals(1.0, unitSquare.getWidth());
            assertEquals(1.0, unitSquare.getHeight());
            assertEquals(1.0, unitSquare.getArea());
            assertTrue(unitSquare.isSquare());
        }
        
        @Test
        @DisplayName("黄金比矩形の作成")
        void testCreateGoldenRectangle() {
            Rectangle goldenRect = Rectangle.createGoldenRectangle(0, 0, 10);
            double goldenRatio = (1 + Math.sqrt(5)) / 2;
            assertEquals(10, goldenRect.getWidth());
            assertEquals(10 / goldenRatio, goldenRect.getHeight(), 0.001);
            assertEquals(goldenRatio, goldenRect.getAspectRatio(), 0.001);
        }
        
        @Test
        @DisplayName("バウンディング矩形の取得")
        void testGetBoundingRectangle() {
            Rectangle[] rectangles = {
                new Rectangle(1, 2, 3, 4),
                new Rectangle(5, 1, 2, 3),
                new Rectangle(2, 5, 4, 2)
            };
            
            Rectangle bounding = Rectangle.getBoundingRectangle(rectangles);
            assertEquals(1, bounding.getX());  // min(1, 5, 2)
            assertEquals(1, bounding.getY());  // min(2, 1, 5)
            assertEquals(6, bounding.getWidth());  // max(4, 7, 6) - 1
            assertEquals(6, bounding.getHeight()); // max(6, 4, 7) - 1
        }
        
        @Test
        @DisplayName("点からの最小バウンディング矩形")
        void testGetMinimumBoundingRectangle() {
            Shape.Point[] points = {
                new Shape.Point(1, 2),
                new Shape.Point(5, 1),
                new Shape.Point(3, 6),
                new Shape.Point(2, 3)
            };
            
            Rectangle bounding = Rectangle.getMinimumBoundingRectangle(points);
            assertEquals(1, bounding.getX());  // min X
            assertEquals(1, bounding.getY());  // min Y
            assertEquals(4, bounding.getWidth());  // max X - min X
            assertEquals(5, bounding.getHeight()); // max Y - min Y
        }
    }
}