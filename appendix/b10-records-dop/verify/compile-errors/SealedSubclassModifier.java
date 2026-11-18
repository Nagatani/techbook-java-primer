/** sealedな型を直接継承・実装するクラスは、final・sealed・non-sealedのどれかを付けなければならない */
public class SealedSubclassModifier {
    sealed interface Shape permits Circle { }

    static class Circle implements Shape { }
}
