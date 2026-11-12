package chapter07.solutions;

/**
 * 鳥（課題3の解答例）。飛べて、歩ける
 */
public class Bird implements Flyable, Walkable {
    @Override
    public int getAltitude() {
        return 100;
    }
}
