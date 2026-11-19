// 第8章：enumのswitchのcaseに型名を付ける（Java 21から）
public class QualifiedEnumCase {
    enum Status { ACTIVE, INACTIVE }

    static int f(Status s) {
        switch (s) {
            case Status.ACTIVE:
                return 1;
            default:
                return 0;
        }
    }
}
