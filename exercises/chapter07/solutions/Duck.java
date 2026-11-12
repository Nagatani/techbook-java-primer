package chapter07.solutions;

/**
 * アヒル（課題3の解答例）。飛べて、泳げて、歩ける
 *
 * クラスの継承（extends）は1つだけだが、インターフェイスはいくつでも実装できる。
 */
public class Duck implements Flyable, Swimmable, Walkable {
    @Override
    public int getAltitude() {
        return 50;
    }

    @Override
    public int getDepth() {
        return 2;
    }
}
