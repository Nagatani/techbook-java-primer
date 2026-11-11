// 子クラス
public class Wizard extends Character {
    int mp;

    public Wizard(String name, int hp, int mp) {
        // super()で親クラスのコンストラクタを呼び出す
        super(name, hp); 
        this.mp = mp;
    }

    // attackメソッドをオーバーライド
    @Override
    public void attack() {
        IO.println(this.name + "は杖で殴った！");
    }

    public void castSpell() {
        IO.println(this.name + "は魔法を唱えた！");
    }
}
