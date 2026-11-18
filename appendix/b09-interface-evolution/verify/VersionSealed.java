/**
 * sealedインターフェイス（Java 17から書ける）。
 * javac --release 16 ではコンパイルできない。javap -v で PermittedSubclasses 属性を見る。
 */
public sealed interface VersionSealed permits VersionSealed.Circle, VersionSealed.Square {
    record Circle(double r) implements VersionSealed {
    }

    record Square(double side) implements VersionSealed {
    }
}
