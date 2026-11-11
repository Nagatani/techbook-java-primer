public class LocalClassExample {
    public void processData(final String prefix) {
        // ローカル内部クラス
        class DataProcessor {
            public String process(String data) {
                // メソッドのfinalまたは実質的にfinalな変数にアクセス可能
                return prefix + ": " + data.toUpperCase();
            }
        }
        
        DataProcessor processor = new DataProcessor();
        IO.println(processor.process("hello")); // 例: prefixが"Log"なら「Log: HELLO」
    }
}
