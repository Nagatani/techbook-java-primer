/**
 * 回復役。HPが半分以下の味方がいれば回復し、いなければ攻撃する
 */
public class Healer extends GameCharacter {
    private int healPower;

    public Healer(String name, int maxHp, int attack, int defense, int healPower) {
        super(name, maxHp, attack, defense);
        this.healPower = healPower;
    }

    @Override
    public void act(GameCharacter[] allies, GameCharacter enemy) {
        GameCharacter weakest = null;
        for (GameCharacter ally : allies) {
            if (ally.isAlive() && ally.getHp() * 2 <= ally.getMaxHp()
                    && (weakest == null || ally.getHp() < weakest.getHp())) {
                weakest = ally;
            }
        }
        if (weakest != null) {
            IO.println("  " + getName() + "の回復魔法 → " + weakest.getName());
            weakest.heal(healPower);
        } else {
            super.act(allies, enemy);
        }
    }
}
