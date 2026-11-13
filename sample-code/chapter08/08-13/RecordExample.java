public class RecordExample {
    public static void main(String[] args) {
        Person person1 = new Person("山田太郎", 25);
        Person person2 = new Person("山田太郎", 25);
        Person person3 = new Person("鈴木花子", 30);
        
        // アクセサーメソッドの使用
        IO.println("名前: " + person1.name());
        IO.println("年齢: " + person1.age());
        
        // toString()の自動実装
        IO.println(person1);  // Person[name=山田太郎, age=25]
        
        // equals()の自動実装
        IO.println(person1.equals(person2));  // true
        IO.println(person1.equals(person3));  // false
        
        // hashCode()の自動実装
        IO.println(person1.hashCode() == person2.hashCode());  // true
    }
}
