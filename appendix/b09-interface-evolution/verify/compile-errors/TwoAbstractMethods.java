/** @FunctionalInterfaceを付けたインターフェイスに抽象メソッドが2つあると、コンパイルエラーになる */
@FunctionalInterface
public interface TwoAbstractMethods {
    int apply(int x);

    int applyTwice(int x);
}
