/**
 * ゲームのキャラクター（親クラス）
 *
 * 攻撃・ダメージ・回復の基本の処理を持つ。
 * 1ターンの行動act()は「敵を攻撃する」が標準で、職業ごとの子クラスがオーバーライドする。
 */
public class GameCharacter {
    private String name;
    private int maxHp;
    private int hp;
    private int attack;
    private int defense;

    public GameCharacter(String name, int maxHp, int attack, int defense) {
        if (name == null || name.isBlank() || maxHp <= 0 || attack < 0 || defense < 0) {
            throw new IllegalArgumentException("能力値が正しくありません");
        }
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    // ふつうの攻撃。ダメージは「攻撃力 - 相手の防御力」で、少なくとも1
    public void attack(GameCharacter target) {
        int damage = Math.max(1, attack - target.getDefense());
        IO.println("  " + name + "の攻撃");
        target.takeDamage(damage);
    }

    // ダメージを受ける。子クラスでオーバーライドして減らすことができる
    public void takeDamage(int damage) {
        hp = Math.max(0, hp - damage);
        IO.println("    " + name + "に" + damage + "のダメージ（HP " + hp + "/" + maxHp + "）");
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
        IO.println("    " + name + "のHPが" + amount + "回復（HP " + hp + "/" + maxHp + "）");
    }

    // 1ターンの行動。alliesは味方の配列、enemyは相手
    public void act(GameCharacter[] allies, GameCharacter enemy) {
        attack(enemy);
    }

    @Override
    public String toString() {
        return name + " HP " + hp + "/" + maxHp;
    }
}
