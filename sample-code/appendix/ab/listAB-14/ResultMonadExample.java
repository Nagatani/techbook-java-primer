/**
 * リストAB-14
 * ResultMonadExampleクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (441行目)
 */

// Resultモナド：成功/失敗を表現
public abstract class Result<T> {
    
    // return (of)
    public static <T> Result<T> of(T value) {
        return new Success<>(value);
    }
    
    public static <T> Result<T> error(String message) {
        return new Failure<>(message);
    }
    
    // flatMap (bind)
    public abstract <U> Result<U> flatMap(Function<T, Result<U>> f);
    
    // map (ファンクタとしての機能)
    public abstract <U> Result<U> map(Function<T, U> f);
    
    // 実装クラス
    private static class Success<T> extends Result<T> {
        private final T value;
        
        Success(T value) {
            this.value = value;
        }
        
        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            try {
                return f.apply(value);
            } catch (Exception e) {
                return new Failure<>(e.getMessage());
            }
        }
        
        @Override
        public <U> Result<U> map(Function<T, U> f) {
            try {
                return new Success<>(f.apply(value));
            } catch (Exception e) {
                return new Failure<>(e.getMessage());
            }
        }
    }
    
    private static class Failure<T> extends Result<T> {
        private final String error;
        
        Failure(String error) {
            this.error = error;
        }
        
        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            return new Failure<>(error);
        }
        
        @Override
        public <U> Result<U> map(Function<T, U> f) {
            return new Failure<>(error);
        }
    }
}

// 使用例
public class ResultMonadExample {
    public static Result<Integer> divide(int a, int b) {
        if (b == 0) {
            return Result.error("Division by zero");
        }
        return Result.of(a / b);
    }
    
    public static Result<Double> sqrt(int x) {
        if (x < 0) {
            return Result.error("Cannot take square root of negative number");
        }
        return Result.of(Math.sqrt(x));
    }
    
    public static void main(String[] args) {
        // モナディックな計算チェーン
        Result<Double> result = divide(20, 4)
            .flatMap(ResultMonadExample::sqrt)
            .map(x -> x * 2);
        
        // パターンマッチングで結果を処理
        switch (result) {
            case Result.Success<Double> s -> 
                System.out.println("Result: " + s.value);
            case Result.Failure<Double> f -> 
                System.out.println("Error: " + f.error);
        }
    }
}