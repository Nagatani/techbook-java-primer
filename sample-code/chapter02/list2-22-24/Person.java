/**
 * リスト2-22
 * Personクラス
 * 
 * 元ファイル: chapter02-getting-started.md (836行目)
 */

public class Person {
    // フィールド（インスタンス変数）
    private String name;
    private int age;
    
    // コンストラクタ
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // メソッド（振る舞い）
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void introduce() {
        System.out.println("こんにちは、" + name + "です。" + age + "歳です。");
    }
}