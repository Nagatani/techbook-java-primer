/** 一方が抽象メソッド、もう一方がdefaultメソッドでも、どちらかに決まらずコンパイルエラーになる */
public class AbstractAndDefault {
    interface WithDefault {
        default String who() {
            return "default";
        }
    }

    interface WithAbstract {
        String who();
    }

    static class Both implements WithDefault, WithAbstract {
    }
}
