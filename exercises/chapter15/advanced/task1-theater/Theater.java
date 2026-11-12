import java.util.List;

/**
 * 映画館の座席。複数の窓口（スレッド）から同時に予約される。
 */
public class Theater {
    private final String[] reservedBy;  // 予約した人の名前。nullなら空席

    public Theater(int seatCount) {
        reservedBy = new String[seatCount];
    }

    public int getSeatCount() {
        return reservedBy.length;
    }

    /**
     * 希望した座席をすべて予約する。1席でも取れなければ何も予約しない。
     * 「空いているか調べる」と「予約する」のあいだに別のスレッドが割り込まないよう、
     * メソッド全体をsynchronizedにする
     */
    public synchronized boolean reserve(String customer, List<Integer> seatNumbers) {
        for (int number : seatNumbers) {
            if (number < 1 || number > reservedBy.length || reservedBy[number - 1] != null) {
                return false;
            }
        }
        for (int number : seatNumbers) {
            reservedBy[number - 1] = customer;
        }
        return true;
    }

    /** 予約済みの座席の数 */
    public synchronized int countReserved() {
        int count = 0;
        for (String name : reservedBy) {
            if (name != null) {
                count++;
            }
        }
        return count;
    }

    /** 座席表（予約済みは×、空席は○） */
    public synchronized String seatMap() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reservedBy.length; i++) {
            sb.append("[").append(i + 1).append(reservedBy[i] == null ? " ○" : " ×").append("]");
        }
        return sb.toString();
    }
}
