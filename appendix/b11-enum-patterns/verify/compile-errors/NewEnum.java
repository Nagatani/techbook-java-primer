/** enumのインスタンスはnewで作れない（コンパイルエラーになることを確かめるプローブ） */
public class NewEnum {
    enum Color { RED, GREEN, BLUE }

    public static void main(String[] args) {
        Color c = new Color();
        IO.println(c);
    }
}
