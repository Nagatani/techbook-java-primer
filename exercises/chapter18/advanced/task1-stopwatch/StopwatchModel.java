import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ストップウォッチの状態（GUIに依存しない）。
 * 時間は0.1秒を1とする整数で数える。
 */
public class StopwatchModel {
    private boolean running = false;
    private int elapsedTenths = 0;
    private final List<Integer> lapTimes = new ArrayList<>();

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    /** 停止中だけリセットできる */
    public boolean reset() {
        if (running) {
            return false;
        }
        elapsedTenths = 0;
        lapTimes.clear();
        return true;
    }

    /** タイマーから0.1秒ごとに呼ばれる */
    public void tick() {
        if (running) {
            elapsedTenths++;
        }
    }

    /** 計測中だけラップを記録できる */
    public boolean lap() {
        if (!running) {
            return false;
        }
        lapTimes.add(elapsedTenths);
        return true;
    }

    public boolean isRunning() {
        return running;
    }

    public int getElapsedTenths() {
        return elapsedTenths;
    }

    /** 記録した時点の経過時間の一覧 */
    public List<Integer> getLapTimes() {
        return Collections.unmodifiableList(lapTimes);
    }

    /** n番目（0から）のラップの区間の長さ */
    public int getLapLength(int index) {
        int previous = (index == 0) ? 0 : lapTimes.get(index - 1);
        return lapTimes.get(index) - previous;
    }

    /** 0.1秒単位の値を「分:秒.1/10秒」の形にする */
    public static String format(int tenths) {
        int minutes = tenths / 600;
        int seconds = (tenths / 10) % 60;
        int tenth = tenths % 10;
        return "%02d:%02d.%d".formatted(minutes, seconds, tenth);
    }
}
