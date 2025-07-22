/**
 * リスト7-7
 * ModernInterfaceインターフェイス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (251行目)
 */

// Java 8でのインターフェイス拡張
public interface ModernInterface {
    // 従来の抽象メソッド
    void abstractMethod();
    
    // defaultメソッド：実装を持つ
    default void defaultMethod() {
        System.out.println("デフォルト実装");
    }
    
    // staticメソッド：ユーティリティ機能
    static void staticUtilityMethod() {
        System.out.println("静的ユーティリティ");
    }
}