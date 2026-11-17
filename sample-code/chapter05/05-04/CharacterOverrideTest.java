// 使用例
public class CharacterOverrideTest {
    public static void main(String[] args) {
        // 通常のキャラクター
        Character normalChar = new Character("戦士", 100);
        normalChar.attack();
        
        IO.println();
        
        // 魔法使い
        Wizard wizard = new Wizard("魔法使い", 70, 10);
        wizard.attack();  // オーバーライドされたメソッド
        wizard.castSpell();
        
        IO.println();
        
        // ポリモーフィズムの例
        Character polymorphicWizard = new Wizard("賢者", 80, 20);
        polymorphicWizard.attack();  // Wizardのattackメソッドが呼ばれる
        // コンパイルエラー：Character型にはcastSpellメソッドがない
        // polymorphicWizard.castSpell();

        IO.println();

        // superで親クラスのメソッドを呼び出す例
        Knight knight = new Knight("騎士", 120);
        // 親クラスのattack()に続けて、Knight独自の処理が実行される
        knight.attack();
    }
}
