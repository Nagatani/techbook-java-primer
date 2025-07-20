/**
 * リスト5-4
 * WordDocumentクラス（重複コードの例）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (338行目)
 */
public class WordDocument {
    private String filename;     // ①
    private String content;      // ①
    private long fileSize;       // ①
    
    public void open() {         // ②
        System.out.println(filename + " を開いています");
    }
    
    public void save() {         // ③
        fileSize = content.length();
        System.out.println(filename + " を保存しました: " + fileSize + "バイト");
    }
    
    public void close() {        // ④
        content = null;
        System.out.println(filename + " を閉じました");
    }
}