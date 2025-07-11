/**
 * リスト5-5
 * Characterクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (328行目)
 */

public class Character {  // 親クラス（スーパークラス）
    String name;         // ①
    int hp;              // ①

    void attack() {      // ②
        System.out.println(this.name + "の攻撃！");
    }
}

public class Hero extends Character {  // ③
    void specialMove() {  // ④
        System.out.println(this.name + "の必殺技！");
    }
}

public class Wizard extends Character {  // ③
    int mp;  // ⑤

    void castSpell() {  // ④
        System.out.println(this.name + "は魔法を唱えた！");
    }
}