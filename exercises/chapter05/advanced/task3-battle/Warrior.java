/**
 * 戦士。HPが半分以下になると攻撃力が上がる
 */
public class Warrior extends GameCharacter {
    public Warrior(String name, int maxHp, int attack, int defense) {
        super(name, maxHp, attack, defense);
    }

    @Override
    public void attack(GameCharacter target) {
        if (getHp() * 2 <= getMaxHp()) {
            IO.println("  " + getName() + "の渾身の一撃");
            target.takeDamage(Math.max(1, getAttack() * 3 / 2 - target.getDefense()));
        } else {
            super.attack(target);
        }
    }
}
