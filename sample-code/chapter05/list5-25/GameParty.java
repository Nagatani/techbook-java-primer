/**
 * リスト5-25
 * GamePartyクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (1344行目)
 */

public class GameParty {
    public static void main(String[] args) {
        Character[] party = {
            new Hero("勇者", 100),
            new Wizard("魔法使い", 70, 50),
            new Knight("騎士", 120)
        };

        for (Character member : party) {
            member.attack(); // これはポリモーフィズムでOK

            // もし、メンバーがWizardだったら、特別に魔法も使わせる
            if (member instanceof Wizard) {
                // memberをWizard型にダウンキャストする
                Wizard wizard = (Wizard) member; 
                wizard.castSpell();
            }
        }
    }
}