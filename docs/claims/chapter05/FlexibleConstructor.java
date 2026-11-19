// 第5章：super(...)の前に文を書く（JEP 513、Java 25で正式）
public class FlexibleConstructor {
    static class Parent {
        Parent(int x) {
        }
    }

    static class Child extends Parent {
        Child(int x) {
            if (x < 0) {
                throw new IllegalArgumentException("x < 0");
            }
            super(x);
        }
    }
}
