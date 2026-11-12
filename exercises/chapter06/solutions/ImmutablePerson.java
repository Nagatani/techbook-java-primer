import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 不変の人物情報（課題2の解答例）
 *
 * List<String>（文字列を順に並べて持つリスト）とArrayListは第9章で学ぶ。
 * ここでは「new ArrayList<>(元のリスト)で、同じ要素を持つ別のリストを作れる」ことだけを使う。
 */
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;

    public ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        // 防御的コピー: 受け取ったリストをそのまま持つと、呼び出し側がリストを変えたときに、このオブジェクトも変わってしまう
        this.hobbies = new ArrayList<>(hobbies);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // 変更できないリストとして返す。add などを呼ぶと UnsupportedOperationException になる
    public List<String> getHobbies() {
        return Collections.unmodifiableList(hobbies);
    }

    // 年齢だけを変えた、新しいオブジェクトを返す
    public ImmutablePerson withAge(int newAge) {
        return new ImmutablePerson(name, newAge, hobbies);
    }

    // 趣味を1つ足した、新しいオブジェクトを返す。自分のリストは変えない
    public ImmutablePerson withHobby(String hobby) {
        List<String> newHobbies = new ArrayList<>(hobbies);
        newHobbies.add(hobby);
        return new ImmutablePerson(name, age, newHobbies);
    }

    // 趣味を1つ除いた、新しいオブジェクトを返す
    public ImmutablePerson withoutHobby(String hobby) {
        List<String> newHobbies = new ArrayList<>(hobbies);
        newHobbies.remove(hobby);
        return new ImmutablePerson(name, age, newHobbies);
    }

    public void showInfo() {
        IO.println("名前: " + name);
        IO.println("年齢: " + age + "歳");
        IO.println("趣味: " + hobbies);
    }

    @Override
    public String toString() {
        return name + "（" + age + "歳）";
    }
}
