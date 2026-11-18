/** 「インターフェイス名.super.メソッド名()」で呼べるのは、直接のスーパーインターフェイスだけ */
public class IndirectSuperCall {
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

    static class Impl implements Child {
        @Override
        public String who() {
            return Parent.super.who();  // Parentは直接のスーパーインターフェイスではない
        }
    }
}
