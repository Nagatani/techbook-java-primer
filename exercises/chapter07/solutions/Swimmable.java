package chapter07.solutions;

/**
 * 泳げるもの（課題3の解答例）
 */
public interface Swimmable {
    // 泳ぐ深さ（m）。動物ごとに違う
    int getDepth();

    default void swim() {
        IO.println("水中を泳いでいます（深度: " + getDepth() + "m）");
    }
}
