// 標準ライブラリのStringクラスは、finalクラスの代表例
// public final class String ... と宣言されているので、継承できない
// class MyString extends String { }  // コンパイルエラー

// 不変クラスは通常finalにする
public final class ImmutablePerson {
    private final String name;
    private final int age;
    
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
}

// 以下はコンパイルエラー
// class ExtendedPerson extends ImmutablePerson { }
