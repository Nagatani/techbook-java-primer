// 第10章：ジェネリクス（J2SE 5.0）とダイヤモンド（Java 7）。--release 8で通ることだけを確かめる
import java.util.ArrayList;
import java.util.List;

public class Generics {
    static List<String> a = new ArrayList<String>();
    static List<String> b = new ArrayList<>();
}
