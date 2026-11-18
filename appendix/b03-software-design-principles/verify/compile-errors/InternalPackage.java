// モジュールがexportしていないパッケージは、ほかのモジュールから使えない
import jdk.internal.misc.Unsafe;

class InternalPackage {
    Unsafe unsafe;
}
