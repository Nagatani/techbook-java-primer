/**
 * リスト7-12
 * コードスニペット
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (413行目)
 */

// 規則1：クラスが常に優先
class BaseClass {
    public void method() {
        System.out.println("BaseClass");
    }
}

interface BaseInterface {
    default void method() {
        System.out.println("BaseInterface");
    }
}

class Derived extends BaseClass implements BaseInterface {
    // BaseClassのmethod()が自動的に使われる（インターフェイスより優先）
}

// 規則2：より具体的なインターフェイスが優先
interface Parent {
    default void method() {
        System.out.println("Parent");
    }
}

interface Child extends Parent {
    @Override
    default void method() {
        System.out.println("Child");
    }
}

class Implementation implements Parent, Child {
    // Childのmethod()が自動的に使われる（より具体的）
}