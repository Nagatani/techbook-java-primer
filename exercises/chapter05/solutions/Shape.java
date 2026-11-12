/**
 * 図形の基底クラス（課題2の解答例）
 *
 * 面積の求め方は図形ごとに違うので、getAreaはサブクラスでオーバーライドする。
 */
public class Shape {
    protected String name;
    protected String color;

    public Shape(String name, String color) {
        this.name = name;
        this.color = color;
    }

    // Shapeそのものには形がないので0を返す。サブクラスが正しい面積を返すようにオーバーライドする
    // 第7章で学ぶ抽象クラスにすると、オーバーライドを強制できる:
    // public abstract class Shape { ... public abstract double getArea(); }
    public double getArea() {
        return 0.0;
    }

    public void showInfo() {
        IO.println(name);
        IO.println("色: " + color);
        // getAreaは、実際のオブジェクト（RectangleやCircle）のメソッドが呼ばれる
        IO.println("面積: " + "%.2f".formatted(getArea()));
    }
}
