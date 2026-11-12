/**
 * 魔法使い。MPがあるあいだは、防御力を無視する魔法で攻撃する
 */
public class Mage extends GameCharacter {
    private static final int SPELL_COST = 10;
    private int mp;
    private int magicPower;

    public Mage(String name, int maxHp, int attack, int defense, int mp, int magicPower) {
        super(name, maxHp, attack, defense);
        this.mp = mp;
        this.magicPower = magicPower;
    }

    public int getMp() {
        return mp;
    }

    @Override
    public void act(GameCharacter[] allies, GameCharacter enemy) {
        if (mp >= SPELL_COST) {
            mp -= SPELL_COST;
            IO.println("  " + getName() + "の炎の魔法（MP 残り" + mp + "）");
            enemy.takeDamage(magicPower);
        } else {
            super.act(allies, enemy);  // MPがなければふつうの攻撃
        }
    }

    @Override
    public String toString() {
        return super.toString() + " MP " + mp;
    }
}
