/**
 * 動物のクラスを試すプログラム（課題1の解答例）
 */
public class AnimalTest {
    public static void main(String[] args) {
        IO.println("=== 動物管理システム ===");
        
        // Dogオブジェクトを作成する
        Dog dog = new Dog("ポチ", 3);
        dog.showInfo();
        dog.makeSound();
        dog.wagTail();
        IO.println();
        
        // Catオブジェクトを作成する
        Cat cat = new Cat("ミケ", 2);
        cat.showInfo();
        cat.makeSound();
        cat.groom();
        IO.println();
        
        // ポリモーフィズムを試する（Animal型の配列に格納）
        Animal[] animals = {dog, cat};
        IO.println("ポリモーフィズムのテスト:");
        for (Animal animal : animals) {
            animal.showInfo();
            animal.makeSound();
            IO.println();
        }
        
        // 年齢を更新する
        dog.setAge(4);
        IO.println();
        
        // 犬と猫の固有行動をテストする
        IO.println("固有行動のテスト:");
        dog.fetch();
        cat.climb();
        IO.println();
        
        // 共通の行動をテストする
        IO.println("共通行動のテスト:");
        dog.eat();
        cat.sleep();
    }
}