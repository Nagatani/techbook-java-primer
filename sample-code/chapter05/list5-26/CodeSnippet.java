/**
 * リスト5-26
 * コードスニペット
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (1374行目)
 */

// 従来の書き方
if (member instanceof Wizard) {
    Wizard wizard = (Wizard) member;
    wizard.castSpell();
}

// パターンマッチングを使った書き方 (Java 16以降)
if (member instanceof Wizard wizard) {
    // instanceofがtrueの場合、キャスト済みのwizard変数が使える
    wizard.castSpell();
}