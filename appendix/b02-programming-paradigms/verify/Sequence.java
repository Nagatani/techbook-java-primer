import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** List<Optional<T>> を Optional<List<T>> に裏返す（sequence） */
public class Sequence {

    static <T> Optional<List<T>> sequence(List<Optional<T>> list) {
        List<T> values = new ArrayList<>();
        for (Optional<T> o : list) {
            if (o.isEmpty()) {
                return Optional.empty();
            }
            values.add(o.get());
        }
        return Optional.of(List.copyOf(values));
    }

    static Optional<Integer> parse(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        IO.println(sequence(List.of(Optional.of(1), Optional.of(2), Optional.of(3))));
        IO.println(sequence(List.of(Optional.of(1), Optional.<Integer>empty(), Optional.of(3))));
        // 入力をすべて数に直せたときだけ、数のリストを得る
        IO.println(sequence(List.of("10", "20", "30").stream().map(Sequence::parse).toList()));
        IO.println(sequence(List.of("10", "x", "30").stream().map(Sequence::parse).toList()));
    }
}
