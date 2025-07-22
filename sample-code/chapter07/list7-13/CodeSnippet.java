/**
 * リスト7-13
 * JsonSerializableインターフェイス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (455行目)
 */

public interface JsonSerializable {
    String toJson();
    
    // ファクトリメソッド
    static <T extends JsonSerializable> String serializeList(List<T> items) {
        return items.stream()
            .map(JsonSerializable::toJson)
            .collect(Collectors.joining(",", "[", "]"));
    }
    
    // ユーティリティメソッド
    static String escapeJson(String input) {
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}

// 使用例
List<User> users = List.of(new User("Alice"), new User("Bob"));
String json = JsonSerializable.serializeList(users);