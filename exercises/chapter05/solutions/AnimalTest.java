/**
 * 第5章 演習問題1: AnimalTestクラスの解答例
 */
public class AnimalTest {
    public static void main(String[] args) {
        System.out.println("=== Animal クラスのテスト ===");
        
        // ポリモーフィズムのデモ
        Animal[] animals = {
            new Dog("ポチ", 3, "柴犬"),
            new Cat("ミケ", 2, "茶トラ"),
            new Dog("ロッキー", 5, "ゴールデンレトリバー")
        };
        
        for (Animal animal : animals) {
            animal.displayInfo();
            animal.makeSound();
            
            // 型チェックで固有メソッド呼び出し
            if (animal instanceof Dog) {
                ((Dog) animal).wagTail();
            } else if (animal instanceof Cat) {
                ((Cat) animal).groom();
            }
            System.out.println();
        }
    }
}