// 本文では省略している補助クラス
public class Person {
    private String name;
    private int age;
    private boolean adultPrivileges;

    public Person() {}

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public void grantAdultPrivileges() { this.adultPrivileges = true; }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", adult=" + adultPrivileges + "}";
    }
}
