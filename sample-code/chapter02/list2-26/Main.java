/**
 * リスト2-26
 * Mainクラス
 * 
 * 元ファイル: chapter02-getting-started.md (914行目)
 */

public class Main {
    public static void main(String[] args) {
        // newキーワードでオブジェクトを作成
        Person person1 = new Person("田中太郎", 25);
        Person person2 = new Person("佐藤花子", 30);
        
        // メソッドを呼び出す
        person1.introduce();  // こんにちは、田中太郎です。25歳です。
        person2.introduce();  // こんにちは、佐藤花子です。30歳です。
        
        // getter メソッドで値を取得
        String name1 = person1.getName();
        System.out.println("名前: " + name1);
    }
}