/**
 * ゲームの得点。得点の大きさで比べる
 */
public record GameScore(String player, int points) implements Comparable<GameScore> {
    @Override
    public int compareTo(GameScore other) {
        return Integer.compare(points, other.points);
    }

    @Override
    public String toString() {
        return player + " " + points + "点";
    }
}
