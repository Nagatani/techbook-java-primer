// 使用例
public class CharacterOverrideTest {
    public static void main(String[] args) {
        // 通常のキャラクター
        Character normalChar = new Character("戦士", 100);
        normalChar.attack();
        
        System.out.println();
        
        // 魔法使い
        Wizard wizard = new Wizard("魔法使い", 70, 10);
        wizard.attack();  // オーバーライドされたメソッド
        wizard.castSpell();
        
        System.out.println();
        
        // ポリモーフィズムの例
        Character polymorphicWizard = new Wizard("賢者", 80, 20);
        polymorphicWizard.attack();  // Wizardのattackメソッドが呼ばれる
        // polymorphicWizard.castSpell();  // コンパイルエラー：Character型にはcastSpellメソッドがない

        System.out.println();

        // superで親クラスのメソッドを呼び出す例
        Knight knight = new Knight("騎士", 120);
        knight.attack();  // 親クラスのattack()に続けて、Knight独自の処理が実行される
    }
}
