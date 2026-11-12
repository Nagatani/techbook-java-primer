package chapter07.solutions;

/**
 * 動物の行動のインターフェイスを試すプログラム（課題3の解答例）
 */
public class AnimalInterfaceTest {
    public static void main(String[] args) {
        IO.println("=== 動物行動インターフェイステスト ===");

        Bird bird = new Bird();
        IO.println("鳥の行動:");
        bird.fly();
        bird.walk();
        IO.println();

        Fish fish = new Fish();
        IO.println("魚の行動:");
        fish.swim();
        IO.println();

        Duck duck = new Duck();
        IO.println("アヒルの行動:");
        duck.fly();
        duck.swim();
        duck.walk();
        IO.println();

        // インターフェイスの型の配列には、そのインターフェイスを実装したクラスなら何でも入れられる
        Flyable[] flyers = {bird, duck};
        IO.println("飛べる動物:");
        for (Flyable flyer : flyers) {
            flyer.fly();
        }

        Swimmable[] swimmers = {fish, duck};
        IO.println("泳げる動物の数: " + swimmers.length);
    }
}
