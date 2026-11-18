/** インターフェイスのメソッドは暗黙にpublicなので、実装するメソッドにpublicを付け忘れるとコンパイルエラーになる */
public class WeakerAccess {
    interface Shape {
        double area();
    }

    static class Square implements Shape {
        double area() {
            return 1.0;
        }
    }
}
