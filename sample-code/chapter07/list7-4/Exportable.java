/**
 * リスト7-4
 * Exportableインターフェイス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (178行目)
 */
// Exportable.java
public interface Exportable {
    // インターフェイス内のメソッドは自動的に public abstract になる
    void exportToFormat(String format);
}