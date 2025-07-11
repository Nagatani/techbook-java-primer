/**
 * リスト9-32
 * RecordBenchmarkクラス
 * 
 * 元ファイル: chapter09-records.md (1460行目)
 */

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
public class RecordBenchmark {
    
    // 従来のクラス
    static class TraditionalPoint {
        private final int x, y;
        
        TraditionalPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TraditionalPoint that = (TraditionalPoint) o;
            return x == that.x && y == that.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    
    // Record版
    record RecordPoint(int x, int y) {}
    
    @Benchmark
    public TraditionalPoint createTraditional() {
        return new TraditionalPoint(42, 100);
    }
    
    @Benchmark
    public RecordPoint createRecord() {
        return new RecordPoint(42, 100);
    }
    
    @Benchmark
    public boolean equalsTraditional() {
        var p1 = new TraditionalPoint(42, 100);
        var p2 = new TraditionalPoint(42, 100);
        return p1.equals(p2);
    }
    
    @Benchmark
    public boolean equalsRecord() {
        var p1 = new RecordPoint(42, 100);
        var p2 = new RecordPoint(42, 100);
        return p1.equals(p2);
    }
    
    @Benchmark
    public int hashCodeTraditional() {
        var p = new TraditionalPoint(42, 100);
        return p.hashCode();
    }
    
    @Benchmark
    public int hashCodeRecord() {
        var p = new RecordPoint(42, 100);
        return p.hashCode();
    }
}