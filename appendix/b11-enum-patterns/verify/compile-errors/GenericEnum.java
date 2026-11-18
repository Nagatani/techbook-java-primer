/** enumは型パラメーターを持てない（コンパイルエラーになることを確かめるプローブ） */
public class GenericEnum {
    enum Key<T> { PORT, HOST }

    public static void main(String[] args) {
        IO.println(Key.PORT);
    }
}
