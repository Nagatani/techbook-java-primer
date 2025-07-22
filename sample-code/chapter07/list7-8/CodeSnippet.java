/**
 * リスト7-8
 * CompleteInterfaceインターフェイス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (272行目)
 */

// Java 9でのさらなる拡張
public interface CompleteInterface {
    // publicメソッド
    default void publicMethod() {
        privateMethod();
        privateStaticMethod();
    }
    
    // privateメソッド：内部実装の共有
    private void privateMethod() {
        System.out.println("内部実装の詳細");
    }
    
    // private staticメソッド
    private static void privateStaticMethod() {
        System.out.println("静的な内部処理");
    }
}