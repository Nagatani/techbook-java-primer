package chapter07.solutions;

/**
 * 飛べるもの（課題3の解答例）
 */
public interface Flyable {
    // 飛ぶ高さ（m）。動物ごとに違う
    int getAltitude();

    default void fly() {
        IO.println("空を飛んでいます（高度: " + getAltitude() + "m）");
    }
}
