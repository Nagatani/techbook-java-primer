public class GenericMethodExample {
    // Tという型パラメーターを持つジェネリックメソッド
    public static <T> void printArray(T[] inputArray) {
        for (T element : inputArray) {
            IO.print(element + " ");
        }
        IO.println();
    }

    public static void main(String[] args) {
        Integer[] intArray = { 1, 2, 3 };
        String[] stringArray = { "A", "B", "C" };

        IO.print("Integer配列: ");
        printArray(intArray); // TはIntegerと推論される

        IO.print("String配列: ");
        printArray(stringArray); // TはStringと推論される
    }
}
