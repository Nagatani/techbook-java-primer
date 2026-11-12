package chapter07.solutions;

/**
 * 図形の抽象クラス（課題1の解答例）
 *
 * 面積と周囲の長さの求め方は図形ごとに違うので、抽象メソッドにしてサブクラスに実装させる。
 * 情報の表示は全図形で同じなので、このクラスに書く。
 */
public abstract class Shape {
    protected String name;

    public Shape(String name) {
        this.name = name;
    }

    public abstract double calculateArea();

    public abstract double calculatePerimeter();

    // 抽象メソッドを呼んでいるが、実行時には実際のオブジェクト（CircleやRectangle）の実装が使われる
    public void displayInfo() {
        IO.println("図形: " + name);
        IO.println("面積: " + "%.2f".formatted(calculateArea()));
        IO.println("周囲の長さ: " + "%.2f".formatted(calculatePerimeter()));
    }

    public String getName() {
        return name;
    }
}
