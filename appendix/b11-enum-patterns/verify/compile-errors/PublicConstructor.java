/** enumのコンストラクターにpublicは付けられない（コンパイルエラーになることを確かめるプローブ） */
public class PublicConstructor {
    enum Color {
        RED, GREEN, BLUE;

        public Color() {
        }
    }

    public static void main(String[] args) {
        IO.println(Color.RED);
    }
}
