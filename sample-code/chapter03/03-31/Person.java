public class Person {
    private String name;
    private int age;
    
    // デフォルトコンストラクタ
    public Person() {
        this("名無し", 0);
    }
    
    // 名前のみを指定するコンストラクタ
    public Person(String name) {
        this(name, 0);
    }
    
    // すべての属性を指定するコンストラクタ
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
