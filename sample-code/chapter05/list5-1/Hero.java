/**
 * リスト5-1
 * Heroクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (61行目)
 */

public class Hero {
    String name;      // ①
    int hp;          // ①
    int maxHp;       // ①
    
    void attack() { 
        System.out.println(name + "が攻撃！");
    }
    
    void takeDamage(int damage) {  // ②
        hp -= damage;
        if (hp < 0) hp = 0;
    }
}

public class Wizard {
    String name;      // ①と重複
    int hp;          // ①と重複
    int maxHp;       // ①と重複
    int mp;          // ③
    
    void attack() {  // ④
        System.out.println(name + "が攻撃！");
    }
    
    void takeDamage(int damage) {  // ②と完全重複
        hp -= damage;
        if (hp < 0) hp = 0;
    }
    
    void castSpell() { /* 魔法使い特有の処理 */ }  // ⑤
}