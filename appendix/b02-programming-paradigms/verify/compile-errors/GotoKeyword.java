/** gotoは予約語なので、変数名にも文にも使えない */
public class GotoKeyword {
    static int f() {
        int goto = 1;
        return goto;
    }
}
