import java.util.Arrays;

/**
 * 不変の人物情報（課題2の解答例）
 *
 * 配列は、変数に入れても「同じ配列を指す」だけでコピーされない。
 * 受け取るときと返すときにコピーして、外から中身を変えられないようにする（防御的コピー）。
 */
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final String[] hobbies;

    public ImmutablePerson(String name, int age, String[] hobbies) {
        this.name = name;
        this.age = age;
        // 受け取った配列をそのまま持つと、呼び出し側が配列の要素を変えたときに、このオブジェクトも変わってしまう
        this.hobbies = hobbies.clone();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // 中身の配列をそのまま返さず、コピーを返す
    public String[] getHobbies() {
        return hobbies.clone();
    }

    public ImmutablePerson withAge(int newAge) {
        return new ImmutablePerson(name, newAge, hobbies);
    }

    // 要素を1つ増やした新しい配列を作り、最後に趣味を入れる
    public ImmutablePerson withHobby(String hobby) {
        String[] newHobbies = Arrays.copyOf(hobbies, hobbies.length + 1);
        newHobbies[hobbies.length] = hobby;
        return new ImmutablePerson(name, age, newHobbies);
    }

    // 指定した趣味を除いた新しい配列を作る
    public ImmutablePerson withoutHobby(String hobby) {
        int count = 0;
        for (String h : hobbies) {
            if (!h.equals(hobby)) {
                count++;
            }
        }
        String[] newHobbies = new String[count];
        int i = 0;
        for (String h : hobbies) {
            if (!h.equals(hobby)) {
                newHobbies[i] = h;
                i++;
            }
        }
        return new ImmutablePerson(name, age, newHobbies);
    }

    public String hobbiesText() {
        return Arrays.toString(hobbies);
    }

    public void showInfo() {
        IO.println("名前: " + name);
        IO.println("年齢: " + age + "歳");
        IO.println("趣味: " + hobbiesText());
    }

    @Override
    public String toString() {
        return name + "（" + age + "歳）";
    }
}
