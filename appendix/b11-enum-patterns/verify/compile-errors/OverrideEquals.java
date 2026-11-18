/** enumはequalsをオーバーライドできない（Enum.equalsはfinal。コンパイルエラーになることを確かめるプローブ） */
public class OverrideEquals {
    enum Color {
        RED, GREEN, BLUE;

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }

    public static void main(String[] args) {
        IO.println(Color.RED);
    }
}
