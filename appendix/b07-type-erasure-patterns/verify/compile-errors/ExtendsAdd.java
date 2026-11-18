import java.util.List;

// ? extends Number のリストには、何のリストかわからないので要素を追加できない
class ExtendsAdd {
    void add(List<? extends Number> numbers) {
        numbers.add(Integer.valueOf(1));
    }
}
