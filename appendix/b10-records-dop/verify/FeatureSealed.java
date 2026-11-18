/**
 * sealedが使える版を確かめる（javac --release 16 と 17 で比べる）。
 */
public class FeatureSealed {
    sealed interface Shape permits Circle { }

    final class Circle implements Shape { }
}
