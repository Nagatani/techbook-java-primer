/**
 * 犬のクラス（課題1の解答例）
 */
public class Dog extends Animal {
    // コンストラクター（superキーワードを使用）
    public Dog(String name, int age) {
        super(name, age);
    }
    
    // makeSoundメソッドをオーバーライドする
    @Override
    public void makeSound() {
        IO.println(name + "は「ワンワン」と鳴きます。");
    }
    
    // 犬固有の行動メソッド
    public void wagTail() {
        IO.println(name + "は尻尾を振っています。");
    }
    
    // 犬固有の行動メソッド
    public void fetch() {
        IO.println(name + "はボールを取ってきました。");
    }
    
    // showInfoメソッドをオーバーライドして犬の情報を表示する
    @Override
    public void showInfo() {
        super.showInfo(); // 親クラスの基本情報を表示
        IO.println("種類: 犬");
    }
}