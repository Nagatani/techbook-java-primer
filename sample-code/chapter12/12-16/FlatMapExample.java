import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapExample {
    public static void main(String[] args) {
        List<List<String>> nestedList = Arrays.asList(
            Arrays.asList("Java", "Python"),
            Arrays.asList("JavaScript", "Go"),
            Arrays.asList("Rust", "C++")
        );
        
        // flatMapを使ってネストしたリストを平坦化する
        // この例では、リストのリストから単一の文字列リストを作成
        List<String> flatList = nestedList.stream()
            .flatMap(List::stream)  // 各内部リストをストリームに変換して結合
            .collect(Collectors.toList());
        
        IO.println(flatList);
        // 出力: [Java, Python, JavaScript, Go, Rust, C++]
        
        // 文字列を単語に分割する例
        List<String> sentences = Arrays.asList(
            "Hello world",
            "Java programming",
            "Stream API"
        );
        
        // 各文字列を単語に分割し、すべての単語を1つのリストにまとめる
        List<String> words = sentences.stream()
            .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
            .collect(Collectors.toList());
        
        IO.println(words);
        // 出力: [Hello, world, Java, programming, Stream, API]
    }
}
