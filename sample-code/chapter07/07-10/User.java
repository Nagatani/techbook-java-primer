// JsonSerializableを実装するクラス（toJson()だけを実装する）
public class User implements JsonSerializable {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public String toJson() {
        return "{\"name\":\"" + JsonSerializable.escapeJson(name) + "\"}";
    }
}
