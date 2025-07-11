/**
 * リスト5-22
 * GamePartyAfterクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (1051行目)
 */

// 統一的な処理で全ての型を扱える
public class GamePartyAfter {
    public static void main(String[] args) {
        // ポリモーフィズム：親クラスの型で管理
        Character[] party = {
            new Hero("勇者", 100),
            new Wizard("魔法使い", 70, 50),
            new Knight("騎士", 120)
            // 新しいメンバーを追加しても、以下のコードは変更不要
            // new Archer("弓使い", 80)
        };
        
        System.out.println("=== パーティ全員の攻撃（ポリモーフィズムあり）===");
        
        // 統一的な処理で全員を扱える
        for (Character member : party) {
            member.attack(); // 実際の型に応じた攻撃が自動的に呼ばれる
        }
    }
    
    // 合計ダメージ計算も拡張性が高い
    public static int calculateTotalDamage(Character[] party) {
        int totalDamage = 0;
        
        // 型を意識せずに処理できる
        for (Character member : party) {
            totalDamage += member.getAttackPower(); // 各キャラクタの攻撃力を取得
        }
        
        return totalDamage;
    }
    
    // 特定の条件での処理も簡潔に書ける
    public static void healLowHpMembers(Character[] party) {
        for (Character member : party) {
            if (member.getHp() < 30) {
                member.heal(20); // HPが低いメンバーを回復
                System.out.println(member.getName() + " を回復しました");
            }
        }
    }
}

// 親クラスに共通インターフェースを定義
abstract class Character {
    protected String name;
    protected int hp;
    
    public abstract int getAttackPower();
    
    public void heal(int amount) {
        this.hp += amount;
    }
    
    public String getName() { return name; }
    public int getHp() { return hp; }
}