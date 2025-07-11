/**
 * リスト5-21
 * GamePartyBeforeクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (1002行目)
 */

// 型ごとに別々の処理を書く必要がある
public class GamePartyBefore {
    public static void main(String[] args) {
        Hero hero = new Hero("勇者", 100);
        Wizard wizard = new Wizard("魔法使い", 70, 50);
        Knight knight = new Knight("騎士", 120);
        
        // それぞれの型に応じた攻撃処理
        System.out.println("=== パーティ全員の攻撃（ポリモーフィズムなし）===");
        
        // Heroの攻撃
        hero.attack();
        
        // Wizardの攻撃
        wizard.attack();
        
        // Knightの攻撃
        knight.attack();
        
        // 新しいメンバーを追加するたびに、ここにコードを追加する必要がある
        // Archer archer = new Archer("弓使い", 80);
        // archer.attack();
    }
    
    // 全員の合計ダメージを計算する場合も型別処理が必要
    public static int calculateTotalDamage(Hero hero, Wizard wizard, Knight knight) {
        int totalDamage = 0;
        
        // 各型ごとに個別に処理
        if (hero != null) {
            totalDamage += 50; // Heroの攻撃力
        }
        if (wizard != null) {
            totalDamage += 30; // Wizardの攻撃力
        }
        if (knight != null) {
            totalDamage += 70; // Knightの攻撃力
        }
        
        // 新しい型が増えるたびに、このメソッドも修正が必要
        return totalDamage;
    }
}