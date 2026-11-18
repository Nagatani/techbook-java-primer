import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

/**
 * 可変なオブジェクト（Date・配列）を持つクラスで、
 * 防御的コピーがないと外から中身を変えられることを観察する。
 *   javac DefensiveCopy.java && java DefensiveCopy
 */
public class DefensiveCopy {
    // コピーしない（危ない）
    static final class LeakyPeriod {
        private final Date start;

        LeakyPeriod(Date start) {
            this.start = start;
        }

        Date start() {
            return start;
        }
    }

    // 受け取るときと返すときにコピーする
    static final class SafePeriod {
        private final Date start;

        SafePeriod(Date start) {
            this.start = new Date(start.getTime());
        }

        Date start() {
            return new Date(start.getTime());
        }
    }

    // 不変なLocalDateなら、コピーは要らない
    record ModernPeriod(LocalDate start) {
    }

    public static void main(String[] args) {
        Date date = new Date(0);
        LeakyPeriod leaky = new LeakyPeriod(date);
        SafePeriod safe = new SafePeriod(date);
        date.setTime(86_400_000L);              // 呼び出し側が元のDateを変える
        leaky.start().setTime(2 * 86_400_000L); // ゲッターの戻り値を変える
        safe.start().setTime(2 * 86_400_000L);
        IO.println("LeakyPeriodのstart（ミリ秒）: " + leaky.start().getTime());
        IO.println("SafePeriodのstart（ミリ秒）: " + safe.start().getTime());

        LocalDate day = LocalDate.of(2026, 4, 1);
        ModernPeriod modern = new ModernPeriod(day);
        LocalDate next = day.plusDays(1);        // 新しいLocalDateを返す
        IO.println("LocalDateのplusDays後: 元=" + day + " 戻り値=" + next
                + " レコード=" + modern.start());

        // finalの配列も、要素は書き換えられる
        final int[] scores = {10, 20};
        scores[0] = 99;
        IO.println("final配列の要素を変更: " + Arrays.toString(scores));

        // clone()は浅いコピー: 2次元配列の内側の配列は共有される
        int[][] grid = {{1, 2}, {3, 4}};
        int[][] shallow = grid.clone();
        shallow[0][0] = 100;
        IO.println("clone()後に内側を変更: grid[0][0]=" + grid[0][0]
                + " 外側は別の配列: " + (shallow != grid)
                + " 内側は同じ配列: " + (shallow[0] == grid[0]));
    }
}
