/**
 * 猫のクラス（課題1の解答例）
 */
public class Cat extends Animal {
    // コンストラクター（superキーワードを使用）
    public Cat(String name, int age) {
        super(name, age);
    }
    
    // makeSoundメソッドをオーバーライドする
    @Override
    public void makeSound() {
        IO.println(name + "は「ニャーニャー」と鳴きます。");
    }
    
    // 猫固有の行動メソッド
    public void groom() {
        IO.println(name + "は毛づくろいをしています。");
    }
    
    // 猫固有の行動メソッド
    public void climb() {
        IO.println(name + "は木に登っています。");
    }
    
    // showInfoメソッドをオーバーライドして猫の情報を表示する
    @Override
    public void showInfo() {
        super.showInfo(); // 親クラスの基本情報を表示
        IO.println("種類: 猫");
    }
}