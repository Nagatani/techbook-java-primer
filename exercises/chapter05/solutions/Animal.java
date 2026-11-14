/**
 * 動物の基底クラス（課題1の解答例）
 */
public class Animal {
    // protectedフィールドを宣言する（派生クラスからアクセス可能）
    protected String name;
    protected int age;
    
    // コンストラクター
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // 名前のgetterメソッド
    public String getName() {
        return name;
    }
    
    // 年齢のgetterメソッド
    public int getAge() {
        return age;
    }
    
    // 年齢のsetterメソッド
    public void setAge(int age) {
        this.age = age;
        IO.println("動物の年齢を更新しました。");
        IO.println("更新後の年齢: " + age + "歳");
    }
    
    // 鳴き声メソッド（派生クラスでオーバーライド予定）
    public void makeSound() {
        IO.println(name + "は鳴いています。");
    }
    
    // 動物の基本情報を表示するメソッド
    public void showInfo() {
        IO.println("動物の名前: " + name);
        IO.println("動物の年齢: " + age + "歳");
    }
    
    // 動物の一般的な行動メソッド
    public void sleep() {
        IO.println(name + "は眠っています。");
    }
    
    // 動物の一般的な行動メソッド
    public void eat() {
        IO.println(name + "は食事をしています。");
    }
}