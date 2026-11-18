/** defaultメソッドでObjectのpublicメソッド（equals・hashCode・toString）をオーバーライドすることはできない */
public interface DefaultObjectMethod {
    String name();

    default String toString() {
        return "Named(" + name() + ")";
    }
}
