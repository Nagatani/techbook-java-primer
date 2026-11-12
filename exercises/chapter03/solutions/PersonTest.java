/**
 * Personを試すプログラム（課題1の解答例）
 */
public class PersonTest {
    public static void main(String[] args) {
        IO.println("=== Person クラステスト ===");
        
        // Personオブジェクトを作成する
        Person person = new Person();
        
        // 名前と年齢を設定する
        person.name = "田中太郎";
        person.age = 20;
        
        // 自己紹介メソッドを呼び出する
        person.introduce();
        
        // 誕生日メソッドを呼び出する
        person.birthday();
        
        // もう一度自己紹介メソッドを呼び出して年齢の変化を確認する
        person.introduce();
    }
}