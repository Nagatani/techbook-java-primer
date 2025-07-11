/**
 * リストAB-22
 * IOExampleクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (1136行目)
 */

// IOモナドの簡易実装
public abstract class IO<T> {
    
    // 実行
    public abstract T unsafeRun();
    
    // pure (return)
    public static <T> IO<T> pure(T value) {
        return new IO<T>() {
            @Override
            public T unsafeRun() {
                return value;
            }
        };
    }
    
    // flatMap (bind)
    public <U> IO<U> flatMap(Function<T, IO<U>> f) {
        return new IO<U>() {
            @Override
            public U unsafeRun() {
                T result = IO.this.unsafeRun();
                return f.apply(result).unsafeRun();
            }
        };
    }
    
    // map
    public <U> IO<U> map(Function<T, U> f) {
        return flatMap(t -> pure(f.apply(t)));
    }
    
    // 基本的なIO操作
    public static IO<String> readLine() {
        return new IO<String>() {
            @Override
            public String unsafeRun() {
                return new Scanner(System.in).nextLine();
            }
        };
    }
    
    public static IO<Void> println(String s) {
        return new IO<Void>() {
            @Override
            public Void unsafeRun() {
                System.out.println(s);
                return null;
            }
        };
    }
    
    // 合成されたIO操作
    public static IO<Void> echo() {
        return readLine()
            .flatMap(line -> println("You entered: " + line));
    }
    
    // より複雑な例
    public static IO<Integer> getNumber() {
        return println("Enter a number:")
            .flatMap(_ -> readLine())
            .map(Integer::parseInt);
    }
    
    public static IO<Void> calculator() {
        return getNumber()
            .flatMap(a -> getNumber()
                .flatMap(b -> {
                    int sum = a + b;
                    return println("Sum: " + sum);
                })
            );
    }
    
    // 並列実行
    public <U> IO<Pair<T, U>> par(IO<U> other) {
        return new IO<Pair<T, U>>() {
            @Override
            public Pair<T, U> unsafeRun() {
                CompletableFuture<T> f1 = CompletableFuture.supplyAsync(
                    () -> IO.this.unsafeRun()
                );
                CompletableFuture<U> f2 = CompletableFuture.supplyAsync(
                    () -> other.unsafeRun()
                );
                
                try {
                    return new Pair<>(f1.get(), f2.get());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    
    // エラーハンドリング
    public IO<T> handleError(Function<Exception, T> handler) {
        return new IO<T>() {
            @Override
            public T unsafeRun() {
                try {
                    return IO.this.unsafeRun();
                } catch (Exception e) {
                    return handler.apply(e);
                }
            }
        };
    }
}

// 使用例
public class IOExample {
    public static void main(String[] args) {
        // 純粋な関数型プログラム
        IO<Void> program = IO.println("Welcome to FP Calculator!")
            .flatMap(_ -> IO.calculator())
            .flatMap(_ -> IO.println("Goodbye!"));
        
        // 副作用はmainの最後でのみ実行
        program.unsafeRun();
    }
}