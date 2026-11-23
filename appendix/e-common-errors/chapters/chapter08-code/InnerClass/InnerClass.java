public class InnerClass {
    class Counter {
        int count;
    }

    // 外側のインスタンスを必要としないなら、staticを付けた静的ネストクラスにする
    static class Totalizer {
        int total;
    }

    public static void main(String[] args) {
        // staticでない内部クラスは、外側のインスタンスから作る
        InnerClass outer = new InnerClass();
        InnerClass.Counter counter = outer.new Counter();
        counter.count++;
        IO.println("count = " + counter.count);

        Totalizer totalizer = new Totalizer();
        totalizer.total += 10;
        IO.println("total = " + totalizer.total);
    }
}
