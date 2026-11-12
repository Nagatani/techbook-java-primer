/**
 * 騎士。戦士の攻撃に加えて、盾で受けるダメージを半分にする
 */
public class Knight extends Warrior {
    public Knight(String name, int maxHp, int attack, int defense) {
        super(name, maxHp, attack, defense);
    }

    @Override
    public void takeDamage(int damage) {
        IO.println("    " + getName() + "は盾で防いだ");
        super.takeDamage((damage + 1) / 2);
    }
}
