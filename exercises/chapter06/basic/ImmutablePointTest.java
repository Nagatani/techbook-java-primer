/**
 * 第6章 基本課題1: ImmutablePointTest クラス
 * 
 * ImmutablePointクラスをテストするためのクラスです。
 * 不変性と座標操作を確認してください。
 * 
 * 実行例:
 * === 不変座標クラステスト ===
 * 座標1: (3.0, 4.0)
 * 座標2: (0.0, 0.0)
 *
 * 座標1から座標2への距離: 5.0
 *
 * 座標1を(2.0, 3.0)移動:
 * 元の座標1: (3.0, 4.0)
 * 新しい座標: (5.0, 7.0)
 *
 * 座標1を90度回転:
 * 元の座標1: (3.0, 4.0)
 * 回転後の座標: (-4.0, 3.0)
 *
 * 座標1の原点からの距離: 5.0
 *
 * 不変性テスト:
 * 取得した値: x=3.0, y=4.0
 * 値変更後のpoint1: (3.0, 4.0)
 *
 * 同じ座標値のオブジェクト比較:
 * point1.equals(point3): true
 * point1 == point3: false
 */
public class ImmutablePointTest {
    public static void main(String[] args) {
        IO.println("=== 不変座標クラステスト ===");
        
        // TODO: ImmutablePointオブジェクトを作成してください
        // ImmutablePoint point1 = new ImmutablePoint(3.0, 4.0);
        // ImmutablePoint point2 = new ImmutablePoint(0.0, 0.0);
        // 
        // IO.println("座標1: " + point1);
        // IO.println("座標2: " + point2);
        // IO.println();
        
        // TODO: 距離計算をテストしてください
        // double distance = point1.distanceTo(point2);
        // IO.println("座標1から座標2への距離: " + distance);
        // IO.println();
        
        // TODO: 移動操作をテストしてください（元の座標が変更されないことを確認）
        // IO.println("座標1を(2.0, 3.0)移動:");
        // ImmutablePoint movedPoint = point1.move(2.0, 3.0);
        // IO.println("元の座標1: " + point1);
        // IO.println("新しい座標: " + movedPoint);
        // IO.println();
        
        // TODO: 回転操作をテストしてください
        // IO.println("座標1を90度回転:");
        // ImmutablePoint rotatedPoint = point1.rotate(90);
        // IO.println("元の座標1: " + point1);
        // IO.println("回転後の座標: " + rotatedPoint);
        // IO.println();
        
        // TODO: 原点からの距離をテストしてください
        // double distanceFromOrigin = point1.distanceFromOrigin();
        // IO.println("座標1の原点からの距離: " + distanceFromOrigin);
        // IO.println();
        
        // TODO: 不変性の確認（getter で取得した値を変更しても元のオブジェクトに影響しないことを確認）
        // IO.println("不変性テスト:");
        // double x = point1.getX();
        // double y = point1.getY();
        // IO.println("取得した値: x=" + x + ", y=" + y);
        // // これらの値を変更しても point1 には影響しない
        // x = 100;
        // y = 200;
        // IO.println("値変更後のpoint1: " + point1);
        // IO.println();
        
        // TODO: equalsメソッドをテストしてください
        // ImmutablePoint point3 = new ImmutablePoint(3.0, 4.0);
        // IO.println("同じ座標値のオブジェクト比較:");
        // IO.println("point1.equals(point3): " + point1.equals(point3));
        // IO.println("point1 == point3: " + (point1 == point3));
    }
}