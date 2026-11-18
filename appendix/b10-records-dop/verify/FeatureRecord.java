/**
 * レコードが使える版を確かめる（javac --release 15 と 16 で比べる）。
 */
public class FeatureRecord {
    record Point(int x, int y) { }
}
