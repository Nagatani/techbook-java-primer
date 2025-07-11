/**
 * リストAB-16
 * OptionalFunctorクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (599行目)
 */

// ファンクタインターフェイス
public interface Functor<T> {
    <R> Functor<R> map(Function<T, R> f);
}

// Optionalファンクタ
public class OptionalFunctor<T> implements Functor<T> {
    private final Optional<T> value;
    
    public OptionalFunctor(Optional<T> value) {
        this.value = value;
    }
    
    @Override
    public <R> OptionalFunctor<R> map(Function<T, R> f) {
        return new OptionalFunctor<>(value.map(f));
    }
    
    public Optional<T> get() {
        return value;
    }
}

// リストファンクタ
public class ListFunctor<T> implements Functor<T> {
    private final List<T> values;
    
    public ListFunctor(List<T> values) {
        this.values = new ArrayList<>(values);
    }
    
    @Override
    public <R> ListFunctor<R> map(Function<T, R> f) {
        return new ListFunctor<>(
            values.stream()
                .map(f)
                .collect(Collectors.toList())
        );
    }
    
    public List<T> get() {
        return new ArrayList<>(values);
    }
}

// ファンクタ則の検証
public class FunctorLawsTest {
    public static void main(String[] args) {
        // 恒等則のテスト
        ListFunctor<Integer> numbers = new ListFunctor<>(List.of(1, 2, 3));
        ListFunctor<Integer> identity = numbers.map(Function.identity());
        System.out.println(numbers.get().equals(identity.get())); // true
        
        // 合成則のテスト
        Function<Integer, Integer> addOne = x -> x + 1;
        Function<Integer, Integer> multiplyTwo = x -> x * 2;
        
        ListFunctor<Integer> composed1 = numbers
            .map(addOne)
            .map(multiplyTwo);
            
        ListFunctor<Integer> composed2 = numbers
            .map(addOne.andThen(multiplyTwo));
            
        System.out.println(composed1.get().equals(composed2.get())); // true
    }
}