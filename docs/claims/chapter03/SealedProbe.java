// 第3章：シールクラス（Java 17で正式）
public sealed interface SealedProbe permits SealedProbe.A {
    final class A implements SealedProbe {
    }
}
