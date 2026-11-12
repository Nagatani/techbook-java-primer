/**
 * 第5章 基本課題1: AnimalTest クラス
 * 
 * 動物の継承階層をテストするためのクラスです。
 * 継承、オーバーライド、superキーワードの動作を確認してください。
 * 
 * 実行例:
 * === 動物管理システム ===
 * 動物の名前: ポチ
 * 動物の年齢: 3歳
 * 種類: 犬
 * ポチは「ワンワン」と鳴きます。
 * ポチは尻尾を振っています。
 *
 * 動物の名前: ミケ
 * 動物の年齢: 2歳
 * 種類: 猫
 * ミケは「ニャーニャー」と鳴きます。
 * ミケは毛づくろいをしています。
 *
 * ポリモーフィズムのテスト:
 * 動物の名前: ポチ
 * 動物の年齢: 3歳
 * 種類: 犬
 * ポチは「ワンワン」と鳴きます。
 *
 * 動物の名前: ミケ
 * 動物の年齢: 2歳
 * 種類: 猫
 * ミケは「ニャーニャー」と鳴きます。
 *
 * 動物の年齢を更新しました。
 * 更新後の年齢: 4歳
 *
 * 固有行動のテスト:
 * ポチはボールを取ってきました。
 * ミケは木に登っています。
 *
 * 共通行動のテスト:
 * ポチは食事をしています。
 * ミケは眠っています。
 */
public class AnimalTest {
    public static void main(String[] args) {
        IO.println("=== 動物管理システム ===");
        
        // TODO: Dogオブジェクトを作成してください
        // Dog dog = new Dog("ポチ", 3);
        // dog.showInfo();
        // dog.makeSound();
        // dog.wagTail();
        // IO.println();
        
        // TODO: Catオブジェクトを作成してください
        // Cat cat = new Cat("ミケ", 2);
        // cat.showInfo();
        // cat.makeSound();
        // cat.groom();
        // IO.println();
        
        // TODO: ポリモーフィズムを試してください（Animal型の配列に格納）
        // Animal[] animals = {dog, cat};
        // IO.println("ポリモーフィズムのテスト:");
        // for (Animal animal : animals) {
        //     animal.showInfo();
        //     animal.makeSound();
        //     IO.println();
        // }
        
        // TODO: 年齢を更新してください
        // dog.setAge(4);
        // IO.println();
        
        // TODO: 犬と猫の固有行動をテストしてください
        // IO.println("固有行動のテスト:");
        // dog.fetch();
        // cat.climb();
        // IO.println();
        
        // TODO: 共通の行動をテストしてください
        // IO.println("共通行動のテスト:");
        // dog.eat();
        // cat.sleep();
    }
}