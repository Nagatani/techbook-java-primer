/**
 * リスト9-2
 * RecordExampleクラス
 * 
 * 元ファイル: chapter09-records.md (127行目)
 */

public class RecordExample {
    public static void main(String[] args) {
        Person alice = new Person("Alice", 30);    // ①
        Person bob = new Person("Bob", 40);        // ①
        Person alice2 = new Person("Alice", 30);   // ①

        System.out.println("名前: " + alice.name());  // ②
        System.out.println("年齢: " + alice.age());   // ②

        System.out.println(alice);  // ③

        System.out.println("aliceとbobは等しいか？: " + alice.equals(bob));     // ④
        System.out.println("aliceとalice2は等しいか？: " + alice.equals(alice2)); // ④
    }
}