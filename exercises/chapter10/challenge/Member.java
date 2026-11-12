/**
 * サークルの会員。番号は"S001"のような文字列
 */
public record Member(String id, String name) implements Identifiable<String> {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + " " + name;
    }
}
