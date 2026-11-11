// 親クラス
public class Character {
    String name;
    int hp;

    public void attack() {
        IO.println(name + "の攻撃！（HP: " + hp + "）");
    }
}
