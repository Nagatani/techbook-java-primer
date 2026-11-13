import java.util.Arrays;

/**
 * ImmutablePersonを試すプログラム（課題2の解答例）
 */
public class ImmutablePersonTest {
    public static void main(String[] args) {
        IO.println("=== 不変人物情報クラステスト ===");

        String[] hobbies = {"読書", "映画鑑賞"};
        ImmutablePerson person = new ImmutablePerson("田中太郎", 25, hobbies);

        IO.println("元の人物情報:");
        person.showInfo();
        IO.println();

        IO.println("年齢を更新（26歳）:");
        ImmutablePerson older = person.withAge(26);
        IO.println("元の人物: " + person);
        IO.println("新しい人物: " + older);
        IO.println();

        IO.println("趣味を追加（プログラミング）:");
        ImmutablePerson withHobby = person.withHobby("プログラミング");
        IO.println("元の趣味: " + person.hobbiesText());
        IO.println("新しい趣味: " + withHobby.hobbiesText());
        IO.println();

        IO.println("趣味を削除（読書）:");
        IO.println("新しい趣味: " + withHobby.withoutHobby("読書").hobbiesText());
        IO.println();

        IO.println("防御的コピーの確認:");
        hobbies[0] = "料理";
        IO.println("渡した配列の0番目を「料理」にしたあと: " + person.hobbiesText());
        String[] got = person.getHobbies();
        got[1] = "釣り";
        IO.println("取り出した配列の1番目を「釣り」にしたあと: " + person.hobbiesText());
        IO.println("取り出した配列: " + Arrays.toString(got));
    }
}
