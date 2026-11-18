/** 明示的に宣言するアクセサーはpublicでなければならない */
public class AccessorNotPublic {
    record Point(int x, int y) {
        int x() {
            return x;
        }
    }
}
