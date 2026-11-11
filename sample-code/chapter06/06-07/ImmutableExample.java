import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImmutableExample {
    public static void main(String[] args) {
        // 不変オブジェクトの作成
        List<String> hobbies = new ArrayList<>(Arrays.asList("読書", "映画鑑賞"));
        ImmutablePerson person = new ImmutablePerson("田中太郎", 25, hobbies);
        
        // コンストラクタに渡したリストを変更しても、オブジェクトには影響しない
        hobbies.add("スポーツ");
        IO.println(person.getHobbies()); // [読書, 映画鑑賞]
        
        // 値を変更したい場合は新しいオブジェクトを作成
        ImmutablePerson olderPerson = person.withAge(26);
        IO.println("元の人: " + person); // age=25
        IO.println("新しい人: " + olderPerson); // age=26
        
        // 趣味を追加
        ImmutablePerson personWithNewHobby = person.addHobby("プログラミング");
        IO.println("元の趣味: " + person.getHobbies());
        IO.println("新しい趣味: " + personWithNewHobby.getHobbies());
    }
}
