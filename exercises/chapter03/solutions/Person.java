/**
 * 人を表すクラス（課題1の解答例）
 */
public class Person {
    // インスタンス変数を宣言する
    String name;   // 名前
    int age;       // 年齢
    
    // 自己紹介メソッド
    void introduce() {
        IO.println("私の名前は" + name + "です。年齢は" + age + "歳です。");
    }
    
    // 誕生日メソッド（年齢を1つ増やす）
    void birthday() {
        age++;
        IO.println("誕生日おめでとう！");
    }
}