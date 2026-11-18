import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * リスコフの置換原則：TimestampはDateを継承しているが、equalsが対称にならない。
 * 実行: javac LspTimestamp.java && java LspTimestamp
 */
public class LspTimestamp {
    public static void main(String[] args) {
        long millis = 1_700_000_000_000L;
        Date date = new Date(millis);
        Timestamp ts = new Timestamp(millis);

        IO.println("date.equals(ts) = " + date.equals(ts));
        IO.println("ts.equals(date) = " + ts.equals(date));
        IO.println("hashCodeは同じ = " + (date.hashCode() == ts.hashCode()));

        // どちらを集合に入れ、どちらで探すかで結果が変わる
        Set<Date> hasTs = new HashSet<>(Set.of(ts));
        Set<Date> hasDate = new HashSet<>(Set.of(date));
        IO.println("{ts}.contains(date) = " + hasTs.contains(date));
        IO.println("{date}.contains(ts) = " + hasDate.contains(ts));
    }
}
