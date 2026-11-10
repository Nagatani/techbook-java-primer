interface MathUtils {
    static double PI = 3.14159;
    
    static double calculateCircleArea(double radius) {
        return PI * radius * radius;
    }
    
    default void printInfo() {
        // インターフェイス名を明示すると、staticメソッドであることがわかりやすい
        IO.println("面積: " + MathUtils.calculateCircleArea(5.0));
    }
}

class Calculator implements MathUtils {
    public void doCalculation() {
        // インターフェイス名を明示して呼び出し
        double area = MathUtils.calculateCircleArea(3.0);
    }
}
