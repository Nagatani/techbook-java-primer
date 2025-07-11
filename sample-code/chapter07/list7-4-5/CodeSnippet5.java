/**
 * リスト7-5
 * Characterクラス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (186行目)
 */

// Character.java
// DrawableとSerializableの両方のインターフェイスを実装する
public class Character implements Drawable, Serializable {
    private String name;

    public Character(String name) {
        this.name = name;
    }

    // Drawableインターフェイスのメソッドの実装
    @Override
    public void draw() {
        System.out.println(this.name + "を描画します。");
    }

    // Serializableインターフェイスのメソッドの実装
    @Override
    public void saveToFile(String filename) {
        System.out.println(this.name + "のデータをファイル " + filename + " に保存します。");
    }
}