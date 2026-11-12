/**
 * 本1冊。ツリーには題名だけを表示する。
 */
public record Book(String title, String author, int year) {
    @Override
    public String toString() {
        return title;
    }
}
