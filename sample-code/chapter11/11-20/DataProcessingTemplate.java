import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DataProcessingTemplate {
    record Customer(String name, int age) {
        boolean isValid() {
            return age >= 0;
        }
    }

    // データ処理の骨格：前処理 → メイン処理 → 後処理の順に適用する
    public <T, R> R processData(
        T input,
        Function<T, T> preprocessor,
        Function<T, R> processor,
        Function<R, R> postprocessor) {

        T preprocessed = preprocessor.apply(input);
        R processed = processor.apply(preprocessed);
        return postprocessor.apply(processed);
    }

    // 使用例：CSV形式の文字列から顧客の一覧を作る
    public List<Customer> processCsvData(String csvData) {
        return processData(
            csvData,
            data -> data.trim(),               // 前処理：前後の空白を除く
            this::parseCustomers,              // メイン処理
            customers -> {                     // 後処理：無効なデータを除く
                customers.removeIf(c -> !c.isValid());
                return customers;
            }
        );
    }

    // 「名前,年齢」を「;」で区切って並べた文字列を読む
    private List<Customer> parseCustomers(String data) {
        List<Customer> customers = new ArrayList<>();
        for (String row : data.split(";")) {
            String[] cols = row.split(",");
            customers.add(new Customer(cols[0], Integer.parseInt(cols[1])));
        }
        return customers;
    }

    public static void main(String[] args) {
        DataProcessingTemplate template = new DataProcessingTemplate();
        List<Customer> customers = template.processCsvData("  Alice,30;Bob,-1;Carol,25  ");
        IO.println(customers);
    }
}
