// sealedで種類を閉じると、種類を足したときに、場合分けし忘れた箇所をコンパイラーが見つける
class SealedSwitchMissing {
    sealed interface Shape permits Rectangle, Circle, Triangle {}
    record Rectangle(double w, double h) implements Shape {}
    record Circle(double r) implements Shape {}
    record Triangle(double b, double h) implements Shape {} // あとから足した種類

    static double area(Shape s) {
        return switch (s) {
            case Rectangle r -> r.w() * r.h();
            case Circle c -> Math.PI * c.r() * c.r();
        };
    }
}
