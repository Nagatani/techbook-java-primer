// Person.java - 抽象基底クラス
public abstract class Person {
    protected String id;
    protected String name;
    protected int age;
    
    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    
    public abstract String getRole();
    
    // 共通のゲッター・セッター
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    
    public void setName(String name) { this.name = name; }
    public void setAge(int age) {
        if (age > 0) this.age = age;
    }
}