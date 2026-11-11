// PersonTest.java
public class PersonTest {
    public static void main(String[] args) {
        Person person = new Person();
        
        // setterを使ってデータを設定
        person.setName("山田太郎");
        person.setAge(25);
        
        // getterを使ってデータを取得
        IO.println("名前: " + person.getName());
        IO.println("年齢: " + person.getAge() + "歳");
        
        // 無効な年齢を設定してみる
        person.setAge(200);
    }
}
