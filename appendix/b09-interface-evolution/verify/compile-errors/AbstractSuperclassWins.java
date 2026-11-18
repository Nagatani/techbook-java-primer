/** スーパークラスの抽象メソッドも「クラスが優先」の対象で、インターフェイスのdefaultメソッドでは実装されない */
public class AbstractSuperclassWins {
    abstract static class Base {
        public abstract String who();
    }

    interface Named {
        default String who() {
            return "Named";
        }
    }

    static class Sub extends Base implements Named {
    }
}
