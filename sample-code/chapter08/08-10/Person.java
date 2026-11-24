import java.util.Objects;

public final class Person {
    private final String name;  // ①
    private final int age;      // ①
    
    public Person(String name, int age) {  // ②
        this.name = name;
        this.age = age;
    }
    
    public String name() { return this.name; }  // ③
    public int age() { return this.age; }       // ③
    
    @Override
    public boolean equals(Object o) {  // ④
        // 全フィールドを比較する実装
        return o instanceof Person other
                && Objects.equals(this.name, other.name)
                && this.age == other.age;
    }
    
    @Override
    public int hashCode() {  // ⑤
        // 全フィールドから計算する実装
        return Objects.hash(name, age);
    }
    
    @Override
    public String toString() {  // ⑥
        // 全フィールドを表示する実装
        return "Person[name=" + name + ", age=" + age + "]";
    }
}
