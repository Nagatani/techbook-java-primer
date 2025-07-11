/**
 * リストAB-19
 * LazyEvaluationクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (802行目)
 */

public class LazyEvaluation {
    
    // 遅延評価を実現するSupplier
    public static class Lazy<T> {
        private Supplier<T> supplier;
        private T value;
        private boolean evaluated = false;
        
        public Lazy(Supplier<T> supplier) {
            this.supplier = supplier;
        }
        
        public T get() {
            if (!evaluated) {
                value = supplier.get();
                evaluated = true;
                supplier = null; // メモリリークを防ぐ
            }
            return value;
        }
        
        public <R> Lazy<R> map(Function<T, R> f) {
            return new Lazy<>(() -> f.apply(get()));
        }
        
        public <R> Lazy<R> flatMap(Function<T, Lazy<R>> f) {
            return new Lazy<>(() -> f.apply(get()).get());
        }
    }
    
    // 無限リストの実装
    public static class LazyList<T> {
        private final T head;
        private final Lazy<LazyList<T>> tail;
        
        public LazyList(T head, Supplier<LazyList<T>> tail) {
            this.head = head;
            this.tail = new Lazy<>(tail);
        }
        
        public T head() {
            return head;
        }
        
        public LazyList<T> tail() {
            return tail.get();
        }
        
        public Stream<T> stream() {
            return Stream.iterate(this, LazyList::tail)
                .map(LazyList::head);
        }
        
        // 無限の自然数列
        public static LazyList<Integer> naturals(int start) {
            return new LazyList<>(start, () -> naturals(start + 1));
        }
        
        // フィボナッチ数列
        public static LazyList<Integer> fibonacci() {
            return fibonacciHelper(0, 1);
        }
        
        private static LazyList<Integer> fibonacciHelper(int a, int b) {
            return new LazyList<>(a, () -> fibonacciHelper(b, a + b));
        }
    }
    
    public static void main(String[] args) {
        // 遅延評価の例
        Lazy<Integer> lazyValue = new Lazy<>(() -> {
            System.out.println("Computing expensive value...");
            return 42;
        });
        
        System.out.println("Lazy value created");
        System.out.println("Value: " + lazyValue.get()); // ここで初めて計算
        System.out.println("Value: " + lazyValue.get()); // 2回目はキャッシュ
        
        // 無限リストの使用
        LazyList.naturals(1)
            .stream()
            .limit(10)
            .forEach(System.out::println); // 1, 2, 3, ..., 10
            
        // フィボナッチ数列
        LazyList.fibonacci()
            .stream()
            .limit(10)
            .forEach(System.out::println); // 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
    }
}