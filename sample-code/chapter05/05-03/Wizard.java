// 子クラス：魔法使い
public class Wizard extends Character {
    int mp; // 子クラス独自のフィールド

    public void castSpell() {
        IO.println(name + "は魔法を唱えた！（MP: " + mp + "）");
    }
}
