// 第5章：共変戻り値型（Java 5から）
public class CovariantReturn {
    static class Animal {
        Animal copy() {
            return new Animal();
        }
    }

    static class Dog extends Animal {
        @Override
        Dog copy() {
            return new Dog();
        }
    }
}
