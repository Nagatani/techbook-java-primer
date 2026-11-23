// 第13章：オーバーライドでthrowsに親より広い検査例外を書くとコンパイルエラー
import java.io.IOException;

public class OverrideBroader {
    static class Parent {
        void load() throws IOException { }
    }

    static class Child extends Parent {
        @Override
        void load() throws Exception { }
    }
}
