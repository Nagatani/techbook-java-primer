/** sealedインターフェイス（--release 17 からコンパイルできる） */
public class SinceSealed {
    sealed interface Shape permits Circle, Square {}
    static final class Circle implements Shape {}
    static final class Square implements Shape {}
}
