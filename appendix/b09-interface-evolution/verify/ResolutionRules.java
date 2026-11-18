/**
 * 同じシグネチャのメソッドが複数の場所にあるとき、どれが呼ばれるかを観察する。
 * javap -c -p 'ResolutionRules$Resolver' で「インターフェイス名.super.メソッド名()」の命令を見る。
 */
public class ResolutionRules {

    // 規則1: クラス（スーパークラス）のメソッドが、インターフェイスのdefaultメソッドより優先される
    static class Base {
        public String who() {
            return "Base.who（クラス）";
        }
    }

    interface Named {
        default String who() {
            return "Named.who（default）";
        }
    }

    static class ClassWins extends Base implements Named {
    }

    // 規則2: より具体的なインターフェイス（サブインターフェイス）のdefaultメソッドが優先される
    interface Parent {
        default String who() {
            return "Parent.who";
        }
    }

    interface Child extends Parent {
        @Override
        default String who() {
            return "Child.who";
        }
    }

    // Parentを直接書き並べても、Childのほうが具体的なのでChildのメソッドになる
    static class SpecificWins implements Parent, Child {
    }

    // 規則3: どちらでも決まらないときは、クラスで明示的にオーバーライドする
    interface Left {
        default String who() {
            return "Left.who";
        }
    }

    interface Right {
        default String who() {
            return "Right.who";
        }
    }

    static class Resolver implements Left, Right {
        @Override
        public String who() {
            return "Resolver.who → " + Left.super.who() + " + " + Right.super.who();
        }
    }

    public static void main(String[] args) {
        IO.println("規則1: " + new ClassWins().who());
        IO.println("規則2: " + new SpecificWins().who());
        IO.println("規則3: " + new Resolver().who());
        // インターフェイスの型の変数から呼んでも、選ばれるメソッドは同じ
        Named named = new ClassWins();
        IO.println("Named型の変数から: " + named.who());
    }
}
