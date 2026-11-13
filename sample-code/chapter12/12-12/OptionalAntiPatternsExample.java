import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class OptionalAntiPatternsExample {
    static class BadExample {
        // アンチパターン1: フィールドにOptionalを使用（避けるべき）
        private Optional<String> name = Optional.empty();  // 悪い例
        
        // アンチパターン2: メソッド引数にOptionalを使用（避けるべき）
        public void processUser(Optional<String> userName) {  // 悪い例
            // 処理...
        }
        
        // アンチパターン3: コレクションをOptionalで包んで返す（避けるべき）
        public Optional<List<String>> getNames() {  // 悪い例
            // 空のリストを返すべき
            return Optional.empty();
        }
    }
    
    static class GoodExample {
        // 良い例1: フィールドは通常の型で、getterでOptionalを返す
        private String name;  // nullableフィールド
        
        public Optional<String> getName() {
            return Optional.ofNullable(name);
        }
        
        // 良い例2: メソッド引数は通常の型で、内部でOptionalに変換
        public void processUser(String userName) {
            Optional.ofNullable(userName)
                .filter(name -> !name.isEmpty())
                .ifPresent(name -> IO.println("Processing: " + name));
        }
        
        // 良い例3: コレクションは空のコレクションを返す
        public List<String> getNames() {
            // nullの代わりに空のリストを返す
            return names != null ? names : new ArrayList<>();
        }
        
        private List<String> names;
    }
    
    public static void main(String[] args) {
        // アンチパターン4: get()の過度な使用
        Optional<String> optional = Optional.of("value");
        
        // 悪い例：isPresentとgetの組み合わせ
        if (optional.isPresent()) {
            String value = optional.get();
            IO.println(value.toUpperCase());
        }
        
        // 良い例：mapを使用
        optional.map(String::toUpperCase)
            .ifPresent(IO::println);
        
        // アンチパターン5: Optional<Optional<T>>の作成
        // これは避けて、flatMapを使用すべき
        
        // 注意点：Optionalは値の一時的なコンテナ
        // - Serializableではない
        // - 長期保存には適さない
        // - APIの戻り値として使用し、フィールドには使わない
    }
}
