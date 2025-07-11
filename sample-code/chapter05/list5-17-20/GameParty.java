/**
 * リスト5-20
 * GamePartyクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (972行目)
 */

public class GameParty {
    public static void main(String[] args) {
        // 親クラスの配列に、様々な子クラスのインスタンスを格納できる
        Character[] party = new Character[3];
        party[0] = new Hero("勇者", 100);
        party[1] = new Wizard("魔法使い", 70, 50);
        party[2] = new Knight("騎士", 120);

        // パーティ全員で一斉攻撃！
        for (Character member : party) {
            // member変数の型はCharacterだが、実行時には
            // 実際のインスタンスのattack()が呼び出される
            member.attack(); 
        }
    }
}