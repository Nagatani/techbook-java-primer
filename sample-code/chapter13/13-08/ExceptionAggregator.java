import java.util.*;

public class ExceptionAggregator {
    private final List<Exception> exceptions = new ArrayList<>();
    
    public void addException(Exception e) {
        exceptions.add(e);
    }
    
    public void throwIfAny() throws AggregateException {
        if (!exceptions.isEmpty()) {
            throw new AggregateException(exceptions);
        }
    }
    
    // 集約例外クラス
    public static class AggregateException extends Exception {
        private final List<Exception> exceptions;
        
        public AggregateException(List<Exception> exceptions) {
            super(String.format("%d個の例外が発生しました", exceptions.size()));
            this.exceptions = new ArrayList<>(exceptions);
        }
        
        public List<Exception> getExceptions() {
            return Collections.unmodifiableList(exceptions);
        }
        
        @Override
        public void printStackTrace() {
            super.printStackTrace();
            System.err.println("--- 内包する例外 ---");
            for (int i = 0; i < exceptions.size(); i++) {
                System.err.printf("例外 %d:%n", i + 1);
                exceptions.get(i).printStackTrace();
            }
        }
    }
    
    // 使用例
    public static void processMultipleTasks(List<Runnable> tasks) 
            throws AggregateException {
        ExceptionAggregator aggregator = new ExceptionAggregator();
        
        for (Runnable task : tasks) {
            try {
                task.run();
            } catch (Exception e) {
                aggregator.addException(e);
            }
        }
        
        aggregator.throwIfAny();
    }
}
