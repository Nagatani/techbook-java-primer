import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImmutableExample {
    public static void main(String[] args) {
        // 不変オブジェクトの作成
        List<String> hobbies = Arrays.asList("読書", "映画鑑賞");
        ImmutablePerson person = new ImmutablePerson("田中太郎", 25, hobbies);
        
        // 元のリストを変更してもオブジェクトには影響しない
        hobbies = new ArrayList<>(hobbies);
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
        
        // マルチスレッド環境での安全な共有（スレッドは第15章で詳しく学ぶ）
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                // 不変オブジェクトはスレッドセーフ
                IO.println("T1: " + person.getName());
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                IO.println("T2: " + person.getAge());
            }
        });
        
        t1.start();
        t2.start();
    }
}
