/** インターフェイスに抽象メソッドを足すと、それを実装していない既存のクラスはコンパイルできなくなる */
public class MissingAbstract {
    interface Shape {
        double area();

        // あとから足したメソッド
        double perimeter();
    }

    static class Square implements Shape {
        @Override
        public double area() {
            return 1.0;
        }
    }
}
