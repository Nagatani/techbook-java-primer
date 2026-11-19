// 第10章：ラムダ式の引数のvar（Java 11以降、JEP 323）
import java.util.function.BinaryOperator;

public class VarLambda {
    static BinaryOperator<Integer> add = (var a, var b) -> a + b;
}
