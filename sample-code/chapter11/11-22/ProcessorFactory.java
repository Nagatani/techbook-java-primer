import java.util.Map;
import java.util.function.Supplier;

public class ProcessorFactory {
    interface DataProcessor {
        String process(String data);
    }

    static class CsvProcessor implements DataProcessor {
        public String process(String data) {
            return "CSVとして読み込み: " + data;
        }
    }

    static class XmlProcessor implements DataProcessor {
        public String process(String data) {
            return "XMLとして読み込み: " + data;
        }
    }

    static class JsonProcessor implements DataProcessor {
        public String process(String data) {
            return "JSONとして読み込み: " + data;
        }
    }

    // 形式の名前と、オブジェクトを作る処理（コンストラクター参照）の対応
    private final Map<String, Supplier<DataProcessor>> processors = Map.of(
        "csv", CsvProcessor::new,
        "xml", XmlProcessor::new,
        "json", JsonProcessor::new
    );

    public DataProcessor createProcessor(String type) {
        Supplier<DataProcessor> supplier = processors.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("未対応の形式: " + type);
        }
        return supplier.get();  // ここではじめてオブジェクトを生成する
    }

    public static void main(String[] args) {
        ProcessorFactory factory = new ProcessorFactory();
        IO.println(factory.createProcessor("csv").process("a,b,c"));
        IO.println(factory.createProcessor("json").process("{\"a\": 1}"));
        try {
            factory.createProcessor("yaml");
        } catch (IllegalArgumentException e) {
            IO.println("エラー: " + e.getMessage());
        }
    }
}
