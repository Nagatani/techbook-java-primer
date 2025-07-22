/**
 * リスト7-4
 * Drawableインターフェイス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (168行目)
 */

// Drawable.java
public interface Drawable {
    // インターフェイス内のメソッドは自動的に public abstract になる
    void draw();
}

// Serializable.java
public interface Serializable {
    void saveToFile(String filename);
}