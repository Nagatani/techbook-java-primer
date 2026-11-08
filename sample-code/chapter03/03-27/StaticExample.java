// 使用例
public class StaticExample {
    public static void main(String[] args) {
        // newを使わずに直接呼び出し
        int sum = MathHelper.add(5, 3);
        System.out.println("合計: " + sum);
        
        double area = MathHelper.calculateCircleArea(10.0);
        System.out.println("円の面積: " + area);
    }
}
