/**
 * 第1章 発展課題1: 入力した内容でプロフィールカードを作る - 解答例
 */
public class ProfileCard {
    public static void main(String[] args) {
        String name = IO.readln("名前を入力: ");
        int grade = Integer.parseInt(IO.readln("学年を入力: "));
        String hobby = IO.readln("趣味を入力: ");

        IO.println();
        IO.println("+------------------------------+");
        IO.println("| 名前: %s".formatted(name));
        IO.println("| 学年: %d年生（来年は%d年生）".formatted(grade, grade + 1));
        IO.println("| 趣味: %s".formatted(hobby));
        IO.println("+------------------------------+");
    }
}
