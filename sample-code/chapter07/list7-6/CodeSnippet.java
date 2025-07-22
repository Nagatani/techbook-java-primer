/**
 * リスト7-6
 * OldStyleInterfaceインターフェイス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (236行目)
 */

// 従来のインターフェイス（Java 7まで）
public interface OldStyleInterface {
    // 定数のみ（暗黙的にpublic static final）
    int CONSTANT = 42;
    
    // 抽象メソッドのみ（暗黙的にpublic abstract）
    void abstractMethod();
    String anotherMethod(int param);
}