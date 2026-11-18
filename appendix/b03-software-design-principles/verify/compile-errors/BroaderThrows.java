import java.io.IOException;

// オーバーライドで、親のメソッドが宣言していない検査例外を投げることはできない
class BroaderThrows {
    static class Base {
        void load() throws IOException {}
    }

    static class Child extends Base {
        @Override
        void load() throws Exception {}
    }
}
