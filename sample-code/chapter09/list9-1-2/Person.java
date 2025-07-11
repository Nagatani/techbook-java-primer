/**
 * リスト9-1
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (73行目)
 */

public final class Person {
    private final String name;  // ①
    private final int age;      // ①

    public Person(String name, int age) {  // ②
        this.name = name;
        this.age = age;
    }

    public String name() { return this.name; }  // ③
    public int age() { return this.age; }       // ③

    @Override
    public boolean equals(Object o) { /* 全フィールドを比較する実装 */ }  // ④

    @Override
    public int hashCode() { /* 全フィールドから計算する実装 */ }  // ⑤

    @Override
    public String toString() { /* 全フィールドを表示する実装 */ }  // ⑥
}