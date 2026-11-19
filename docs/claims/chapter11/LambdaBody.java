// 第11章：ラムダ式の本体は同じクラスのprivateメソッドになり、invokedynamicでオブジェクトを作る
public class LambdaBody {
    static Runnable make() {
        return () -> System.out.println("run");
    }
}
