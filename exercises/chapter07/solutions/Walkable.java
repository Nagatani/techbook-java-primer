package chapter07.solutions;

/**
 * 歩けるもの（課題3の解答例）
 *
 * 抽象メソッドがなくても、インターフェイスにできる。実装したクラスはwalkをそのまま使える。
 */
public interface Walkable {
    default void walk() {
        IO.println("地面を歩いています");
    }
}
