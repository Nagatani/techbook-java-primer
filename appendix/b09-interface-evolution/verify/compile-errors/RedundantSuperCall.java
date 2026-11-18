/** 直接書き並べたスーパーインターフェイスでも、ほかのインターフェイスがオーバーライドしたメソッドは呼べない */
public class RedundantSuperCall {
    interface Parent {
        default String who() {
            return "Parent";
        }
    }

    interface Child extends Parent {
        @Override
        default String who() {
            return "Child";
        }
    }

    static class Impl implements Parent, Child {
        @Override
        public String who() {
            return Parent.super.who();  // ChildがParentのwho()をオーバーライドしている
        }
    }
}
