import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 完全な不変クラスの例
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;
    
    public ImmutablePerson(String name, int age, List<String> hobbies) {
        // パラメーターの検証
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("年齢は0～150の範囲である必要があります");
        }
        
        this.name = name.trim();
        this.age = age;
        // 防御的コピー：外部のリストが変更されても影響を受けない
        this.hobbies = hobbies != null 
            ? new ArrayList<>(hobbies) 
            : new ArrayList<>();
    }
    
    // getterメソッドのみ提供（setterは存在しない）
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public List<String> getHobbies() {
        // 防御的コピー：内部リストへの参照を返さない
        return new ArrayList<>(hobbies);
    }
    
    // 値を変更したい場合は新しいオブジェクトを作成
    public ImmutablePerson withAge(int newAge) {
        return new ImmutablePerson(this.name, newAge, this.hobbies);
    }
    
    public ImmutablePerson addHobby(String hobby) {
        List<String> newHobbies = new ArrayList<>(this.hobbies);
        newHobbies.add(hobby);
        return new ImmutablePerson(this.name, this.age, newHobbies);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ImmutablePerson that = (ImmutablePerson) obj;
        return age == that.age && 
               Objects.equals(name, that.name) && 
               Objects.equals(hobbies, that.hobbies);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age, hobbies);
    }
    
    @Override
    public String toString() {
        return "ImmutablePerson{name='" + name + "', age=" + age + 
               ", hobbies=" + hobbies + "}";
    }
}
